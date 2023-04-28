package org.huel.cloudhub.client.disk.domain.user;

/**
 * 合法的用户类型
 *
 * @author RollW
 */
public enum LegalUserType {
    USER,
    /**
     * 代表用户的组/空间
     */
    SPACE,
    ORGANIZATION,
    GROUP,
    ;


    public static LegalUserType from(String name) {
        for (LegalUserType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

}
