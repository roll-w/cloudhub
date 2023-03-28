package org.huel.cloudhub.client.disk.domain.userstorage;

/**
 * @author RollW
 */
public enum OwnerType {
    USER,
    /**
     * 代表用户的组/空间
     */
    SPACE,
    ORGANIZATION,
    GROUP,
    ;


    public static OwnerType from(String name) {
        for (OwnerType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

}
