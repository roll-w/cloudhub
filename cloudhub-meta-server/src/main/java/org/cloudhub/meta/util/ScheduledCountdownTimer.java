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

package org.cloudhub.meta.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class ScheduledCountdownTimer {
    private final long countDown;
    private final Runnable task;

    public ScheduledCountdownTimer(long countDownInMillis,
                                   Runnable task) {
        this.countDown = countDownInMillis;
        this.task = task;
    }

    public void reset() {
    }

    public void stop() {
    }

    public void start() {
        SCHEDULED_POOL.schedule(
                task,
                countDown,
                TimeUnit.MILLISECONDS
        );
    }

    private static final ScheduledExecutorService SCHEDULED_POOL =
            Executors.newScheduledThreadPool(
                    Runtime.getRuntime().availableProcessors() * 2
            );

}
