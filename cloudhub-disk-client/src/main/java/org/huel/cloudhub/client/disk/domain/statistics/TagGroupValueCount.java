package org.huel.cloudhub.client.disk.domain.statistics;

import java.util.List;

/**
 * @author RollW
 */
public record TagGroupValueCount(
        long groupId,
        String name,
        List<TagValueCount> counts
)  {

}
