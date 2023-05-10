package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;

/**
 * @author RollW
 */
public interface TagEventListener {
    void onTagGroupChanged(TagGroupDto tagGroupDto);
}
