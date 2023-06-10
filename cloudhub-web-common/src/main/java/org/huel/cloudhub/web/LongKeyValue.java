package org.huel.cloudhub.web;

import space.lingu.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public record LongKeyValue(
        String key,
        long value
) {
    public static List<LongKeyValue> from(@NonNull Map<String, Long> map) {
        return map.entrySet().stream()
                .map(entry -> new LongKeyValue(entry.getKey(), entry.getValue()))
                .toList();
    }

    public static Map<String, Long> to(@NonNull List<LongKeyValue> keyValues) {
        return keyValues.stream()
                .collect(HashMap::new,
                        (map, keyValue) -> map.put(keyValue.key(), keyValue.value()),
                        Map::putAll
                );
    }
}
