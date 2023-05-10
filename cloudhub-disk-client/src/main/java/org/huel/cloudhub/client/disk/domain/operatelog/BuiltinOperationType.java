package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

import java.text.MessageFormat;

/**
 * @author RollW
 */
public enum BuiltinOperationType implements OperateType, OperateTypeFinder {
    CREATE_FILE(-1L, Action.CREATE, "创建文件", "创建了 {1} 文件", SystemResourceKind.FILE),
    CREATE_FOLDER(-2L, Action.CREATE, "创建文件夹", "创建了 {1} 文件夹", SystemResourceKind.FOLDER),
    CREATE_LINK(-3L, Action.CREATE, "创建链接", "创建了 {1} 链接", SystemResourceKind.LINK),
    CREATE_VERSIONED_FILE(-4L, Action.CREATE, "创建版本化文件", "创建版本化文件", SystemResourceKind.VERSIONED_FILE),
    CREATE_VERSIONED_FOLDER(-5L, Action.CREATE, "创建版本化文件夹", "创建版本化文件夹", SystemResourceKind.VERSIONED_FOLDER),
    CREATE_STORAGE_PERMISSION(-6L, Action.CREATE, "创建存储权限", "创建存储权限", SystemResourceKind.STORAGE_PERMISSION),
    CREATE_TAG(-7L, Action.CREATE, "创建标签", "创建标签", SystemResourceKind.TAG),
    CREATE_TAG_GROUP(-8L, Action.CREATE, "创建标签组", "创建标签组", SystemResourceKind.TAG_GROUP),
    CREATE_STORAGE_SHARE(-9L, Action.CREATE, "创建存储分享", "创建存储分享", SystemResourceKind.STORAGE_SHARE),

    UPDATE_FILE(-12L, Action.UPDATE, "更新文件", "更新文件", SystemResourceKind.FILE),
    UPDATE_FOLDER(-13L, Action.UPDATE, "更新文件夹", "更新文件夹", SystemResourceKind.FOLDER),
    UPDATE_STORAGE_PERMISSION(-14L, Action.UPDATE, "更新存储权限", "更新存储权限", SystemResourceKind.STORAGE_PERMISSION),
    UPDATE_TAG(-15L, Action.UPDATE, "更新标签", "更新标签", SystemResourceKind.TAG),
    UPDATE_TAG_GROUP(-16L, Action.UPDATE, "更新标签组", "更新标签组", SystemResourceKind.TAG_GROUP),
    UPDATE_STORAGE_SHARE(-17L, Action.UPDATE, "更新存储分享", "更新存储分享", SystemResourceKind.STORAGE_SHARE),



    DELETE_FILE(-20L, Action.DELETE, "删除文件", "删除了 {1} 文件", SystemResourceKind.FILE),
    ;
    private final long id;
    private final Action action;
    private final String name;
    private final String description;
    private final SystemResourceKind systemResourceKind;

    BuiltinOperationType(long id, Action action, String name,
                         String description,
                         SystemResourceKind systemResourceKind) {
        this.id = id;
        this.action = action;
        this.name = name;
        this.description = description;
        this.systemResourceKind = systemResourceKind;
    }

    @Override
    public long getTypeId() {
        return id;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription(Object... args) {
        return MessageFormat.format(description, args);
    }

    @Override
    public String getDescriptionTemplate() {
        return description;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return systemResourceKind;
    }

    @Override
    public OperateType getOperateType(long id) {
        if (id >= 0) {
            return null;
        }
        if (id >= -values().length) {
            BuiltinOperationType type = values()[-(int) id];
            if (type != null && type.id == id) {
                return type;
            }
        }
        for (BuiltinOperationType value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return null;
    }

    public static OperateTypeFinder getFinderInstance() {
        return BuiltinOperationType.CREATE_FILE;
    }
}
