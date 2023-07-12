package org.huel.cloudhub.client.disk.controller.job;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.jobs.JobRegistry;
import org.huel.cloudhub.client.disk.jobs.JobRegistryPoint;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class SystemJobController {
    private final JobRegistry jobRegistry;

    public SystemJobController(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    @GetMapping("/jobs")
    public HttpResponseEntity<List<JobVo>> getJobList() {
        List<JobRegistryPoint> registryPoints =
                jobRegistry.getJobRegistryPoints();

        return HttpResponseEntity.success(
                registryPoints.stream()
                        .map(JobVo::of)
                        .toList()
        );
    }
}
