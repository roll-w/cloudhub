package org.huel.cloudhub.client.disk.jobs.executor;

import org.huel.cloudhub.client.disk.jobs.JobEvent;
import org.huel.cloudhub.client.disk.jobs.JobExecutor;
import org.huel.cloudhub.client.disk.jobs.JobTask;

import java.util.concurrent.Executor;

/**
 * @author RollW
 */
public class JavaJobExecutor implements JobExecutor {
    private final Executor executor;

    public JavaJobExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(JobTask jobTask, JobEvent jobEvent) {
        executor.execute(() -> jobTask.execute(jobEvent));
    }
}
