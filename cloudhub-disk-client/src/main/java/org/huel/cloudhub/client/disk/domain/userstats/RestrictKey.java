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

    public long toValue(long strictValue) {
        return toValue.apply(strictValue);
    }

    public long toValue(String strictValue) {
        return toValue.apply(Long.parseLong(strictValue));
    }
}
