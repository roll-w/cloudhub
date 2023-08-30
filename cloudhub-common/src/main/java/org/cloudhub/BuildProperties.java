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

package org.cloudhub;

import space.lingu.Nullable;

import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Provides build information.
 *
 * @author RollW
 */
public final class BuildProperties {
    @Nullable
    private static final Manifest MANIFEST = loadManifest();

    public static final String GIT = "https://github.com/Roll-W/cloudhub";

    public static final String VERSION = loadManifestAttribute("Implementation-Version");

    public static final String BUILD_TIME = loadManifestAttribute("Built-At");

    public static final String GIT_COMMIT_ID = loadManifestAttribute("Git-Commit-Id");

    public static final String GIT_COMMIT_ID_ABBREV = loadManifestAttribute("Git-Commit-Id-Abbrev");

    public static final String GIT_COMMIT_TIME = loadManifestAttribute("Git-Commit-Time");

    public static final String BUILD_JDK_VERSION = loadManifestAttribute("Build-Jdk-Version");

    public static final String JAVA_VERSION = System.getProperty("java.version");

    public static final String JAVA_RUNTIME_NAME = System.getProperty("java.runtime.name");

    public static final String JAVA_RUNTIME_VERSION = System.getProperty("java.runtime.version");

    public static final String JAVA_VM_NAME = System.getProperty("java.vm.name");

    public static final String JAVA_VM_VERSION = System.getProperty("java.vm.version");

    private static Manifest loadManifest() {
        try {
            return new Manifest(BuildProperties.class.getResourceAsStream(
                    "/META-INF/MANIFEST.MF"));
        } catch (IOException e) {
            return null;
        }
    }

    private static String loadManifestAttribute(String name) {
        if (MANIFEST == null) {
            return null;
        }
        return MANIFEST.getMainAttributes().getValue(name);
    }

    private BuildProperties() {
    }
}
