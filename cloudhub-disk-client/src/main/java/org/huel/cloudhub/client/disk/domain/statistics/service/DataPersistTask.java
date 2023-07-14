package org.huel.cloudhub.client.disk.domain.statistics.service;

import org.huel.cloudhub.client.disk.domain.statistics.Statistics;
import org.huel.cloudhub.client.disk.domain.statistics.repository.StatisticsRepository;
import org.huel.cloudhub.client.disk.jobs.JobEvent;
import org.huel.cloudhub.client.disk.jobs.JobRegistry;
import org.huel.cloudhub.client.disk.jobs.JobTask;
import org.huel.cloudhub.client.disk.jobs.trigger.TimeJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
@Service
public class DataPersistTask implements JobTask {
    private static final Logger logger = LoggerFactory.getLogger(DataPersistTask.class);

    private final StatisticsRepository statisticsRepository;
    private final List<StatisticsPersistable> statisticsPersistables;

    private final Map<String, Statistics> statisticsByKey =
            new ConcurrentHashMap<>();
    private final Map<String, Long> lastVersionByKey =
            new ConcurrentHashMap<>();

    public DataPersistTask(JobRegistry jobRegistry,
                           StatisticsRepository statisticsRepository,
                           List<StatisticsPersistable> statisticsPersistables) {
        this.statisticsRepository = statisticsRepository;
        this.statisticsPersistables = statisticsPersistables;
        jobRegistry.register(
                this,
                TimeJobTrigger.of("0 0/5 * * * ?")
        );
        jobRegistry.register(
                this,
                TimeJobTrigger.of(System.currentTimeMillis() + 1000 * 10)
        );
    }


    @Override
    public void execute(JobEvent jobEvent) {
        statisticsPersistables.forEach(this::tryPersist);
    }

    private void tryPersist(
            StatisticsPersistable statisticsPersistable) {
        List<String> keys =
                statisticsPersistable.getStatisticsKeys();
        if (keys.isEmpty()) {
            return;
        }
        keys.forEach(key -> persistOfKey(statisticsPersistable, key));
    }

    private void persistOfKey(StatisticsPersistable statisticsPersistable,
                              String statisticsKey) {
        long version = statisticsPersistable.getStatisticsVersion();
        if (lastVersionByKey.getOrDefault(statisticsKey, -1L)
                == version) {
            return;
        }
        Map<String, String> statisticsMap =
                statisticsPersistable.getStatistics(statisticsKey);
        if (statisticsMap == null || statisticsMap.isEmpty()) {
            logger.debug("StatisticsPersistable {}(key={}) return empty statistics, rescan.",
                    statisticsPersistable.getClass().getName(), statisticsKey);
            statisticsPersistable.rescanStatistics();
            return;
        }

        lastVersionByKey.put(statisticsKey, version);
        Statistics statistics = getByKey(statisticsKey);
        if (StatHelper.equalsStatistics(statistics.getValue(),
                statisticsMap)) {
            return;
        }
        Statistics updated = statistics.toBuilder()
                .setValue(statisticsMap)
                .build();
        statisticsByKey.put(statisticsKey, updated);
        statisticsRepository.update(updated);

        logger.debug("Persisted statistics: {}, version: {}",
                statisticsKey, version);
    }

    private Statistics getByKey(String statisticsKey) {
        Statistics statistics = statisticsByKey.get(statisticsKey);
        if (statistics != null) {
            return statistics;
        }
        Statistics queried =
                statisticsRepository.getByKey(statisticsKey);
        if (queried == null) {
            queried = buildNewOne(statisticsKey);
        }
        statisticsByKey.put(statisticsKey, queried);
        return queried;
    }

    private Statistics buildNewOne(String key) {
        Statistics.Builder builder = Statistics.builder()
                .setKey(key)
                .setValue(new ConcurrentHashMap<>());
        long id = statisticsRepository.insert(builder.build());
        return builder.setId(id).build();
    }
}
