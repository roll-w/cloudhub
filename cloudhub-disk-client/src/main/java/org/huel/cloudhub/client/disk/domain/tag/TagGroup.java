package org.huel.cloudhub.client.disk.domain.tag;

import space.lingu.Nullable;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "tag_group")
public class TagGroup {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "parent_id")
    @Nullable
    private final Long parentId;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "description")
    private final String description;

    @DataColumn(name = "tags")
    private final long[] tags;

    @DataColumn(name = "search_scope")
    private final KeywordSearchScope keywordSearchScope;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public TagGroup(Long id, @Nullable Long parentId,
                    String name, String description,
                    long[] tags,
                    KeywordSearchScope keywordSearchScope,
                    long createTime, long updateTime,
                    boolean deleted) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.keywordSearchScope = keywordSearchScope;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    @Nullable
    public Long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long[] getTags() {
        return tags;
    }

    public KeywordSearchScope getKeywordSearchScope() {
        return keywordSearchScope;
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

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long parentId;
        private String name;
        private String description;
        private long[] tags;
        private KeywordSearchScope keywordSearchScope;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        public Builder() {
        }

        public Builder(TagGroup tagGroup) {
            this.id = tagGroup.id;
            this.parentId = tagGroup.parentId;
            this.name = tagGroup.name;
            this.description = tagGroup.description;
            this.tags = tagGroup.tags;
            this.keywordSearchScope = tagGroup.keywordSearchScope;
            this.createTime = tagGroup.createTime;
            this.updateTime = tagGroup.updateTime;
            this.deleted = tagGroup.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setParentId(Long parentId) {
            this.parentId = parentId;
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

        public Builder setTags(long[] tags) {
            this.tags = tags;
            return this;
        }

        public Builder setKeywordSearchScope(KeywordSearchScope keywordSearchScope) {
            this.keywordSearchScope = keywordSearchScope;
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

        public TagGroup build() {
            return new TagGroup(id, parentId, name,
                    description, tags, keywordSearchScope,
                    createTime, updateTime, deleted);
        }
    }
}
