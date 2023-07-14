package org.huel.cloudhub.client.disk.domain.statistics.service;

import space.lingu.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public interface StatisticsPersistable {
    @NonNull
    List<String> getStatisticsKeys();

    void rescanStatistics();

    Map<String, String> getStatistics(String key);

    long getStatisticsVersion();
}
