package org.huel.cloudhub.common;

import org.springframework.http.HttpStatus;

/**
 * @author RollW
 */
@SuppressWarnings("unchecked")
public class HttpResponseBody<D> {
    private static final HttpResponseBody<?> SUCCESS = new HttpResponseBody<>(
            HttpStatus.OK.value(), ErrorCode.SUCCESS.toString(), ErrorCode.SUCCESS);

    private final int status;
    private String message;
    private ErrorCode errorCode;

    private D data;

    private HttpResponseBody() {
        this.status = HttpStatus.OK.value();
    }

    public HttpResponseBody(int status, String message, ErrorCode errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    public HttpResponseBody(int status, String message,
                            ErrorCode errorCode, D data) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    public HttpResponseBody(HttpStatus status, String message, ErrorCode errorCode) {
        this.status = status.value();
        this.message = message;
        this.errorCode = errorCode;
    }

    public HttpResponseBody(HttpStatus status, String message,
                            ErrorCode errorCode, D data) {
        this.status = status.value();
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    private HttpResponseBody<D> setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    private HttpResponseBody<D> setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public D getData() {
        return data;
    }

    private HttpResponseBody<D> setData(D data) {
        this.data = data;
        return this;
    }

    public HttpResponseBody<D> fork() {
        return new HttpResponseBody<>(getStatus(), getMessage(), getErrorCode(), getData());
    }

    public static <D> HttpResponseBody<D> success() {
        return (HttpResponseBody<D>) SUCCESS;
    }

    public static <D> HttpResponseBody<D> success(String message) {
        return (HttpResponseBody<D>) success()
                .fork()
                .setMessage(message);
    }

    public static <D> HttpResponseBody<D> success(String message, D data) {
        return (HttpResponseBody<D>) success()
                .fork()
                .setMessage(message)
                .setData(data);
    }

    public static <D> HttpResponseBody<D> success(D data) {
        return (HttpResponseBody<D>) success()
                .fork()
                .setData(data);
    }

    // for semantic control

    public static <D> HttpResponseBody<D> failure(HttpStatus status,
                                                  String message,
                                                  ErrorCode errorCode,
                                                  D data) {
        return new HttpResponseBody<>(status, message, errorCode, data);
    }

    public static <D> HttpResponseBody<D> failure(HttpStatus status,
                                                  String message,
                                                  ErrorCode errorCode) {
        return new HttpResponseBody<>(status, message, errorCode);
    }

    public static <D> HttpResponseBody<D> failure(String message,
                                                  ErrorCode errorCode) {
        return new HttpResponseBody<>(errorCode.getStatus(), message, errorCode);
    }

    public static <D> HttpResponseBody<D> failure(String message,
                                                  ErrorCode errorCode,
                                                  D data) {
        return new HttpResponseBody<>(errorCode.getStatus(), message, errorCode, data);
    }
}
