package org.huel.cloudhub.web.util;

import com.google.common.base.CaseFormat;
import org.huel.cloudhub.common.ErrorCode;

/**
 * @author RollW
 */
public class ErrorCodeKeyHelper {
    private static final String SUCCESS_KEY = "success";

    public static String getI18nKey(ErrorCode errorCode) {
        if (errorCode == null) {
            return null;
        }
        String name = errorCode.getName();
        String className = errorCode.getClass().getSimpleName();
        return convertToKeyName(className, name);
    }

    public static String convertToKeyName(String className, String errorCodeName) {
        if (errorCodeName.equalsIgnoreCase("SUCCESS")) {
            return SUCCESS_KEY;
        }
        String group = toGroupName(className);
        String errorCodeKey = toCodeKey(errorCodeName);
        return "error." + group + "." + errorCodeKey;
    }

    public static String toCodeKey(String errorCodeName) {
        if (errorCodeName.startsWith("ERROR_")) {
            return errorCodeName.substring(6).toLowerCase();
        }
        return errorCodeName.toLowerCase();
    }

    public static String toGroupName(String className) {
        String underscore = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
        if (underscore.equals("error_code")) {
            throw new IllegalStateException("Cannot get group from class name: " + className);
        }
        if (underscore.endsWith("error_code")) {
            return underscore.substring(0, underscore.length() - 11);
        }
        return underscore;
    }

    private ErrorCodeKeyHelper() {
    }
}
