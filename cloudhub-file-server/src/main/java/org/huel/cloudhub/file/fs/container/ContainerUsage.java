package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public record ContainerUsage(
        int usedBlocks, int blockLimit) {
}
