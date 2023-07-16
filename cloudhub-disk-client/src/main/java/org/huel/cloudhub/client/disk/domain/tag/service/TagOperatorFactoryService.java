package org.huel.cloudhub.client.disk.domain.tag.service;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorFactory;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.systembased.validate.ValidatorProvider;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.huel.cloudhub.client.disk.domain.tag.TagGroupOperator;
import org.huel.cloudhub.client.disk.domain.tag.TagOperator;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagErrorCode;
import org.huel.cloudhub.client.disk.domain.tag.common.ContentTagException;
import org.huel.cloudhub.client.disk.domain.tag.repository.ContentTagRepository;
import org.huel.cloudhub.client.disk.domain.tag.repository.TagGroupRepository;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class TagOperatorFactoryService implements SystemResourceOperatorFactory,
        TagGroupOperatorDelegate, TagOperatorDelegate {
    private final TagGroupRepository tagGroupRepository;
    private final ContentTagRepository contentTagRepository;
    private final Validator tagValidator;
    private final Validator tagGroupValidator;

    public TagOperatorFactoryService(TagGroupRepository tagGroupRepository,
                                     ContentTagRepository contentTagRepository,
                                     ValidatorProvider validatorProvider) {
        this.tagGroupRepository = tagGroupRepository;
        this.contentTagRepository = contentTagRepository;
        tagGroupValidator = validatorProvider.getValidator(SystemResourceKind.TAG_GROUP);
        tagValidator = validatorProvider.getValidator(SystemResourceKind.TAG);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.TAG ||
                systemResourceKind == SystemResourceKind.TAG_GROUP;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return false;
    }

    @Override
    public SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            boolean checkDelete) {
        return switch (systemResource.getSystemResourceKind()) {
            case TAG -> forTagOperator(systemResource, checkDelete);
            case TAG_GROUP -> forTagGroupOperator(systemResource, checkDelete);
            default -> throw new IllegalArgumentException("Unsupported system resource kind: " +
                    systemResource.getSystemResourceKind());
        };
    }

    private TagOperator forTagOperator(SystemResource systemResource,
                                       boolean check) {
        if (systemResource instanceof ContentTag contentTag) {
            return new TagOperatorImpl(contentTag, this, check);
        }
        ContentTag contentTag = contentTagRepository.getById(systemResource.getResourceId());
        if (contentTag == null) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_NOT_EXIST);
        }
        return new TagOperatorImpl(contentTag, this, check);
    }

    private TagGroupOperator forTagGroupOperator(SystemResource systemResource,
                                                 boolean check) {
        if (systemResource instanceof TagGroup tagGroup) {
            return new TagGroupOperatorImpl(tagGroup, this, check);
        }
        TagGroup tagGroup = tagGroupRepository.getById(systemResource.getResourceId());
        if (tagGroup == null) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_GROUP_NOT_EXIST);
        }
        return new TagGroupOperatorImpl(tagGroup, this, check);
    }

    @Override
    public void updateTagGroup(TagGroup tagGroup) {
        tagGroupRepository.update(tagGroup);
    }

    @Override
    public Validator getTagGroupValidator() {
        return tagGroupValidator;
    }

    @Override
    public void updateTag(ContentTag tag) {
        contentTagRepository.update(tag);
    }

    @Override
    public Validator getTagValidator() {
        return tagValidator;
    }
}
