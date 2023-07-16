package org.huel.cloudhub.client.disk.domain.tag.service;

import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;

/**
 * @author RollW
 */
public interface TagOperatorDelegate {
    void updateTag(ContentTag tag);

    Validator getTagValidator();
}
