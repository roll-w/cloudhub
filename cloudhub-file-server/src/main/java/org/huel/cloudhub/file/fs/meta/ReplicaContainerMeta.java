package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public class ReplicaContainerMeta implements ContainerMeta {
    private final String locator;
    private final long version;
    private final String source;
    private final int blockSize;
    private final int usedBlock;
    private final int blockCapacity;
    private final String checksum;
    private final List<? extends BlockFileMeta> blockFileMetas;

    private final SerializedContainerBlockMeta serializedContainerMeta;

    public ReplicaContainerMeta(String locator, long version,
                                String source, int blockSize,
                                int usedBlock, int blockCapacity,
                                String checksum,
                                List<? extends BlockFileMeta> blockFileMetas) {
        this(locator, version, source, blockSize, usedBlock,
                blockCapacity, checksum,
                blockFileMetas, null);
    }

    public ReplicaContainerMeta(SerializedContainerBlockMeta serializedContainerMeta,
                                String locator, long version,
                                String source,
                                List<? extends BlockFileMeta> blockFileMetas) {
        this(locator, version, source,
                serializedContainerMeta.getBlockSize(),
                serializedContainerMeta.getUsedBlock(),
                serializedContainerMeta.getBlockCap(),
                serializedContainerMeta.getCrc(),
                blockFileMetas);
    }

    protected ReplicaContainerMeta(String locator, long version,
                                   String source, int blockSize,
                                   int usedBlock, int blockCapacity,
                                   String checksum,
                                   List<? extends BlockFileMeta> blockFileMetas,
                                   SerializedContainerBlockMeta serializedContainerMeta) {
        this.locator = locator;
        this.version = version;
        this.source = source;
        this.blockSize = blockSize;
        this.usedBlock = usedBlock;
        this.blockCapacity = blockCapacity;
        this.checksum = checksum;
        this.blockFileMetas = blockFileMetas;
        this.serializedContainerMeta = serializedContainerMeta;
    }

    @Override
    public String getLocator() {
        return locator;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    @Override
    public int getUsedBlock() {
        return usedBlock;
    }

    @Override
    public int getBlockCapacity() {
        return blockCapacity;
    }

    @Override
    public String getChecksum() {
        return checksum;
    }

    @Override
    public ContainerType getContainerType() {
        return ContainerType.REPLICA;
    }

    @Override
    public List<? extends BlockFileMeta> getBlockFileMetas() {
        return blockFileMetas;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        serializedContainerMeta.writeTo(outputStream);
    }

    private static SerializedContainerBlockMeta buildSerializedContainerMeta(
            String crc) {
        return SerializedContainerBlockMeta.newBuilder()
                .setCrc(crc)
                .build();
    }


    public static final class Builder implements ContainerMetaBuilder {
        private String locator;
        private long version;
        private String source;
        private int blockSize;
        private int usedBlock;
        private int blockCapacity;
        private String checksum;
        private List<? extends BlockFileMeta> blockFileMetas;

        public Builder() {
        }

        @Override
        public Builder setLocator(String locator) {
            this.locator = locator;
            return this;
        }

        @Override
        public Builder setVersion(long version) {
            this.version = version;
            return this;
        }

        @Override
        public Builder setSource(String source) {
            this.source = source;
            return this;
        }

        @Override
        public Builder setBlockSize(int blockSize) {
            this.blockSize = blockSize;
            return this;
        }

        @Override
        public Builder setUsedBlock(int usedBlock) {
            this.usedBlock = usedBlock;
            return this;
        }

        @Override
        public Builder setBlockCapacity(int blockCapacity) {
            this.blockCapacity = blockCapacity;
            return this;
        }

        @Override
        public Builder setChecksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        @Override
        public Builder setBlockFileMetas(List<? extends BlockFileMeta> blockFileMetas) {
            this.blockFileMetas = blockFileMetas;
            return this;
        }

        @Override
        public ReplicaContainerMeta build() {
            return new ReplicaContainerMeta(
                    locator, version, source,
                    blockSize, usedBlock,
                    blockCapacity, checksum,
                    blockFileMetas);
        }
    }
}
