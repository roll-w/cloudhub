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
    TAG,
    TAG_GROUP,
    USER_SETTING,
    GROUP_SETTING,
    ORGANIZATION_SETTING,

    ;

    public interface Kind {
        SystemResourceKind getSystemResourceKind();
    }
}
