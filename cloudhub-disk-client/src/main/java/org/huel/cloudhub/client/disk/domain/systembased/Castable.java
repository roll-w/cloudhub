package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * Help to cast the object to the specified type.
 *
 * @author RollW
 */
public interface Castable {
    default <T> T cast(Class<T> clazz) {
        return clazz.cast(this);
    }
}
