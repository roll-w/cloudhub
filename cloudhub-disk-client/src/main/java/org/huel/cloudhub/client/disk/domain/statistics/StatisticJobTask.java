package org.huel.cloudhub.client.disk.domain.statistics;

import org.huel.cloudhub.client.disk.jobs.JobEvent;
import org.huel.cloudhub.client.disk.jobs.JobTask;
import space.lingu.NonNull;

import java.util.Map;

/**
 * @author RollW
 */
public interface StatisticJobTask extends JobTask {
    @Override
    void execute(JobEvent jobEvent);

    @NonNull
    String getStatisticsKey();

    void rescanStatistics();

    /**
     */
    @NonNull
    Map<String, Object> getStatistics(Map<String, String> rawStatistics);
}
