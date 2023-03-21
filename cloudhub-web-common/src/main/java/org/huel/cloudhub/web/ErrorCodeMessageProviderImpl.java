package org.huel.cloudhub.web;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.ErrorCodeMessageProvider;
import org.huel.cloudhub.web.util.ErrorCodeKeyHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * @author RollW
 */
public class ErrorCodeMessageProviderImpl implements ErrorCodeMessageProvider {
    private final MessageSource messageSource;

    public ErrorCodeMessageProviderImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String apply(ErrorCode errorCode, Locale locale, Object... args) {
        if (errorCode == null) {
            return null;
        }
        String key = getI18nKey(errorCode);
        try {
            if (locale == null) {
                return messageSource.getMessage(key, args, Locale.getDefault());
            }
            return messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    private String getI18nKey(ErrorCode errorCode) {
        return ErrorCodeKeyHelper.getI18nKey(errorCode);
    }
}
