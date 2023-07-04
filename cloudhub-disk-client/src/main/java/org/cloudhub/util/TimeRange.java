package org.cloudhub.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author RollW
 */
public record TimeRange(
        Long start,
        Long end
) {
    public static final TimeRange NULL = new TimeRange(null, null);

    public LocalDateTime startDateTime() {
        if (start == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(start),
                TimeZone.getDefault().toZoneId());
    }

    public LocalDateTime endDateTime() {
        if (end == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(end),
                TimeZone.getDefault().toZoneId());
    }

}
