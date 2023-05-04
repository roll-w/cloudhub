package org.huel.cloudhub.client.disk.domain.tag.service;

import org.huel.cloudhub.client.disk.domain.tag.ContentTagService;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagDto;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.client.disk.domain.tag.repository.ContentTagRepository;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ContentTagServiceImpl implements ContentTagService {
    private final ContentTagRepository contentTagRepository;

    public ContentTagServiceImpl(ContentTagRepository contentTagRepository) {
        this.contentTagRepository = contentTagRepository;
    }

    @Override
    public ContentTagDto getTagById(long id) {
        return null;
    }

    @Override
    public TagGroupDto getTagGroupById(long id) {
        return null;
    }

    @Override
    public List<ContentTagDto> getTags(Pageable pageable) {
        return null;
    }

    @Override
    public ContentTagDto getByName(String name) {
        return null;
    }
}
