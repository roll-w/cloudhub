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

package org.cloudhub.file.server.command;

import org.cloudhub.file.server.FileServerApplication;
import org.cloudhub.server.command.CommandHelper;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.shell.standard.commands.Version;

/**
 * @author RollW
 */
@ShellComponent
@ShellCommandGroup("Built-In Commands")
public class BuiltinCommand extends AbstractShellComponent
        implements Version.Command, Quit.Command {

    @ShellMethod(key = {"version"}, value = "Show version info.")
    public void version() {
        CommandHelper.printVersion(getTerminal().writer(), "meta-server");
    }

    @ShellMethod(key = {"exit", "quit"}, value = "Shutdown server.")
    public void exit() {
        FileServerApplication.exit(0);
    }

    @ShellMethod(key = {"close"}, value = "Exit the shell.")
    public void close() {
        throw new ExitRequest();
    }

}
