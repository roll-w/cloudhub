package org.huel.cloudhub.client.disk.database;

import org.huel.cloudhub.client.disk.domain.storagepermission.PermissionType;
import space.lingu.light.DataConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

    @DataConverter
    public static String convertToPermissionType(List<PermissionType> permissionTypes) {
        StringJoiner joiner = new StringJoiner(",");
        for (PermissionType permissionType : permissionTypes) {
            joiner.add(permissionType.name());
        }
        return joiner.toString();
    }

    @DataConverter
    public static List<PermissionType> convertFromPermissionType(String s) {
        String[] split = s.split(",");
        List<PermissionType> permissionTypes = new ArrayList<>();
        for (String s1 : split) {
            permissionTypes.add(PermissionType.valueOf(s1));
        }
        return permissionTypes;
    }

    private DiskConverter() {
    }
}
