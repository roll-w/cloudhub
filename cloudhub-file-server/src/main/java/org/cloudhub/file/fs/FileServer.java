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

package org.cloudhub.file.fs;

import space.lingu.NonNull;

import java.io.IOException;

/**
 * 文件服务器接口。
 *
 * @author RollW
 */
public interface FileServer {
    default boolean upload(String localFilePath, String remoteFilePath) throws IOException {
        return upload(getServerFileProvider().openFile(localFilePath),
                getServerFileProvider().openFile(remoteFilePath));
    }

    default boolean upload(ServerFile localFile, ServerFile remoteFile) throws IOException {
        return upload(localFile, remoteFile, false);
    }

    boolean upload(ServerFile localFile, ServerFile remoteFile, boolean overwrite) throws IOException;

    default boolean download(String localFilePath, String remoteFilePath) throws IOException {
        return download(getServerFileProvider().openFile(localFilePath),
                getServerFileProvider().openFile(remoteFilePath));
    }

    default boolean download(ServerFile remoteFile, ServerFile localFile) throws IOException {
        return download(remoteFile, localFile, false);
    }


    boolean download(ServerFile remoteFile, ServerFile localFile, boolean overwrite) throws IOException;

    @NonNull
    ServerFileProvider getServerFileProvider();
}
