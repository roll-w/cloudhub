package org.huel.cloudhub.file.checksum;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.nio.ByteBuffer;

/**
 * @author RollW
 */
public class Crc32Checksum {
    // TODO:
    private final Hasher hasher = Hashing.crc32().newHasher();

    public Crc32Checksum putBytes(byte[] bytes) {
        hasher.putBytes(bytes);
        return this;
    }

    public Crc32Checksum putBytes(byte[] bytes, int off, int len) {
        hasher.putBytes(bytes, off, len);
        return this;
    }

    public Crc32Checksum putBytes(ByteBuffer bytes) {
        hasher.putBytes(bytes);
        return this;
    }

    public String stringChecksum() {
        return hasher.hash().toString();
    }


    public Crc32Checksum() {
    }
}
