package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public class ContainerIdentity {
    private final String id;
    private final String crc;
    private final long serial;
    private final int blockLimit;
    private final int blockSize;

    private final long blockSizeInBytes;
    private final long limitBytes;

    public static final int ID_SUBNUM = 32;
    public static final int IDMETA_SUBNUM = 12;

    public static final String INVALID_CRC = "INVALID";

    public ContainerIdentity(String id, String crc,
                             long serial, int blockLimit,
                             int blockSize) {
        this.id = id;
        this.crc = crc;
        this.serial = serial;
        this.blockLimit = blockLimit;
        this.blockSize = blockSize;
        this.blockSizeInBytes = blockSize * 1024L;
        this.limitBytes = blockSizeInBytes * blockLimit;
    }

    public String id() {
        return id;
    }

    public String crc() {
        return crc;
    }

    public long serial() {
        return serial;
    }

    public int blockLimit() {
        return blockLimit;
    }

    public int blockSize() {
        return blockSize;
    }

    public long blockSizeBytes() {
        return blockSizeInBytes;
    }

    public long limitBytes() {
        return limitBytes;
    }

    public static String toCmetaId(String id) {
        return id.substring(0, IDMETA_SUBNUM);
    }

    public static String toMetaId(String id) {
        return id.substring(0, ID_SUBNUM);
    }
}
