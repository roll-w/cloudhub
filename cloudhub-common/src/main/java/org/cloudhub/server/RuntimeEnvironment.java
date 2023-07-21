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

package org.cloudhub.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author RollW
 */
public class RuntimeEnvironment {
    public static final String UNKNOWN = "[UNKNOWN]";

    private final String hostName;
    private final String hostAddress;
    private final String runUser;
    private final String userHome;
    private final String workDir;
    private final String javaVersion;
    private final String javaHome;
    private final String osName;
    private final String osVersion;
    private final String osArch;

    public RuntimeEnvironment(String hostName, String hostAddress,
                              String runUser, String userHome,
                              String workDir, String javaVersion,
                              String javaHome,
                              String osName, String osVersion,
                              String osArch) {
        this.hostName = hostName;
        this.hostAddress = hostAddress;
        this.runUser = runUser;
        this.userHome = userHome;
        this.workDir = workDir;
        this.javaVersion = javaVersion;
        this.javaHome = javaHome;
        this.osName = osName;
        this.osVersion = osVersion;
        this.osArch = osArch;
    }

    public String getRunUser() {
        return runUser;
    }

    public String getUserHome() {
        return userHome;
    }

    public String getWorkDir() {
        return workDir;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getOsName() {
        return osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public static RuntimeEnvironment load() {
        String hostName, address;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
            address = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            hostName = UNKNOWN;
            address = UNKNOWN;
        }

        final String user = System.getProperty("user.name");
        final String userHome = System.getProperty("user.home");
        final String workDir = System.getProperty("user.dir");
        final String javaVersion = System.getProperty("java.version");
        final String javaHome = System.getProperty("java.home");
        final String osName = System.getProperty("os.name");
        final String osVersion = System.getProperty("os.version");
        final String osArch = System.getProperty("os.arch");
        return new RuntimeEnvironment(
                hostName, address, user,
                userHome, workDir, javaVersion,
                javaHome, osName, osVersion, osArch);
    }
}
