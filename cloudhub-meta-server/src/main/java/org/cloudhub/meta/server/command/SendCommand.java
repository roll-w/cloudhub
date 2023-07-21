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

import org.cloudhub.meta.server.service.file.FileUploadService;
import org.cloudhub.meta.server.service.file.FileUploadService;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author RollW
 */
@ShellComponent
public class SendCommand extends AbstractShellComponent {
    private final FileUploadService fileUploadService;

    public SendCommand(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @ShellMethod(value = "send file to file server.", key = {"send"})
    public void nodeAction(
            @ShellOption(help = "send file at this path",
                    value = {"--path", "-p"}) String path) throws IOException {
        if (path == null || path.isEmpty()) {
            getTerminal().writer().println("no path provide");
            return;
        }
        sendFile(path);
        getTerminal().writer().flush();
    }

    private void sendFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            getTerminal().writer().printf("file path at '%s' not exists.\n",
                    path);
            return;
        }
        if (!file.isFile()) {
            getTerminal().writer().printf("path at '%s' is not a file.\n",
                    path);
            return;
        }
        getTerminal().writer().printf("send file in absolute path '%s'.\n",
                file.getAbsolutePath());
        getTerminal().writer().flush();
        fileUploadService.uploadFile(new FileInputStream(file), null);
    }
}
