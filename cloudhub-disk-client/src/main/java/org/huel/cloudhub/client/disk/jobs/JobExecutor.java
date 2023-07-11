package org.huel.cloudhub.client.disk.jobs;

/**
 * @author RollW
 */
public interface JobExecutor {
    void execute(JobTask jobTask, JobEvent jobEvent);
}
