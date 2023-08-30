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

package org.cloudhub.server.command;

import org.cloudhub.BuildProperties;

import java.io.PrintWriter;

/**
 * @author RollW
 */
public class CommandHelper {
    public static final String COMMAND_PREFIX = "cloudhub> ";

    public static void printVersion(PrintWriter writer, String artifact) {
        writer.println("Cloudhub " + BuildProperties.VERSION);
        writer.println(BuildProperties.GIT);
        writer.println("=======================");
        writer.println("Cloudhub " + artifact + " version " + BuildProperties.VERSION +
                " (build " + BuildProperties.GIT_COMMIT_ID_ABBREV + ")");
        writer.println();
        writer.println("Built at      : " + BuildProperties.BUILD_TIME + " with java version " + BuildProperties.BUILD_JDK_VERSION);
        writer.println("From commit at: " + BuildProperties.GIT_COMMIT_TIME + " (" + BuildProperties.GIT_COMMIT_ID + ")");
        writer.println("Running on    : java version \"" + BuildProperties.JAVA_VERSION + "\"");
        writer.println("                " + BuildProperties.JAVA_RUNTIME_NAME + " (build " + BuildProperties.JAVA_RUNTIME_VERSION + ")");
        writer.println("                " + BuildProperties.JAVA_VM_NAME +
                " (build " + BuildProperties.JAVA_VM_VERSION + ", " +
                System.getProperty("java.vm.info") + ")");
        writer.println("=======================");
        writer.println("Powered by Cloudhub");
    }

    private CommandHelper() {
    }
}
