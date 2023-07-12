package org.huel.cloudhub.client.disk.controller.job;

import org.huel.cloudhub.client.disk.jobs.JobRegistryPoint;
import org.huel.cloudhub.client.disk.jobs.JobStatus;

/**
 * @author RollW
 */
public record JobVo(
        String id,
        long lastExecuteTime,
        long nextExecuteTime,
        String taskType,
        String triggerType,
        JobStatus status
) {

    public static JobVo of(JobRegistryPoint registryPoint) {
        return new JobVo(
                registryPoint.getJobId(),
                registryPoint.getJobTrigger().lastExecuteTime(),
                registryPoint.getJobTrigger().nextExecuteTime(),
                registryPoint.getJobTask().getClass().getCanonicalName(),
                registryPoint.getJobTrigger().getName(),
                registryPoint.getJobTrigger().getJobStatus()
        );
    }
}
