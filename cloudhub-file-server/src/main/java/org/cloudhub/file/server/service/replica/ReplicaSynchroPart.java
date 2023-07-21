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

package org.cloudhub.file.server.service.replica;

import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.container.Container;

/**
 * @author RollW
 */
public class ReplicaSynchroPart {
    private final Container container;
    private final BlockGroupsInfo blockGroupsInfo;
    private final int count;

    public ReplicaSynchroPart(Container container, BlockGroupsInfo blockGroupsInfo, int count) {
        this.container = container;
        this.blockGroupsInfo = blockGroupsInfo;
        this.count = count;
    }

    public Container getContainer() {
        return container;
    }

    public BlockGroupsInfo getBlockGroupsInfo() {
        return blockGroupsInfo;
    }

    public int getCount() {
        return count;
    }
}
