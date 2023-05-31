package org.huel.cloudhub.web;

import space.lingu.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public record KeyValue(
        String key,
        String value
) {

    public static List<KeyValue> from(@NonNull Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                .toList();
    }

    public static Map<String, String> to(@NonNull List<KeyValue> keyValues) {
        return keyValues.stream()
                .collect(HashMap::new,
                        (map, keyValue) ->
                                map.put(keyValue.key(), keyValue.value()),
                        Map::putAll
                );
    }

}
