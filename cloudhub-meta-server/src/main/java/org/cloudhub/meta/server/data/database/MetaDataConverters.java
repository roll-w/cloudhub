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

package org.cloudhub.meta.server.data.database;

import space.lingu.light.DataConverter;

import java.util.StringJoiner;
import java.util.StringTokenizer;

/**
 * @author RollW
 */
public final class MetaDataConverters {
    @DataConverter
    public static String convertsStringArray(final String[] strings) {
        if (strings == null) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(",");
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @DataConverter
    public static String[] convertsStringArrayFrom(final String string) {
        if (string == null || string.isEmpty()) {
            return EMPTY_STRING_ARRAY;
        }

        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        final int size = stringTokenizer.countTokens();
        String[] res = new String[size];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            res[i] = stringTokenizer.nextToken();
            i++;
        }
        return res;
    }

    private MetaDataConverters() {
    }
}
