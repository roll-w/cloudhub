package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public interface MetadataCacheable<T> {
    String getKey();

    T getMeta();

    Class<T> getCacheableClass();
}
