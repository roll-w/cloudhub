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

import org.cloudhub.meta.server.service.file.FileDeleteService;
import org.cloudhub.meta.server.service.file.FileDeleteService;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @author RollW
 */
@ShellComponent
public class DeleteCommand extends AbstractShellComponent {
    private final FileDeleteService fileDeleteService;

    public DeleteCommand(FileDeleteService fileDeleteService) {
        this.fileDeleteService = fileDeleteService;
    }

    @ShellMethod(value = "delete file.", key = {"delete"})
    public void deleteFile(String file) {
        getTerminal().writer().println("Start delete file, id=" + file);
        getTerminal().writer().flush();
        fileDeleteService.deleteFileCompletely(file);
    }
}
