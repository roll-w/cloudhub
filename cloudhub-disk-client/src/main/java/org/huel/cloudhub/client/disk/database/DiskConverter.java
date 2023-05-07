package org.huel.cloudhub.client.disk.database;

import com.google.common.base.Strings;
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

    private static final long[] EMPTY_LONG_ARRAY = new long[0];

    @DataConverter
    public static long[] convertFrom(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return EMPTY_LONG_ARRAY;
        }
        String[] split = s.split(",");
        long[] result = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                result[i] = Long.parseLong(split[i]);
            } catch (NumberFormatException e) {
                return EMPTY_LONG_ARRAY;
            }
        }
        return result;
    }

    @DataConverter
    public static String convertTo(long[] array) {
        if (array == null || array.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (long l : array) {
            builder.append(l).append(",");
        }
        return builder.toString();
    }

    @DataConverter
    public static String convertToPermissionType(List<PermissionType> permissionTypes) {
        if (permissionTypes == null || permissionTypes.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(",");
        for (PermissionType permissionType : permissionTypes) {
            joiner.add(permissionType.name());
        }
        return joiner.toString();
    }

    @DataConverter
    public static List<PermissionType> convertFromPermissionType(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return List.of();
        }
        String[] split = s.split(",");
        List<PermissionType> permissionTypes = new ArrayList<>();
        for (String s1 : split) {
            permissionTypes.add(PermissionType.valueOf(s1));
        }
        return permissionTypes;
    }

    @DataConverter
    public static String[] convertToArray(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return new String[0];
        }

        return s.split(",");
    }

    @DataConverter
    public static String convertToString(String[] array) {
        if (array == null || array.length == 0) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(",");
        for (String s : array) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    @DataConverter
    public static List<TagKeyword> convertToKeywords(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return List.of();
        }

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
        if (keywords == null || keywords.isEmpty()) {
            return "";
        }

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
