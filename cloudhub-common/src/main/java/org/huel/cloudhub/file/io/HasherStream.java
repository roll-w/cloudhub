package org.huel.cloudhub.file.io;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

/**
 * @author RollW
 */
public interface HasherStream {
    void addHasher(String key, Hasher hasher);

    HashCode getHash(String key);
}
