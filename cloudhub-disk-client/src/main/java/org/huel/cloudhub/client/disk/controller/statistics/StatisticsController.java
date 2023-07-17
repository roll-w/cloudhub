package org.huel.cloudhub.client.disk.controller.statistics;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.statistics.DatedData;
import org.huel.cloudhub.client.disk.domain.statistics.StatisticsService;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@AdminApi
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics/{statisticsKey}")
    public HttpResponseEntity<Map<String, Object>> getStatistics(
            @PathVariable String statisticsKey) {

        Map<String, Object> stats =
                statisticsService.getStatistics(statisticsKey);
        return HttpResponseEntity.success(stats);
    }

    @GetMapping("/statistics/{statisticsKey}/at")
    public HttpResponseEntity<DatedData> getStatisticsAtDate(
            @PathVariable String statisticsKey,
            @RequestParam LocalDate date) {
        DatedData datedData =
                statisticsService.getStatistics(statisticsKey, date);
        return HttpResponseEntity.success(datedData);
    }

    @GetMapping("/statistics/{statisticsKey}/between")
    public HttpResponseEntity<List<DatedData>> getStatisticsBetweenDate(
            @PathVariable String statisticsKey,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        List<DatedData> datedData =
                statisticsService.getStatistics(statisticsKey, start, end);
        return HttpResponseEntity.success(datedData);
    }
}
