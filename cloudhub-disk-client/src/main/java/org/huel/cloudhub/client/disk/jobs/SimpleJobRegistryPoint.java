package org.huel.cloudhub.client.disk.jobs;

/**
 * @author RollW
 */
public record SimpleJobRegistryPoint(
        JobTask jobTask,
        JobTrigger jobTrigger,
        String jobId
) implements JobRegistryPoint {
    @Override
    public JobTask getJobTask() {
        return jobTask;
    }

    @Override
    public JobTrigger getJobTrigger() {
        return jobTrigger;
    }

    @Override
    public String getJobId() {
        return jobId;
    }
}
