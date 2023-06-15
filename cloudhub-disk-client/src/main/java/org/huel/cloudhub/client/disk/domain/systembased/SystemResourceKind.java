package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public enum SystemResourceKind {
    FILE,
    FOLDER,
    LINK,
    STORAGE_PERMISSION,
    VERSIONED_FILE,
    VERSIONED_FOLDER,
    STORAGE_SHARE,
    TAG,
    TAG_GROUP,
    USER,
    USER_GROUP("group"),
    ORGANIZATION,
    STORAGE_USER_PERMISSION,
    ;

    private final String alias;

    SystemResourceKind() {
        alias = null;
    }

    SystemResourceKind(String alias) {
        this.alias = alias;
    }


    public interface Kind {
        SystemResourceKind getSystemResourceKind();
    }

    public static SystemResourceKind from(String nameIgnoreCase) {
        if (nameIgnoreCase == null || nameIgnoreCase.isBlank()) {
            return null;
        }
        for (SystemResourceKind value : values()) {
            if (value.name().equalsIgnoreCase(nameIgnoreCase)) {
                return value;
            }
            if (value.alias == null) {
                continue;
            }
            if (value.alias.equalsIgnoreCase(nameIgnoreCase)) {
                return value;
            }
        }
        return null;
    }
}
