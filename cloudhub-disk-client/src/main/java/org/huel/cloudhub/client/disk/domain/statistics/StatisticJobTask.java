package org.huel.cloudhub.client.disk.domain.statistics;

import org.huel.cloudhub.client.disk.jobs.JobEvent;
import org.huel.cloudhub.client.disk.jobs.JobTask;
import space.lingu.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public interface StatisticJobTask extends JobTask {
    @Override
    void execute(JobEvent jobEvent);

    @NonNull
    List<String> getStatisticsKeys();

    void rescanStatistics();

    @NonNull
    Map<String, Object> getStatistics(String key,
                                      Map<String, String> rawStatistics);
}
