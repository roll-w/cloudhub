package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.fs.container.ContainerType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public class LocalContainerMeta implements ContainerMeta {
    private final String locator;
    private final int blockSize;
    private final int usedBlock;
    private final int blockCapacity;
    private final String checksum;
    private final long version;


    // TODO:
    public LocalContainerMeta(String locator, int blockSize,
                              int usedBlock,
                              int blockCapacity, String checksum,
                              long version) {
        this.locator = locator;
        this.blockSize = blockSize;
        this.usedBlock = usedBlock;
        this.blockCapacity = blockCapacity;
        this.checksum = checksum;
        this.version = version;
    }

    @Override
    public String getLocator() {
        return locator;
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
    public long getVersion() {
        return version;
    }

    @Override
    public String getSource() {
        return ContainerFinder.LOCAL;
    }

    @Override
    public ContainerType getContainerType() {
        return ContainerType.ORIGINAL;
    }

    @Override
    public List<? extends BlockFileMeta> getBlockFileMetas() {
        return null;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {

    }
}
