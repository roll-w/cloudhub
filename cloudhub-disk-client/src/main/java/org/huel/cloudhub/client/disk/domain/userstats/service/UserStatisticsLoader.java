package org.huel.cloudhub.client.disk.domain.userstats.service;

import org.huel.cloudhub.client.disk.domain.userstats.UserStatisticsService;
import org.huel.cloudhub.client.disk.domain.userstats.repository.UserStatisticsRepository;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserStatisticsLoader {
    private final UserStatisticsService userStatisticsService;
    private final UserStatisticsRepository userStatisticsRepository;

    public UserStatisticsLoader(UserStatisticsService userStatisticsService,
                                UserStatisticsRepository userStatisticsRepository) {
        this.userStatisticsService = userStatisticsService;
        this.userStatisticsRepository = userStatisticsRepository;
    }

    private void triggerOnInitialized() {
        // needs to confirm all statistics has been calculated on
        // every user, if not, it needs to be a rescan and calculate
        // all statistics.

    }
}
