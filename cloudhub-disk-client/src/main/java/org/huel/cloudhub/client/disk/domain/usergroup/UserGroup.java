package org.huel.cloudhub.client.disk.domain.usergroup;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@DataTable(name = "user_group")
public class UserGroup implements DataItem, SystemResource {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "description")
    private final String description;

    @DataColumn(name = "settings", dataType = SQLDataType.LONGTEXT)
    private final Map<String, String> settings;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public UserGroup(Long id, String name, String description,
                     Map<String, String> settings,
                     long createTime, long updateTime, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.settings = settings;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public long getResourceId() {
        return getId();
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER_GROUP;
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String description;
        private Map<String, String> settings;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        private Builder() {
            this.settings = new HashMap<>();
        }

        private Builder(UserGroup usergroup) {
            this.id = usergroup.id;
            this.name = usergroup.name;
            this.description = usergroup.description;
            if (usergroup.settings == null) {
                this.settings = new HashMap<>();
            } else {
                this.settings = new HashMap<>(usergroup.settings);
            }
            this.createTime = usergroup.createTime;
            this.updateTime = usergroup.updateTime;
            this.deleted = usergroup.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSettings(Map<String, String> settings) {
            if (settings == null) {
                this.settings = new HashMap<>();
                return this;
            }

            this.settings = settings;
            return this;
        }

        public Builder setSetting(String key, String value) {
            if (this.settings == null) {
                this.settings = new HashMap<>();
            }

            this.settings.put(key, value);
            return this;
        }

        public Builder setCreateTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public UserGroup build() {
            return new UserGroup(id, name, description,
                    settings, createTime, updateTime, deleted);
        }
    }
}
