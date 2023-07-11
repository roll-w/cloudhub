package org.huel.cloudhub.client.disk.jobs;

import java.util.List;

/**
 * @author RollW
 */
public interface JobRegistry {
    /**
     * Register a job task and a job trigger.
     *
     * @param jobTask    job task
     * @param jobTrigger job trigger
     * @return job id
     */
    String register(JobTask jobTask, JobTrigger jobTrigger);

    /**
     * Unregister a job by job id.
     *
     * @param jobId job id
     */
    void unregister(String jobId);

    JobRegistryPoint getJobRegistryPoint(String jobId);

    List<JobRegistryPoint> getJobRegistryPoints();
}
