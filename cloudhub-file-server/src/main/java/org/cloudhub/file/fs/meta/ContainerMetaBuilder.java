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

package org.cloudhub.file.fs.meta;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerMetaBuilder {
    ContainerMetaBuilder setLocator(String locator);

    ContainerMetaBuilder setId(String id);

    ContainerMetaBuilder setVersion(long version);

    ContainerMetaBuilder setSerial(long serial);

    ContainerMetaBuilder setSource(String source);

    ContainerMetaBuilder setBlockSize(int blockSize);

    ContainerMetaBuilder setUsedBlock(int usedBlock);

    ContainerMetaBuilder setBlockCapacity(int blockCapacity);

    ContainerMetaBuilder setChecksum(String checksum);

    ContainerMetaBuilder setBlockFileMetas(List<? extends BlockFileMeta> blockFileMetas);

    ContainerMeta build();
}
