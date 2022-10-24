package org.huel.cloudhub.file.fs.block;

import org.huel.cloudhub.file.fs.container.ContainerLocation;

import java.util.Arrays;

/**
 * @author RollW
 */
public class ContainerBlock {
    public static final byte[] NULL = new byte[0];

    private final ContainerLocation containerLocation;

    private byte[] data;
    private final int index;
    private int validBytes;


    public ContainerBlock(ContainerLocation containerLocation,
                          int index,
                          byte[] data,
                          int validBytes) {
        this.containerLocation = containerLocation;
        this.index = index;
        this.data = data;
        this.validBytes = validBytes;
    }

    public ContainerBlock(ContainerLocation containerLocation,
                          int index) {
        this(containerLocation, index, NULL, 0);
    }

    public void write(byte[] data, int validBytes) {
        if (data == null || data == NULL) {
            throw new IllegalArgumentException("Cannot write null");
        }
        this.data = data;
        this.validBytes = validBytes;
    }

    public ContainerLocation getContainerLocation() {
        return containerLocation;
    }

    public byte[] getData() {
        return data;
    }

    public int getIndex() {
        return index;
    }

    public int getValidBytes() {
        return validBytes;
    }

    public void release() {
        data = NULL;
    }

    public ContainerBlock forkNull() {
        return new ContainerBlock(containerLocation, index, NULL, validBytes);
    }

    public ContainerBlock fork() {
        return new ContainerBlock(
                containerLocation,
                index,
                Arrays.copyOf(data, data.length),
                validBytes);
    }
}
