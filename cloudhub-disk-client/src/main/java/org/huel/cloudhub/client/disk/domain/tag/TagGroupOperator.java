package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface TagGroupOperator extends SystemResource, SystemResourceOperator {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.TAG_GROUP;
    }

    @Override
    TagGroupOperator update() throws BusinessRuntimeException;

    @Override
    TagGroupOperator delete() throws BusinessRuntimeException;

    @Override
    TagGroupOperator rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException;

    TagGroupOperator setDescription(String description);

    TagGroupOperator setKeywordSearchScope(KeywordSearchScope scope);

    TagGroupOperator addTag(long tagId);

    TagGroupOperator removeTag(long tagId);

    TagGroupOperator addTag(SystemResource systemResource);

    TagGroupOperator removeTag(SystemResource systemResource);

    @Override
    TagGroupOperator disableAutoUpdate();

    @Override
    TagGroupOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    TagGroupOperator getSystemResource();

    TagGroup getTagGroup();

}
