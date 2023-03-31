package org.huel.cloudhub.file.fs.meta.cache;

import org.huel.cloudhub.file.fs.meta.MetadataCacheStrategy;
import org.huel.cloudhub.file.fs.meta.MetadataCacheable;
import space.lingu.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class MetadataFullyCacheStrategy implements MetadataCacheStrategy {
    private final Map<String, MetadataCacheable<?>> cache =
            new ConcurrentHashMap<>();

    public MetadataFullyCacheStrategy() {
    }

    @Override
    public void cache(MetadataCacheable<?> meta) {
        cache.put(meta.getKey(), meta);
    }

    @Override
    @Nullable
    public <T> MetadataCacheable<T> get(String key) {
        return (MetadataCacheable<T>) cache.get(key);
    }
}
