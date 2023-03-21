package org.huel.cloudhub.client.controller.common;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.ErrorCodeFinder;
import org.huel.cloudhub.common.ErrorCodeMessageProvider;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * @author RollW
 */
@RestController
@CommonApi
public class ErrorCodeMessageController {
    private final ErrorCodeMessageProvider errorCodeMessageProvider;
    private final ErrorCodeFinder errorCodeFinder;

    public ErrorCodeMessageController(ErrorCodeMessageProvider errorCodeMessageProvider,
                                      ErrorCodeFinder errorCodeFinder) {
        this.errorCodeMessageProvider = errorCodeMessageProvider;
        this.errorCodeFinder = errorCodeFinder;
    }

    @GetMapping("/code")
    public String getErrorCodeName(@RequestParam String code) {
        return errorCodeFinder.findErrorCode(code).getName();
    }

    @GetMapping("/code/message")
    public String getErrorCodeMessage(@RequestParam String code) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorCode errorCode = errorCodeFinder.findErrorCode(code);
        return errorCodeMessageProvider.getMessage(errorCode, locale);
    }
}
