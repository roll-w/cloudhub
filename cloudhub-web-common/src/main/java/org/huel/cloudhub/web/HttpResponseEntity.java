package org.huel.cloudhub.web;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * Http响应实体。携带Http状态码及Header。
 *
 * @author RollW
 */
public class HttpResponseEntity<D> extends ResponseEntity<HttpResponseBody<D>> {
    public HttpResponseEntity(HttpStatus status) {
        super(status);
    }

    public HttpResponseEntity(int status) {
        super(HttpStatus.valueOf(status));
    }

    public HttpResponseEntity(HttpResponseBody<D> body) {
        super(body, null, body.getStatus());
    }

    public HttpResponseEntity(HttpResponseBody<D> body,
                              MultiValueMap<String, String> headers) {
        super(body, headers, body.getStatus());
    }

    public HttpResponseEntity<D> fork() {
        return new HttpResponseEntity<>(getBody(), getHeaders());
    }

    public HttpResponseEntity<D> fork(HttpResponseBody<D> newResponseBody) {
        return new HttpResponseEntity<>(newResponseBody, getHeaders());
    }

    public static class Builder<D> {
        private Integer status;
        private String message;
        private ErrorCode errorCode;
        private String tip;
        private D data;

        public Builder<D> status(HttpStatus status) {
            this.status = status.value();
            return this;
        }

        public Builder<D> status(int status) {
            this.status = status;
            return this;
        }

        public Builder<D> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<D> errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder<D> tip(String tip) {
            this.tip = tip;
            return this;
        }

        public Builder<D> data(D data) {
            this.data = data;
            return this;
        }

        public HttpResponseEntity<D> build() {
            return new HttpResponseEntity<>(
                    HttpResponseBody.of(errorCode, status, message, tip, data)
            );
        }
    }

    public static <D> HttpResponseEntity<D> of(HttpResponseBody<D> body) {
        return new HttpResponseEntity<>(body);
    }

    public static <D> HttpResponseEntity<D> success() {
        return of(
                HttpResponseBody.success()
        );
    }

    public static <D> HttpResponseEntity<D> success(String message, D data) {
        return of(
                HttpResponseBody.success(message, data)
        );
    }

    public static <D> HttpResponseEntity<D> success(D data) {
        return of(
                HttpResponseBody.success(data)
        );
    }

    public static <D> HttpResponseEntity<D> of(ErrorCode errorCode,
                                               String message) {
        return of(
                HttpResponseBody.of(errorCode, message)
        );
    }

    public static <D> HttpResponseEntity<D> of(ErrorCode errorCode,
                                               String message,
                                               String tip) {
        return of(
                HttpResponseBody.<D>builder()
                        .errorCode(errorCode)
                        .message(message)
                        .tip(tip)
                        .build()
        );
    }


    public static <D> HttpResponseEntity<D> of(ErrorCode errorCode) {
        return of(
                HttpResponseBody.of(errorCode)
        );
    }

    public static <D> HttpResponseEntity<D> of(ErrorCode errorCode,
                                               D data) {
        return of(
                HttpResponseBody.builder(data)
                        .errorCode(errorCode)
                        .build()
        );
    }
}
