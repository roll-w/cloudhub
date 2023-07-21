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

package org.cloudhub.meta.fs;

/**
 * 上传状态
 *
 * @author RollW
 */
public enum FileObjectUploadStatus {
    /**
     * 处于元文件服务器的暂存区。文件不可用。
     */
    TEMPORARY(true, false),
    /**
     * 处于文件服务器存储的过程中。文件不可用。
     */
    STORING(true, false),
    /**
     * 处于文件服务器存储中，副本同步状态。文件当前可用，副本不可用。
     */
    SYNCING(true, true),
    /**
     * 文件和副本现在都可用。
     */
    AVAILABLE(true, true),
    /**
     * 文件和副本都丢失。
     */
    LOST(false, true);

    private final boolean available;
    private final boolean lastStatus;

    FileObjectUploadStatus(boolean available, boolean lastStatus) {
        this.available = available;
        this.lastStatus = lastStatus;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isLastStatus() {
        return lastStatus;
    }
}
