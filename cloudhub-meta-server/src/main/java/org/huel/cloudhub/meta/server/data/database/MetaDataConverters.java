package org.huel.cloudhub.meta.server.data.database;

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
