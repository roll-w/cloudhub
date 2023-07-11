package org.huel.cloudhub.client.disk.domain.tag;

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
}
