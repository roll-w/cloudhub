package org.huel.cloudhub.file.fs.meta;

import space.lingu.Nullable;

/**
 * @author RollW
 */
public interface MetadataCacheStrategy {
    void cache(MetadataCacheable<?> meta);

    @Nullable
    <T> MetadataCacheable<T> get(String key);
}
