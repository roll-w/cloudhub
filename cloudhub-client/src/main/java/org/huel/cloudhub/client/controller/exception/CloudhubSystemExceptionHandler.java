package org.huel.cloudhub.client.controller.exception;

import com.google.common.base.Strings;
import com.google.common.base.VerifyException;
import org.huel.cloudhub.common.BusinessRuntimeException;
import org.huel.cloudhub.common.CommonErrorCode;
import org.huel.cloudhub.common.ErrorCodeFinder;
import org.huel.cloudhub.common.IoErrorCode;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import space.lingu.light.LightRuntimeException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * Handle {@link Exception}
 *
 * @author RollW
 */
@ControllerAdvice
public class CloudhubSystemExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CloudhubSystemExceptionHandler.class);

    private final ErrorCodeFinder errorCodeFinder;
    private final MessageSource messageSource;

    public CloudhubSystemExceptionHandler(ErrorCodeFinder errorCodeFinder,
                                          MessageSource messageSource) {
        this.errorCodeFinder = errorCodeFinder;
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public HttpResponseEntity<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        HttpMethod method = HttpMethod.resolve(e.getHttpMethod());
        if (method == HttpMethod.OPTIONS) {
            return new HttpResponseEntity<>(HttpStatus.OK);
        }
        return HttpResponseEntity.of(
                CommonErrorCode.ERROR_NOT_FOUND,
                e.getMessage()
        );
    }


    @ExceptionHandler(LightRuntimeException.class)
    public HttpResponseEntity<Void> handle(LightRuntimeException e) {
        logger.error("Unexpected sql error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.of(
                errorCodeFinder.fromThrowable(e),
                e.getMessage()
        );
    }

    @ExceptionHandler({
            BindException.class,
            MethodArgumentNotValidException.class,
    })
    public HttpResponseEntity<Void> handleParamException(Exception e) {
        return HttpResponseEntity.of(
                WebCommonErrorCode.ERROR_PARAM_FAILED,
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public HttpResponseEntity<Void> handleMethodTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        return HttpResponseEntity.of(
                WebCommonErrorCode.ERROR_PARAM_FAILED,
                e.getMessage()
        );
    }

    @ExceptionHandler(BusinessRuntimeException.class)
    public HttpResponseEntity<Void> handle(BusinessRuntimeException e) {
        logger.trace("System runtime error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.of(
                errorCodeFinder.fromThrowable(e),
                tryGetMessage(e)
        );
    }

    private String tryGetMessage(BusinessRuntimeException e) {
        if (Strings.isNullOrEmpty(e.getMessage())) {
            return null;
        }
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(
                    e.getMessage(),
                    e.getArgs(),
                    locale
            );
        } catch (Exception ex) {
            return tryFormatMessage(e.getMessage(), e.getArgs());
        }
    }

    private String tryFormatMessage(String message, Object[] args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return MessageFormat.format(message, args);
        } catch (Exception e) {
            return message;
        }
    }

    @ExceptionHandler(NullPointerException.class)
    public HttpResponseEntity<Void> handle(NullPointerException e) {
        logger.error("Null exception : %s".formatted(e.toString()), e);
        return HttpResponseEntity.of(
                CommonErrorCode.ERROR_NULL,
                e.getMessage()
        );
    }

    // treat VerifyException as parameter failed
    @ExceptionHandler(VerifyException.class)
    public HttpResponseEntity<Void> handle(VerifyException e) {
        return HttpResponseEntity.of(
                WebCommonErrorCode.ERROR_PARAM_FAILED,
                e.getMessage()
        );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public HttpResponseEntity<Void> handle(FileNotFoundException e) {
        return HttpResponseEntity.of(
                IoErrorCode.ERROR_FILE_NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(IOException.class)
    public HttpResponseEntity<Void> handle(IOException e) {
        logger.error("IO Error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.of(
                errorCodeFinder.fromThrowable(e),
                e.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public HttpResponseEntity<Void> handleAuthException(AccessDeniedException e) {
        return HttpResponseEntity.of(AuthErrorCode.ERROR_UNAUTHORIZED_USE);
    }

    @ExceptionHandler(DisabledException.class)
    public HttpResponseEntity<Void> handleAuthException(DisabledException e) {
        return HttpResponseEntity.of(UserErrorCode.ERROR_USER_DISABLED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public HttpResponseEntity<Void> handleAuthException(AuthenticationException e) {
        if (e instanceof InsufficientAuthenticationException) {
            return HttpResponseEntity.of(AuthErrorCode.ERROR_TOKEN_EXPIRED);
        }
        logger.error("Auth Error: %s, type: %s".formatted(e.toString(), e.getClass().getCanonicalName()), e);
        return HttpResponseEntity.of(AuthErrorCode.ERROR_NOT_HAS_ROLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HttpResponseEntity<Void> handleHttpMessageNotReadableException() {
        return HttpResponseEntity.of(WebCommonErrorCode.ERROR_BODY_MISSING);
    }

    @ExceptionHandler(Exception.class)
    public HttpResponseEntity<Void> handle(Exception e) {
        logger.error("Error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.of(
                errorCodeFinder.fromThrowable(e),
                e.getMessage()
        );
    }
}
