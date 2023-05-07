package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.client.disk.domain.share.ShareService;

import java.time.Duration;

/**
 * @author RollW
 */
public record ShareCreateRequest(
        int time,
        String password
) {
    public static final int TIME_1_DAY = 1;
    public static final int TIME_7_DAY = 2;
    public static final int TIME_30_DAY = 3;
    public static final int TIME_INFINITE = 4;

    public Duration toDuration() {
        return switch (time) {
            case TIME_1_DAY -> ShareService.DAYS_1;
            case TIME_7_DAY -> ShareService.DAYS_7;
            case TIME_30_DAY -> ShareService.DAYS_30;
            case TIME_INFINITE -> ShareService.INFINITE;
            default -> throw new ParameterFailedException("time");
        };
    }

}
