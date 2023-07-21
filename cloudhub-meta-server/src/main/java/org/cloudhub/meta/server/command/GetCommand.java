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

package org.cloudhub.meta.server.command;

import org.cloudhub.meta.server.service.file.FileDownloadCallback;
import org.cloudhub.meta.server.service.file.FileDownloadService;
import org.cloudhub.meta.server.service.file.FileDownloadingException;
import org.cloudhub.meta.server.service.file.FileDownloadCallback;
import org.cloudhub.meta.server.service.file.FileDownloadService;
import org.cloudhub.meta.server.service.file.FileDownloadingException;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
@ShellComponent
public class GetCommand extends AbstractShellComponent {
    private final FileDownloadService fileDownloadService;

    public GetCommand(FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
    }

    @ShellMethod(value = "download file from file server.", key = {"get"})
    public void getAction(
            @ShellOption(help = "download to path",
                    value = {"--path", "-p"}) String path,
            @ShellOption(help = "file id",
                    value = {"--file", "-f"}) String fileId
    ) throws IOException {
        if (path == null || path.isEmpty()) {
            getTerminal().writer().println("no path provide");
            return;
        }
        downloadTo(path, fileId);
        getTerminal().writer().flush();
    }

    private void downloadTo(String path, String fileId) throws IOException {
        OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(path, false));
        getTerminal().writer().println("download file to %s, from file %s."
                .formatted(path, fileId));
        getTerminal().writer().flush();
        fileDownloadService.downloadFile(outputStream, fileId, new FileDownloadCallback() {
            @Override
            public void onDownloadComplete() {
                getTerminal().writer().println("download complete");
            }

            @Override
            public void onDownloadError(FileDownloadingException e) {

            }
        });
    }
}
