package org.huel.cloudhub.client.disk.domain.statistics.service;

import org.huel.cloudhub.client.disk.domain.statistics.DatedStatistics;
import org.huel.cloudhub.client.disk.domain.statistics.Statistics;
import org.huel.cloudhub.client.disk.domain.statistics.repository.DatedStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.statistics.repository.StatisticsRepository;
import org.huel.cloudhub.client.disk.jobs.JobEvent;
import org.huel.cloudhub.client.disk.jobs.JobRegistry;
import org.huel.cloudhub.client.disk.jobs.JobTask;
import org.huel.cloudhub.client.disk.jobs.trigger.TimeJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class DataArchivingTask implements JobTask {
    private static final Logger logger = LoggerFactory.getLogger(DataArchivingTask.class);
    // 5 minutes
    /**
     * Delay to execute the task. Because of the
     * task executed at 4:00 am every day, but
     * the task has to record the data of the
     * day the application started, so the task
     * will be executed after the delay time
     * after starting the application.
     */
    private static final long DELAY = 1000 * 60 * 5;

    private final DatedStatisticsRepository datedStatisticsRepository;
    private final StatisticsRepository statisticsRepository;

    private final Map<String, DatedStatistics> latestStatisticsByKey =
            new HashMap<>();

    public DataArchivingTask(JobRegistry jobRegistry,
                             DatedStatisticsRepository datedStatisticsRepository,
                             StatisticsRepository statisticsRepository) {
        this.datedStatisticsRepository = datedStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
        jobRegistry.register(
                this,
                // execute at 4:00 am every day
                TimeJobTrigger.of("0 0 4 * * ?")
        );
        jobRegistry.register(
                this,
                TimeJobTrigger.of(System.currentTimeMillis() + DELAY)
        );
    }


    @Override
    public void execute(JobEvent jobEvent) {
        LocalDate localDate = LocalDate.now();

        logger.debug("Execute statistics data archiving task at {}",
                localDate
        );

        List<Statistics> statistics =
                statisticsRepository.get();
        statistics.forEach(stat -> persistOf(stat, localDate));
    }

    private void persistOf(Statistics statistics,
                           LocalDate localDate) {
        String statisticsKey = statistics.getKey();
        Map<String, String> value = statistics.getValue();
        if (value == null || value.isEmpty()) {
            return;
        }
        DatedStatistics datedStatistics = findLatestDatedStatistics(
                statistics, localDate
        );
        Map<String, String> latestValue = datedStatistics.getValue();
        if (StatHelper.equalsStatistics(value, latestValue)) {
            return;
        }
        DatedStatistics updated = datedStatistics.toBuilder()
                .setValue(new HashMap<>(value))
                .build();
        datedStatisticsRepository.update(updated);
        latestStatisticsByKey.put(statisticsKey, updated);
    }

    private DatedStatistics findLatestDatedStatistics(
            Statistics statistics, LocalDate localDate) {
        final String statisticsKey = statistics.getKey();
        DatedStatistics datedStatistics =
                latestStatisticsByKey.get(statisticsKey);
        if (datedStatistics != null) {
            return datedStatistics;
        }
        DatedStatistics queried =
                datedStatisticsRepository.getLatestOfKey(statisticsKey);

        if (queried == null) {
            Map<String, String> copiedValue =
                    new HashMap<>(statistics.getValue());
            DatedStatistics buildNewOne = DatedStatistics.builder()
                    .setKey(statisticsKey)
                    .setValue(copiedValue)
                    .setDate(localDate)
                    .build();
            long id = datedStatisticsRepository.insert(buildNewOne);
            DatedStatistics inserted = buildNewOne
                    .toBuilder()
                    .setId(id)
                    .build();
            latestStatisticsByKey.put(statisticsKey, inserted);
            return inserted;
        }
        return queried;
    }
}
