package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public record ContainerIdentity(
        String id,
        String crc,
        long serial,
        int blockLimit,
        int blockSize) {
    public static final int ID_SUBNUM = 32;
    public static final int IDMETA_SUBNUM = 12;

    public static final String INVALID_CRC = "INVALID";

    public long blockSizeBytes() {
        return blockSize * 1024L;
    }

    public long limitBytes() {
        return blockSizeBytes() * blockLimit;
    }

    public static String toCmetaId(String id) {
        return id.substring(0, IDMETA_SUBNUM);
    }

    public static String toMetaId(String id) {
        return id.substring(0, ID_SUBNUM);
    }
}
