package org.huel.cloudhub.web.controller.exception;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseBody;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.common.SystemRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import space.lingu.light.LightRuntimeException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Handle {@link Exception}
 *
 * @author RollW
 */
@ControllerAdvice
public class CloudhubSystemExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CloudhubSystemExceptionHandler.class);

    @ExceptionHandler(LightRuntimeException.class)
    @ResponseBody
    public HttpResponseEntity<String> handle(LightRuntimeException e) {
        logger.error("[SQL] Suspected SQL error. %s".formatted(e.toString()), e);
        return HttpResponseEntity.create(HttpResponseBody.failure(
                e.getMessage(),
                ErrorCode.getErrorFromThrowable(e),
                e.toString())
        );
    }

    @ExceptionHandler(SystemRuntimeException.class)
    @ResponseBody
    public HttpResponseEntity<String> handle(SystemRuntimeException e) {
        logger.error("System runtime error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.create(HttpResponseBody.failure(
                e.getMessage(),
                e.getErrorCode(),
                e.toString())
        );
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public HttpResponseEntity<String> handle(NullPointerException e) {
        logger.error("Null exception : %s".formatted(e.toString()), e);
        return HttpResponseEntity.create(HttpResponseBody.failure(
                e.getMessage(),
                ErrorCode.ERROR_NULL,
                e.toString())
        );
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    public HttpResponseEntity<String> handle(FileNotFoundException e) {
        return HttpResponseEntity.create(HttpResponseBody.failure(
                HttpStatus.NOT_FOUND,
                "404 Not found.",
                ErrorCode.ERROR_FILE_NOT_FOUND,
                "404 Not found.")
        );
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public HttpResponseEntity<String> handle(IOException e) {
        logger.error("Error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.create(HttpResponseBody.failure(
                HttpStatus.SERVICE_UNAVAILABLE,
                e.getMessage(),
                ErrorCode.getErrorFromThrowable(e),
                e.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpResponseEntity<String> handle(Exception e) {
        logger.error("Error: %s".formatted(e.toString()), e);
        return HttpResponseEntity.create(HttpResponseBody.failure(
                HttpStatus.SERVICE_UNAVAILABLE,
                e.getMessage(),
                ErrorCode.getErrorFromThrowable(e),
                e.toString())
        );
    }
}
