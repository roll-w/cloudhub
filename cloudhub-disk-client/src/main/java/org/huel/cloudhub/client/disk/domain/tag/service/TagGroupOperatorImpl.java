package org.huel.cloudhub.client.disk.domain.tag.service;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.UnsupportedKindException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.huel.cloudhub.client.disk.domain.tag.TagGroupOperator;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagErrorCode;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagException;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.WebCommonErrorCode;

import java.util.Objects;

/**
 * @author RollW
 */
public class TagGroupOperatorImpl implements TagGroupOperator {
    private TagGroup tagGroup;
    private final TagGroupOperatorDelegate delegate;
    private final TagGroup.Builder builder;

    private boolean checkDeleted;
    private boolean autoUpdateEnabled = true;
    private boolean updateFlag = false;

    public TagGroupOperatorImpl(TagGroup tagGroup,
                                TagGroupOperatorDelegate delegate,
                                boolean checkDeleted) {
        this.tagGroup = tagGroup;
        this.builder = tagGroup.toBuilder();
        this.delegate = delegate;
        this.checkDeleted = checkDeleted;
    }


    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        this.checkDeleted = checkDeleted;
    }

    @Override
    public boolean isCheckDeleted() {
        return checkDeleted;
    }

    @Override
    public long getResourceId() {
        return tagGroup.getResourceId();
    }

    @Override
    public TagGroupOperator update() throws BusinessRuntimeException {
        if (!autoUpdateEnabled && updateFlag) {
            tagGroup = builder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateTagGroup(tagGroup);
            updateFlag = false;
            return this;
        }
        return this;
    }

    @Override
    public TagGroupOperator delete() throws BusinessRuntimeException {
        checkDeleted();
        if (tagGroup.isDeleted()) {
            return this;
        }
        builder.setDeleted(true);
        return updateInternal();
    }

    @Override
    public TagGroupOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException {
        checkDeleted();
        if (Objects.equals(tagGroup.getName(), newName)) {
            return this;
        }
        delegate.getTagGroupValidator().validateThrows(newName, FieldType.NAME);
        builder.setName(newName);
        return updateInternal();
    }

    @Override
    public TagGroupOperator setDescription(String description) {
        checkDeleted();
        if (Objects.equals(tagGroup.getDescription(), description)) {
            return this;
        }
        delegate.getTagGroupValidator().validateThrows(description, FieldType.DESCRIPTION);
        builder.setDescription(description);
        return updateInternal();
    }

    @Override
    public TagGroupOperator setKeywordSearchScope(KeywordSearchScope scope) {
        checkDeleted();
        if (scope == null) {
            throw new ContentTagException(WebCommonErrorCode.ERROR_PARAM_MISSING,
                    "Keyword search scope can not be null");
        }
        if (Objects.equals(tagGroup.getKeywordSearchScope(), scope)) {
            return this;
        }
        builder.setKeywordSearchScope(scope);
        return updateInternal();
    }

    @Override
    public TagGroupOperator addTag(long tagId) {
        checkDeleted();
        // TODO: may needs check tag is deleted or not
        long[] rawTagIds = tagGroup.getTags();
        if (checkContains(rawTagIds, tagId)) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_EXIST);
        }

        long[] newTagIds = new long[rawTagIds.length + 1];
        System.arraycopy(rawTagIds, 0, newTagIds, 0, rawTagIds.length);
        newTagIds[rawTagIds.length] = tagId;
        builder.setTags(newTagIds);

        return updateInternal();
    }


    @Override
    public TagGroupOperator removeTag(long tagId) {
        checkDeleted();
        long[] rawTagIds = tagGroup.getTags();
        if (!checkContains(rawTagIds, tagId)) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_NOT_EXIST);
        }
        long[] newTagIds = new long[rawTagIds.length - 1];
        int index = 0;
        for (long id : rawTagIds) {
            if (id != tagId) {
                newTagIds[index++] = id;
            }
        }
        builder.setTags(newTagIds);
        return updateInternal();
    }

    @Override
    public TagGroupOperator addTag(SystemResource systemResource) {
        if (systemResource.getSystemResourceKind() != SystemResourceKind.TAG) {
            throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        }
        return addTag(systemResource.getResourceId());
    }

    @Override
    public TagGroupOperator removeTag(SystemResource systemResource) {
        if (systemResource.getSystemResourceKind() != SystemResourceKind.TAG) {
            throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        }

        return removeTag(systemResource.getResourceId());
    }

    private boolean checkContains(long[] tagIds, long tagId) {
        for (long id : tagIds) {
            if (id == tagId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TagGroupOperator disableAutoUpdate() {
        autoUpdateEnabled = false;
        return this;
    }

    @Override
    public TagGroupOperator enableAutoUpdate() {
        autoUpdateEnabled = true;
        return this;
    }

    @Override
    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }

    @Override
    public TagGroupOperator getSystemResource() {
        return this;
    }

    @Override
    public TagGroup getTagGroup() {
        return tagGroup;
    }

    private void checkDeleted() {
        if (!checkDeleted) {
            return;
        }
        if (tagGroup.isDeleted()) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_GROUP_DELETED);
        }
    }

    private TagGroupOperator updateInternal() {
        if (!autoUpdateEnabled) {
            updateFlag = true;
            return this;
        }
        tagGroup = builder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        delegate.updateTagGroup(tagGroup);
        return this;
    }
}
