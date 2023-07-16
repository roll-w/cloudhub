package org.huel.cloudhub.client.disk.domain.tag.service;

import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;
import org.huel.cloudhub.client.disk.domain.tag.TagOperator;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagErrorCode;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagException;
import org.huel.cloudhub.web.BusinessRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author RollW
 */
public class TagOperatorImpl implements TagOperator {

    private ContentTag contentTag;
    private final TagOperatorDelegate delegate;
    private final ContentTag.Builder builder;

    private boolean checkDeleted;
    private boolean autoUpdateEnabled = true;
    private boolean updateFlag = false;

    public TagOperatorImpl(ContentTag contentTag,
                           TagOperatorDelegate delegate,
                           boolean checkDeleted) {
        this.contentTag = contentTag;
        this.builder = contentTag.toBuilder();
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
        return contentTag.getResourceId();
    }

    @Override
    public TagOperator update() throws BusinessRuntimeException {
        if (!autoUpdateEnabled && updateFlag) {
            contentTag = builder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateTag(contentTag);
            updateFlag = false;
            return this;
        }
        return this;
    }

    @Override
    public TagOperator delete() throws BusinessRuntimeException {
        checkDeleted();
        if (contentTag.isDeleted()) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_DELETED);
        }
        builder.setDeleted(true);
        return updateInternal();
    }

    @Override
    public TagOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException {
        checkDeleted();
        if (Objects.equals(contentTag.getName(), newName)) {
            return this;
        }
        delegate.getTagValidator().validateThrows(newName, FieldType.NAME);
        builder.setName(newName);
        return updateInternal();
    }

    @Override
    public TagOperator setDescription(String description) throws BusinessRuntimeException {
        checkDeleted();
        if (Objects.equals(contentTag.getDescription(), description)) {
            return this;
        }
        delegate.getTagValidator().validateThrows(description, FieldType.DESCRIPTION);

        builder.setDescription(description);
        return updateInternal();
    }

    @Override
    public TagOperator addKeyword(TagKeyword tagKeyword) throws BusinessRuntimeException {
        checkDeleted();

        List<TagKeyword> rawKeywords = contentTag.getKeywords();
        List<TagKeyword> newKeywords = new ArrayList<>();
        boolean keywordExists = false;
        for (TagKeyword rawKeyword : rawKeywords) {
            if (Objects.equals(rawKeyword.name(), tagKeyword.name())) {
                keywordExists = true;
                newKeywords.add(tagKeyword);
            } else {
                newKeywords.add(rawKeyword);
            }
        }
        if (!keywordExists) {
            newKeywords.add(tagKeyword);
        }
        builder.setKeywords(newKeywords);
        return updateInternal();
    }

    @Override
    public TagOperator removeKeyword(TagKeyword tagKeyword)
            throws BusinessRuntimeException {
        checkDeleted();

        List<TagKeyword> rawKeywords = contentTag.getKeywords();
        List<TagKeyword> newKeywords = new ArrayList<>();
        boolean keywordExists = false;
        for (TagKeyword rawKeyword : rawKeywords) {
            if (Objects.equals(rawKeyword.name(), tagKeyword.name())) {
                keywordExists = true;
            } else {
                newKeywords.add(rawKeyword);
            }
        }
        if (!keywordExists) {
            return this;
        }
        builder.setKeywords(newKeywords);
        return updateInternal();
    }

    @Override
    public TagOperator disableAutoUpdate() {
        autoUpdateEnabled = false;
        return this;
    }

    @Override
    public TagOperator enableAutoUpdate() {
        autoUpdateEnabled = true;
        return this;
    }

    @Override
    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }

    @Override
    public TagOperator getSystemResource() {
        return this;
    }

    @Override
    public ContentTag getContentTag() {
        return contentTag;
    }

    private void checkDeleted() {
        if (!checkDeleted) {
            return;
        }
        if (contentTag.isDeleted()) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_DELETED);
        }
    }

    private TagOperator updateInternal() {
        if (!autoUpdateEnabled) {
            updateFlag = true;
            return this;
        }
        contentTag = builder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        delegate.updateTag(contentTag);
        return this;
    }
}
