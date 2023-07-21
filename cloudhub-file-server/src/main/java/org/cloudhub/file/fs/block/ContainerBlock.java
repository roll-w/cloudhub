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

package org.cloudhub.file.fs.block;

import org.cloudhub.file.fs.container.ContainerLocation;
import org.cloudhub.file.fs.container.ContainerLocation;

import java.util.Arrays;

/**
 * @author RollW
 */
public class ContainerBlock {
    public static final byte[] NULL = new byte[0];

    private final ContainerLocation containerLocation;

    private byte[] data;
    private final int index;
    private long validBytes;


    public ContainerBlock(ContainerLocation containerLocation,
                          int index,
                          byte[] data,
                          long validBytes) {
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

    public long getValidBytes() {
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
