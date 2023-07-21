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
 * Local file server, but not a true server.
 * <p>
 * Upload or download methods in the class will
 * only simply copies file.
 *
 * @author RollW
 */
public class LocalFileServer implements FileServer {
    final LocalFileProvider localFileProvider;

    public LocalFileServer() {
        this.localFileProvider = new LocalFileProvider();
    }


    @Override
    public boolean upload(ServerFile sourceFile, ServerFile destFile, boolean overwrite) throws IOException {
        return false;
    }

    @Override
    public boolean download(ServerFile remoteFile, ServerFile localFile, boolean overwrite) throws IOException {
        return false;
    }

    @NonNull
    @Override
    public ServerFileProvider getServerFileProvider() {
        return localFileProvider;
    }
}
