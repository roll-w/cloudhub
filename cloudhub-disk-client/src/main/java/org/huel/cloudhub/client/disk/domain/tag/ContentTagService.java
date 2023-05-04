package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagDto;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface ContentTagService {
    ContentTagDto getTagById(long id);

    TagGroupDto getTagGroupById(long id);

    List<ContentTagDto> getTags(Pageable pageable);

    ContentTagDto getByName(String name);
}
