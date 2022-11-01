package org.huel.cloudhub.file.fs.container.file;

/**
 * File write policy.
 *
 * @author RollW
 */
public enum FileWriteStrategy {
    /**
     * Write all blocks sequence into free blocks.
     * Useful for small size, avoid excessive file IO.
     * <p>
     * Always write sequentially starting from the
     * first free block.
     */
    SEQUENCE,

    /**
     * Skip too small free block segment. Only if size is lesser than
     * container size, else will degrade to {@link #SEARCH_MAX}).
     * <p>
     * A "small free block size" segment: The free block segment size
     * is less than a quarter of the current size to be written.
     */
    SKIP_SMALL,

    /**
     * Search for maximum free space, or a contiguous free block
     * that can be filled with its own size. If not found, it will
     * degenerate to a case where the size is larger than the
     * container size.
     * <p>
     * If the size is larger than the container size,
     * will fill into new containers sequence.
     */
    SEARCH_MAX;
}