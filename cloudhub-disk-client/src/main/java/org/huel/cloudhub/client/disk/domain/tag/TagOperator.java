package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface TagOperator extends SystemResource, SystemResourceOperator {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.TAG;
    }

    @Override
    TagOperator update() throws BusinessRuntimeException;

    @Override
    TagOperator delete() throws BusinessRuntimeException;

    @Override
    TagOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException;

    TagOperator setDescription(String description) throws BusinessRuntimeException;

    /**
     * If the keyword is already in the tag, the weight of the keyword will be updated.
     */
    TagOperator addKeyword(TagKeyword tagKeyword) throws BusinessRuntimeException;

    TagOperator removeKeyword(TagKeyword tagKeyword) throws BusinessRuntimeException;

    @Override
    TagOperator disableAutoUpdate();

    @Override
    TagOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    TagOperator getSystemResource();

    ContentTag getContentTag();
}
