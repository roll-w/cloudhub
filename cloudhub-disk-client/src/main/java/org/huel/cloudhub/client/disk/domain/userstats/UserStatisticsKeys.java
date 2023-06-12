package org.huel.cloudhub.client.disk.domain.userstats;

import org.huel.cloudhub.client.disk.domain.usergroup.GroupSettingKeys;

import java.util.Map;

/**
 * @author RollW
 */
public final class UserStatisticsKeys {
    public static final String USER_STORAGE_COUNT = "user_storage_count";
    public static final String USER_STORAGE_USED = "user_storage_used";

    public static final long NO_LIMIT = -1;

    static final Map<String, RestrictKey> RESTRICT_KEYS = Map.of(
            USER_STORAGE_COUNT,
            new RestrictKey(USER_STORAGE_COUNT, GroupSettingKeys.GROUP_FILE_NUM_LIMIT),
            USER_STORAGE_USED,
            new RestrictKey(USER_STORAGE_USED, GroupSettingKeys.GROUP_QUOTA,
                    (strictValue) -> strictValue * 1024 * 1024)// mb to bytes
    );

    public static RestrictKey restrictKeyOf(String key) {
        return RESTRICT_KEYS.get(key);
    }

    private UserStatisticsKeys() {
    }
}
