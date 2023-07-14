package org.huel.cloudhub.client.disk.domain.statistics.service;

import java.util.Map;

/**
 * @author RollW
 */
public final class StatHelper {
    public static boolean equalsStatistics(Map<String, String> first,
                                           Map<String, String> second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.size() != second.size()) {
            return false;
        }
        for (Map.Entry<String, String> entry : first.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String secondValue = second.get(key);
            if (value == null) {
                if (secondValue != null) {
                    return false;
                }
            } else {
                if (!value.equals(secondValue)) {
                    return false;
                }
            }
        }
        return true;
    }


    private StatHelper() {
    }
}
