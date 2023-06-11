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
    CREATE_TAG(-7L, Action.CREATE, "创建标签", "创建 {1} 标签", SystemResourceKind.TAG),
    CREATE_TAG_GROUP(-8L, Action.CREATE, "创建标签组", "创建 {1} 标签组", SystemResourceKind.TAG_GROUP),
    CREATE_STORAGE_SHARE(-9L, Action.CREATE, "创建存储分享", "创建 {1} 存储分享", SystemResourceKind.STORAGE_SHARE),

    UPDATE_FILE(-101L, Action.UPDATE, "更新文件", "更新文件", SystemResourceKind.FILE),
    UPDATE_FOLDER(-111L, Action.UPDATE, "更新文件夹", "更新文件夹", SystemResourceKind.FOLDER),
    UPDATE_STORAGE_PERMISSION(-131L, Action.UPDATE, "更新存储权限", "更新存储权限", SystemResourceKind.STORAGE_PERMISSION),
    UPDATE_TAG(-171L, Action.UPDATE, "更新标签", "更新标签", SystemResourceKind.TAG),
    UPDATE_TAG_GROUP(-181L, Action.UPDATE, "更新标签组", "更新标签组", SystemResourceKind.TAG_GROUP),
    UPDATE_STORAGE_SHARE(-161L, Action.UPDATE, "更新存储分享", "更新存储分享", SystemResourceKind.STORAGE_SHARE),
    UPDATE_USER_SETTING(-191L, Action.UPDATE, "更新用户存储", "更新用户存储", SystemResourceKind.USER),

    // id = (1, Action ordinal, ResourceKind ordinal)

    DELETE_FILE(-104L, Action.DELETE, "删除文件", "删除了 {1} 文件", SystemResourceKind.FILE),
    DELETE_FOLDER(-114L, Action.DELETE, "删除文件夹", "删除了 {1} 文件夹", SystemResourceKind.FOLDER),
    DELETE_LINK(-124L, Action.DELETE, "删除链接", "删除了 {1} 链接", SystemResourceKind.LINK),
    DELETE_STORAGE_PERMISSION(-134L, Action.DELETE, "删除存储权限", "删除存储权限", SystemResourceKind.STORAGE_PERMISSION),
    DELETE_VERSIONED_FILE(-144L, Action.DELETE, "删除版本化文件", "删除版本化文件", SystemResourceKind.VERSIONED_FILE),
    DELETE_VERSIONED_FOLDER(-154L, Action.DELETE, "删除版本化文件夹", "删除版本化文件夹", SystemResourceKind.VERSIONED_FOLDER),
    DELETE_STORAGE_SHARE(-164L, Action.DELETE, "删除存储分享", "删除存储分享", SystemResourceKind.STORAGE_SHARE),
    DELETE_TAG(-174L, Action.DELETE, "删除标签", "删除标签", SystemResourceKind.TAG),
    DELETE_TAG_GROUP(-184L, Action.DELETE, "删除标签组", "删除标签组", SystemResourceKind.TAG_GROUP),

    MOVE_FILE(-105L, Action.MOVE, "移动文件", "移动了 {1} 文件", SystemResourceKind.FILE),
    MOVE_FOLDER(-115L, Action.MOVE, "移动文件夹", "移动了 {1} 文件夹", SystemResourceKind.FOLDER),
    MOVE_LINK(-125L, Action.MOVE, "移动链接", "移动了 {1} 链接", SystemResourceKind.LINK),
    MOVE_TAG(-175L, Action.MOVE, "移动标签", "移动标签", SystemResourceKind.TAG),
    MOVE_TAG_GROUP(-185L, Action.MOVE, "移动标签组", "移动标签组", SystemResourceKind.TAG_GROUP),

    COPY_FILE(-106L, Action.COPY, "复制文件", "复制了 {1} 文件", SystemResourceKind.FILE),
    COPY_FOLDER(-116L, Action.COPY, "复制文件夹", "复制了 {1} 文件夹", SystemResourceKind.FOLDER),
    COPY_LINK(-126L, Action.COPY, "复制链接", "复制了 {1} 链接", SystemResourceKind.LINK),

    RENAME_FILE(-107L, Action.RENAME, "重命名文件", "从 {0} 重命名了 {1} 文件", SystemResourceKind.FILE),
    RENAME_FOLDER(-117L, Action.RENAME, "重命名文件夹", "从 {0} 重命名了 {1} 文件夹", SystemResourceKind.FOLDER),
    RENAME_LINK(-127L, Action.RENAME, "重命名链接", "从 {0} 重命名了 {1} 链接", SystemResourceKind.LINK),

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
