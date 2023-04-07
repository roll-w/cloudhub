package org.huel.cloudhub.web;

import java.util.function.Function;

/**
 * @author RollW
 */
@Deprecated
public record MessagePackage<D>(
        ErrorCode errorCode,
        String message,
        D data
) {
    public MessagePackage(ErrorCode code, D data) {
        this(code, code.toString(), data);
    }

    public HttpResponseBody<D> toResponseBody() {
        if (errorCode.success()) {
            return HttpResponseBody.success(message(), data());
        }
        return HttpResponseBody.of(errorCode(), message(), data());
    }

    public <T> HttpResponseBody<T> toResponseBody(Function<D, T> typeTrans) {
        if (errorCode.success()) {
            return HttpResponseBody.success(message(), typeTrans.apply(data()));
        }
        return HttpResponseBody.of(errorCode(), message(), typeTrans.apply(data()));
    }
}
