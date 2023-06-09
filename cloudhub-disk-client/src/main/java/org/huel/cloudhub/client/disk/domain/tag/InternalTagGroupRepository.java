package org.huel.cloudhub.client.disk.domain.tag;

import java.util.List;

/**
 * @author RollW
 */
public interface InternalTagGroupRepository {
    List<TagGroup> findAll();
}
