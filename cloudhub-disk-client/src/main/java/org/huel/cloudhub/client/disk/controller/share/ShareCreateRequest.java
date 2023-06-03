package org.huel.cloudhub.client.disk.controller.share;

import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.client.disk.domain.share.ShareService;

import java.time.Duration;

/**
 * @author RollW
 */
public record ShareCreateRequest(
        int time,
        String password,
        int type
) {
    public static final int TIME_1_DAY = -1;
    public static final int TIME_7_DAY = -7;
    public static final int TIME_30_DAY = -30;
    public static final int TIME_INFINITE = 0;

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;

    public Duration toDuration() {
        return switch (time) {
            case TIME_1_DAY -> ShareService.DAYS_1;
            case TIME_7_DAY -> ShareService.DAYS_7;
            case TIME_30_DAY -> ShareService.DAYS_30;
            case TIME_INFINITE -> ShareService.INFINITE;
            // TODO: may supports custom time future
            default -> throw new ParameterFailedException("Not supports time param.");
        };
    }

}
