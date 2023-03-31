package org.huel.cloudhub.meta.server.service.synchro;

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
