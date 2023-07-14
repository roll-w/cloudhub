package org.huel.cloudhub.client.disk.domain.statistics.service;

import org.huel.cloudhub.client.disk.domain.statistics.*;
import org.huel.cloudhub.client.disk.domain.statistics.repository.DatedStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.statistics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author RollW
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final List<StatisticJobTask> statisticJobTasks;

    private final DatedStatisticsRepository datedStatisticsRepository;
    private final StatisticsRepository statisticsRepository;

    public StatisticsServiceImpl(List<StatisticJobTask> statisticJobTasks,
                                 DatedStatisticsRepository datedStatisticsRepository, StatisticsRepository statisticsRepository) {
        this.statisticJobTasks = statisticJobTasks;
        this.datedStatisticsRepository = datedStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
    }

    private StatisticJobTask findByStatisticsKey(String statisticsKey) {
        return statisticJobTasks.stream()
                .filter(statisticJobTask -> statisticJobTask.getStatisticsKeys().contains(statisticsKey))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No such statistics key: " + statisticsKey));
    }

    @Override
    public Map<String, Object> getStatistics(String statisticsKey) {
        StatisticJobTask statisticJobTask =
                findByStatisticsKey(statisticsKey);
        Statistics statistics = statisticsRepository
                .getByKey(statisticsKey);
        if (statistics == null) {
            return null;
        }
        return statisticJobTask.getStatistics(statistics.getKey(), statistics.getValue());
    }

    @Override
    public DatedData getStatistics(String statisticsKey,
                                   LocalDate date) {
        StatisticJobTask statisticJobTask =
                findByStatisticsKey(statisticsKey);
        DatedStatistics datedStatistics =
                datedStatisticsRepository.getByKeyAndDate(statisticsKey, date);
        if (datedStatistics != null) {
            return new DatedData(
                    statisticJobTask.getStatistics(statisticsKey, datedStatistics.getValue()),
                    datedStatistics.getDate()
            );
        }
        DatedStatistics latestDatedStatistics =
                datedStatisticsRepository.getLatestOfKey(statisticsKey, date);
        if (latestDatedStatistics != null) {
            return new DatedData(
                    statisticJobTask.getStatistics(statisticsKey, latestDatedStatistics.getValue()),
                    date
            );
        }
        return null;
    }

    @Override
    public List<DatedData> getStatistics(String statisticsKey,
                                         LocalDate from,
                                         LocalDate to) {
        List<DatedStatistics> datedStatisticsList =
                datedStatisticsRepository.getByKeyAndDateBetween(statisticsKey, from, to);
        if (!datedStatisticsList.isEmpty()) {
            return datedStatisticsList.stream()
                    .map(datedStatistics -> {
                        StatisticJobTask statisticJobTask =
                                findByStatisticsKey(statisticsKey);
                        return new DatedData(
                                statisticJobTask.getStatistics(statisticsKey, datedStatistics.getValue()),
                                datedStatistics.getDate()
                        );
                    })
                    .toList();
        }
        DatedStatistics latestDatedStatistics =
                datedStatisticsRepository.getLatestOfKey(statisticsKey);

        return Stream.of(latestDatedStatistics)
                .map(datedStatistics -> {
                    StatisticJobTask statisticJobTask =
                            findByStatisticsKey(statisticsKey);
                    return new DatedData(
                            statisticJobTask.getStatistics(statisticsKey, datedStatistics.getValue()),
                            datedStatistics.getDate()
                    );
                })
                .toList();
    }
}
