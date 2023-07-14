package org.huel.cloudhub.client.disk.domain.tag;

/**
 * @author RollW
 */
public interface TaggedValue {
    long groupId();

    long tagId();

    String name();

    String value();
}
