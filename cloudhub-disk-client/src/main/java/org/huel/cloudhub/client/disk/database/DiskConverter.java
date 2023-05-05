package org.huel.cloudhub.client.disk.database;

import org.huel.cloudhub.client.disk.domain.storagepermission.PermissionType;
import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;
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

    @DataConverter
    public static String[] convertToArray(String s) {
        return s.split(",");
    }

    @DataConverter
    public static String convertToString(String[] array) {
        StringJoiner joiner = new StringJoiner(",");
        for (String s : array) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    @DataConverter
    public static List<TagKeyword> convertToKeywords(String s) {
        // name^value#name^value
        List<TagKeyword> keywords = new ArrayList<>();
        String[] split = s.split("#");
        for (String s1 : split) {
            String[] split1 = s1.split("\\^");
            keywords.add(new TagKeyword(split1[0], Integer.parseInt(split1[1])));
        }
        return keywords;
    }

    @DataConverter
    public static String convertFromKeywords(List<TagKeyword> keywords) {
        // name^value#name^value
        StringBuilder builder = new StringBuilder();
        for (TagKeyword keyword : keywords) {
            builder.append(keyword.name()).append("^")
                    .append(keyword.weight())
                    .append("#");
        }
        return builder.toString();
    }

    private DiskConverter() {
    }
}
