package org.huel.cloudhub.client.disk.domain.statistics.service;

import org.huel.cloudhub.client.disk.domain.statistics.*;
import org.huel.cloudhub.client.disk.domain.statistics.repository.DatedStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.statistics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
                .filter(statisticJobTask -> statisticJobTask.getStatisticsKey().equals(statisticsKey))
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
        return statisticJobTask.getStatistics(statistics.getValue());
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
                    statisticJobTask.getStatistics(datedStatistics.getValue()),
                    datedStatistics.getDate()
            );
        }
        DatedStatistics latestDatedStatistics =
                datedStatisticsRepository.getLatestOfKey(statisticsKey);
        if (latestDatedStatistics != null) {
            return new DatedData(
                    statisticJobTask.getStatistics(latestDatedStatistics.getValue()),
                    latestDatedStatistics.getDate()
            );
        }
        return null;
    }

    @Override
    public List<DatedData> getStatistics(String statisticsKey,
                                         LocalDate from,
                                         LocalDate to) {
        return null;
    }
}
