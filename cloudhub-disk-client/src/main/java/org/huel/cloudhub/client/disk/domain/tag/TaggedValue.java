package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author RollW
 */
public record TaggedValue(
        Long groupId,
        Long tagId,
        String name,
        String value
) {
    public static TaggedValue of(Long groupId, Long tagId,
                                 String name, String value) {
        return new TaggedValue(groupId, tagId, name, value);
    }

    public static List<TaggedValue> pairWithTags(List<TagGroupInfo> tagGroupInfos,
                                                 List<ContentTagInfo> tags) {
        List<TaggedValue> tagValues = new ArrayList<>();
        List<ContentTagInfo> sortedTags = tags.stream()
                .sorted(Comparator.comparingLong(ContentTagInfo::id))
                .toList();
        for (TagGroupInfo tagGroupInfo : tagGroupInfos) {
            ContentTagInfo tag = findInTags(sortedTags, tagGroupInfo.tags());
            tagValues.add(new TaggedValue(
                    tagGroupInfo.id(),
                    tag.id(),
                    tagGroupInfo.name(),
                    tag.name()
            ));
        }
        return tagValues;
    }

    private static ContentTagInfo findInTags(List<ContentTagInfo> tags,
                                             long[] tagIds) {
        return tags.stream()
                .filter(tag -> Arrays.stream(tagIds).anyMatch(tagId -> tag.id() == tagId))
                .findFirst()
                .orElse(null);
    }


}
