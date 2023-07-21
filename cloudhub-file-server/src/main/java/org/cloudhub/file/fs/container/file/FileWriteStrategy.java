/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.fs.container.file;

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