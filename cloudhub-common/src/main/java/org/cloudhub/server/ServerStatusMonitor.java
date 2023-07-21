/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.server;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class ServerStatusMonitor {
    private static final int DEFAULT_LIMIT = 100;
    private static final int DEFAULT_FREQUENCY = 1000;// ms

    private int mQueueLimit;
    private int mFrequency;

    private final Queue<ServerHostInfo> mServerHostInfoDeque = new ArrayDeque<>();
    private final ServerHostInfo serverHostInfo;

    public ServerStatusMonitor(String monitorDiskPath) {
        mQueueLimit = DEFAULT_LIMIT;
        mFrequency = DEFAULT_FREQUENCY;
        serverHostInfo = ServerHostInfo.load(monitorDiskPath);
    }

    public void setRecordFrequency(int milliSeconds) {
        mFrequency = milliSeconds;
    }

    public void setLimit(int limit) {
        mQueueLimit = limit;
    }

    private void record() {
        pushQueue(serverHostInfo.reload().fork());
    }

    private void pushQueue(ServerHostInfo info) {
        while (mServerHostInfoDeque.size() >= mQueueLimit) {
            mServerHostInfoDeque.remove();
        }
        mServerHostInfoDeque.add(info);
    }

    public void startMonitor() {
        threadPool.scheduleAtFixedRate(
                this::record,
                0,
                mFrequency,
                TimeUnit.MILLISECONDS);
    }

    public ServerHostInfo getLatest() {
        return mServerHostInfoDeque.peek();
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public Collection<ServerHostInfo> getRecent(int count) {
        int size = mServerHostInfoDeque.size();
        if (count >= size) {
            return Collections.unmodifiableCollection(mServerHostInfoDeque);
        }

        return new ArrayList<>(mServerHostInfoDeque).subList(size - count - 1, size - 1);
    }

    private final ScheduledExecutorService threadPool =
            Executors.newScheduledThreadPool(1);
}
