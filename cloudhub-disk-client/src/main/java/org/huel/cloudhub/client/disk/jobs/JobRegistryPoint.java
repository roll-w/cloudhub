package org.huel.cloudhub.client.disk.jobs;

/**
 * @author RollW
 */
public interface JobRegistryPoint {
    JobTask getJobTask();

    JobTrigger getJobTrigger();

    String getJobId();
}
