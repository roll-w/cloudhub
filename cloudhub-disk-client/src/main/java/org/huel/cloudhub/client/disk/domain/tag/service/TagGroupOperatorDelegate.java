package org.huel.cloudhub.client.disk.domain.tag.service;

import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;

/**
 * @author RollW
 */
public interface TagGroupOperatorDelegate {
    void updateTagGroup(TagGroup tagGroup);

    Validator getTagGroupValidator();
}
