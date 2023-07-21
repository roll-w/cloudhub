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

package org.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * @author RollW
 */
public class ContainerIdentity {
    private final String id;
    private String crc;
    private final long serial;
    private final int blockLimit;
    // block size in kb
    private final int blockSize;

    private final long blockSizeInBytes;
    private final long limitBytes;

    public static final int ID_SUBNUM = 16;
    public static final int IDMETA_SUBNUM = 8;

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

    public void updatesChecksum(@NonNull String crc) {
        this.crc = crc;
    }

    public boolean isInvalidChecksum() {
        return INVALID_CRC.equals(crc);
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

    public static String toContainerId(String id) {
        if (id.length() <= ID_SUBNUM) {
            return id;
        }
        return id.substring(0, ID_SUBNUM);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerIdentity identity = (ContainerIdentity) o;
        return serial == identity.serial && blockLimit == identity.blockLimit && blockSize == identity.blockSize && blockSizeInBytes == identity.blockSizeInBytes && limitBytes == identity.limitBytes && Objects.equals(id, identity.id) && Objects.equals(crc, identity.crc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, crc, serial, blockLimit, blockSize, blockSizeInBytes, limitBytes);
    }

    @Override
    public String toString() {
        return "ContainerIdentity{" +
                "id='" + id + '\'' +
                ", crc='" + crc + '\'' +
                ", serial=" + serial +
                ", blockLimit=" + blockLimit +
                ", blockSize=" + blockSize +
                ", blockSizeInBytes=" + blockSizeInBytes +
                ", limitBytes=" + limitBytes +
                '}';
    }
}
