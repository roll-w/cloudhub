package org.huel.cloudhub.client.disk.database;

import space.lingu.light.DataConverter;

/**
 * @author RollW
 */
public class DiskConverter {

    @DataConverter
    public static long[] convertFrom(String s) {
        String[] split = s.split(",");
        long[] result = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            result[i] = Long.parseLong(split[i]);
        }
        return result;
    }

    @DataConverter
    public static String convertTo(long[] array) {
        StringBuilder builder = new StringBuilder();
        for (long l : array) {
            builder.append(l).append(",");
        }
        return builder.toString();
    }

    private DiskConverter() {
    }
}
