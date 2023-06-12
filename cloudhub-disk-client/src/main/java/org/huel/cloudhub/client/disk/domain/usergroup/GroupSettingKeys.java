package org.huel.cloudhub.client.disk.domain.usergroup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public final class GroupSettingKeys {
    // by mb
    public static final String GROUP_QUOTA = "group_quota";

    public static final String GROUP_FILE_NUM_LIMIT = "group_file-number-limit";


    static final String NO_LIMIT = "-1";

    private static final Map<String, String> DEFAULT_GROUP_SETTINGS = new HashMap<>();
    static {
        DEFAULT_GROUP_SETTINGS.put(GROUP_QUOTA, "10240");
        DEFAULT_GROUP_SETTINGS.put(GROUP_FILE_NUM_LIMIT, NO_LIMIT);
    }

    public static final UserGroup DEFAULT = new UserGroup(
            0L, "default", "default group",
            DEFAULT_GROUP_SETTINGS,
            0, 0, false
    );

    private GroupSettingKeys() {
    }
}
