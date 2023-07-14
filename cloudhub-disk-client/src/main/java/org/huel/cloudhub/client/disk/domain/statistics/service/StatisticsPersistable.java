package org.huel.cloudhub.client.disk.domain.statistics.service;

import space.lingu.NonNull;

import java.util.Map;

/**
 * @author RollW
 */
public interface StatisticsPersistable {
    @NonNull
    String getStatisticsKey();

    void rescanStatistics();

    Map<String, String> getStatistics();

    long getStatisticsVersion();
}
