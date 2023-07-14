package org.huel.cloudhub.client.disk.domain.statistics;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author RollW
 */
public record DatedData(
        Map<String, Object> data,
        LocalDate date
) {
}
