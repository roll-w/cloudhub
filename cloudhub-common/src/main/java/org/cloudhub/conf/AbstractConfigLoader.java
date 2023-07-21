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

package org.cloudhub.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author RollW
 */
public class AbstractConfigLoader {
    private final Properties properties;

    public AbstractConfigLoader(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    public String get(String key) {
        return properties.getProperty(key, null);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    protected int getInt(String key, String defaultValue) {
        return Integer.parseInt(properties.getProperty(key, defaultValue));
    }

    protected int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    protected static InputStream openConfigInput(Class<?> appClz) throws IOException {
        File confFile = tryFile();
        if (!confFile.exists()) {
            return appClz.getResourceAsStream("/cloudhub.conf");
        }
        return new FileInputStream(confFile);
    }

    private static File tryFile() {
        File confFile = new File("conf/cloudhub.conf");
        if (confFile.exists()) {
            return confFile;
        }
        return new File("cloudhub.conf");
    }
}
