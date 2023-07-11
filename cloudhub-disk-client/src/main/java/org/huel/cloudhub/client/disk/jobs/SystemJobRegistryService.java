package org.huel.cloudhub.client.disk.jobs;

import org.huel.cloudhub.client.disk.jobs.executor.JavaJobExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Service
public class SystemJobRegistryService implements JobRegistry {
    private final Map<String, Integer> jobRegistryMap = new HashMap<>();
    private final Map<String, JobRegistryPoint> jobRegistryPointMap =
            new HashMap<>();
    private final Map<String, JobRegistryPoint> stoppedJobRegistryPointMap =
            new HashMap<>();
    private final JobExecutor jobExecutor;

    public SystemJobRegistryService() {
        this.jobExecutor = new JavaJobExecutor(
                new ThreadPoolExecutor(
                        10, 20,
                        20L,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                )
        );
    }

    @Override
    public String register(JobTask jobTask, JobTrigger jobTrigger) {
        String jobId = nameOfJob(jobTask.getClass().getSimpleName());
        jobTrigger.setJobExecutor(jobExecutor);
        jobTrigger.setJobTask(jobTask);
        JobRegistryPoint jobRegistryPoint = new SimpleJobRegistryPoint(
                jobTask, jobTrigger, jobId);
        jobRegistryPointMap.put(jobId, jobRegistryPoint);
        jobTrigger.start();
        return jobId;
    }

    private void reloadExecutor() {
        for (JobRegistryPoint jobRegistryPoint :
                jobRegistryPointMap.values()) {
            jobRegistryPoint.getJobTrigger()
                    .setJobExecutor(jobExecutor);
        }
    }

    private String nameOfJob(String jobName) {
        int serialNumber =
                jobRegistryMap.getOrDefault(jobName, 0) + 1;
        jobRegistryMap.put(jobName, serialNumber);
        return jobName + "-" + serialNumber;
    }

    @Override
    public void unregister(String jobId) {
        JobRegistryPoint jobRegistryPoint = jobRegistryPointMap.get(jobId);
        if (jobRegistryPoint == null) {
            return;
        }
        jobRegistryPoint.getJobTrigger().stop();
        stoppedJobRegistryPointMap.put(jobId, jobRegistryPoint);
        jobRegistryPointMap.remove(jobId);
    }

    @Override
    public JobRegistryPoint getJobRegistryPoint(String jobId) {
        return jobRegistryPointMap.get(jobId);
    }

    @Override
    public List<JobRegistryPoint> getJobRegistryPoints() {
        return jobRegistryPointMap
                .values()
                .stream()
                .toList();
    }
}
