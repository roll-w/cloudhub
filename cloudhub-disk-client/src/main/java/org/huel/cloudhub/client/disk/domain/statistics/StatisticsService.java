package org.huel.cloudhub.client.disk.domain.statistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public interface StatisticsService {
    Map<String, Object> getStatistics(String statisticsKey);

    DatedData getStatistics(String statisticsKey,
                            LocalDate date);

    List<DatedData> getStatistics(String statisticsKey,
                                  LocalDate from, LocalDate to);
}
