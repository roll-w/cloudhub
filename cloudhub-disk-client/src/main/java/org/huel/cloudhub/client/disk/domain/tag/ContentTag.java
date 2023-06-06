package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

import java.util.List;

/**
 * @author RollW
 */
@DataTable(name = "content_tag")
public class ContentTag implements SystemResource, DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "keywords", dataType = SQLDataType.LONGTEXT)
    private final List<TagKeyword> keywords;

    @DataColumn(name = "description")
    private final String description;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public ContentTag(Long id, String name,
                      List<TagKeyword> keywords,
                      String description,
                      long createTime,
                      long updateTime,
                      boolean deleted) {
        this.id = id;
        this.name = name;
        this.keywords = keywords;
        this.description = description;
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

    public List<TagKeyword> getKeywords() {
        return keywords;
    }

    public String getDescription() {
        return description;
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

    @Override
    public long getResourceId() {
        return getId();
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.TAG;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private List<TagKeyword> keywords;
        private String description;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        public Builder() {
        }

        public Builder(ContentTag contentTag) {
            this.id = contentTag.id;
            this.name = contentTag.name;
            this.keywords = contentTag.keywords;
            this.description = contentTag.description;
            this.createTime = contentTag.createTime;
            this.updateTime = contentTag.updateTime;
            this.deleted = contentTag.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name is null or blank");
            }
            this.name = name;
            return this;
        }

        public Builder setKeywords(List<TagKeyword> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
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

        public ContentTag build() {
            return new ContentTag(id, name, keywords,
                    description, createTime,
                    updateTime, deleted);
        }
    }
}
