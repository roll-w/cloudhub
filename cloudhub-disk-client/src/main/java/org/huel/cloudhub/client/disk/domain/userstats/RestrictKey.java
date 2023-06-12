package org.huel.cloudhub.client.disk.domain.userstats;

import java.util.function.Function;

/**
 * @author RollW
 */
public record RestrictKey(
        String userKey,
        String restrictKey,
        Function<Long, Long> toValue
) {
    public RestrictKey(String userKey, String restrictKey) {
        this(userKey, restrictKey, Function.identity());
    }

    public String getKey() {
        return userKey;
    }

    public String getRestrictKey() {
        return restrictKey;
    }

    public long toValue(long restrictValue) {
        return toValue.apply(restrictValue);
    }

    public long toValue(String restrictValue) {
        return toValue.apply(Long.parseLong(restrictValue));
    }
}
