package org.huel.cloudhub.common;

import java.util.function.Function;

/**
 * @author RollW
 */
public record MessagePackage<D>(
        ErrorCode errorCode,
        String message,
        D data
) {
    public MessagePackage(ErrorCode code, D data) {
        this(code, code.toString(), data);
    }

    public HttpResponseBody<D> toResponseBody() {
        if (errorCode.getState()) {
            return HttpResponseBody.success(message(), data());
        }
        return HttpResponseBody.failure(message(), errorCode(), data());
    }

    public <T> HttpResponseBody<T> toResponseBody(Function<D, T> typeTrans) {
        if (errorCode.getState()) {
            return HttpResponseBody.success(message(), typeTrans.apply(data()));
        }
        return HttpResponseBody.failure(message(), errorCode(), typeTrans.apply(data()));
    }
}
