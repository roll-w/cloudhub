package org.huel.cloudhub.client.disk.domain.userstorage;

/**
 * @author RollW
 */
public enum AttributeField {
    NAME("name", String.class),
    TYPE("type", String.class),
    SIZE("size", long.class),
    CREATE_TIME("createTime", long.class),
    UPDATE_TIME("updateTime", long.class),
    ;

    private final String name;
    private final Class<?> type;

    AttributeField(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
