package org.huel.cloudhub.client.disk.jobs;

/**
 * @author RollW
 */
public interface JobTrigger {
    void start();

    void stop();

    JobTask getJobTask();

    void setJobTask(JobTask jobTask);

    TriggerType getTriggerType();
}
