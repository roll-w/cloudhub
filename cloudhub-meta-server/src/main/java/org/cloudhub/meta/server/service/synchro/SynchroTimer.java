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

package org.cloudhub.meta.server.service.synchro;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author RollW
 */
public class SynchroTimer {
    private final long countDown;
    private final Runnable task;
    private Timer timer;

    public SynchroTimer(long countDown,
                        Runnable task) {
        this.countDown = countDown;
        this.task = task;
        resetTimer();
    }

    public void reset() {
        resetTimer();
    }

    public void stop() {
        timer.cancel();
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, countDown);
    }

    private void resetTimer() {
        timer = new Timer("SynchroTimer");
    }
}
