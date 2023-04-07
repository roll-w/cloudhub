package org.huel.cloudhub.web;

import java.util.Locale;

/**
 * @author RollW
 */
public interface ErrorCodeMessageProvider {
    default String getMessage(ErrorCode errorCode) {
        return getMessage(errorCode, Locale.CHINA);
    }

    default String getMessage(ErrorCode errorCode, Object... args) {
        return getMessage(errorCode, Locale.CHINA, args);
    }

    default String getMessage(ErrorCode errorCode, Locale locale) {
        return apply(errorCode, locale);
    }

    default String getMessage(ErrorCode errorCode, Locale locale, Object... args) {
        return apply(errorCode, locale, args);
    }

    // internal method
    default String apply(ErrorCode errorCode,
                         Locale locale,
                         Object... args) {
        return errorCode.toString();
    }
}
