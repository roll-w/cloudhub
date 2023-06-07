package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;

/**
 * @author RollW
 */
public interface TagEventListener {
    default void onTagGroupChanged(TagGroupDto tagGroupDto) {
    }

    default void onTagGroupDelete(String tagGroupName) {
    }
}
