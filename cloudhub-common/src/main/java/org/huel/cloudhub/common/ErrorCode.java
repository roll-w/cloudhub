package org.huel.cloudhub.common;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.IllegalFormatException;

/**
 * @author RollW
 */
@JsonSerialize(using = ErrorCode.ErrorCodeSerializer.class)
@JsonDeserialize(using = ErrorCode.ErrorCodeDeserializer.class)
public enum ErrorCode implements ErrorEnum {
    // 有需要在这里添加错误码
    
    
    /**
     * 成功
     */
    SUCCESS("00000"),
    /**
     * 注册错误
     */
    ERROR_REGISTER("A0100"),
    /**
     * 用户已存在
     */
    ERROR_USER_EXISTED("A0111"),
    /**
     * 用户已登录
     */
    ERROR_USER_ALREADY_LOGIN("A0112"),
    /**
     * 用户已激活
     */
    ERROR_USER_ALREADY_ACTIVATED("A0113"),
    /**
     * 令牌过期
     */
    ERROR_TOKEN_EXPIRED("A0150"),
    /**
     * 令牌未过期
     */
    ERROR_TOKEN_NOT_EXPIRED("A0151"),
    /**
     * 令牌不存在
     */
    ERROR_TOKEN_NOT_EXIST("A0152"),
    /**
     * 令牌已使用
     */
    ERROR_TOKEN_USED("A0153"),
    /**
     * 用户不存在
     */
    ERROR_USER_NOT_EXIST("A0201"),
    /**
     * 用户未登录
     */
    ERROR_USER_NOT_LOGIN("A0202", 403), // 403 Forbidden.
    /**
     * 密码错误
     */
    ERROR_PASSWORD_NOT_CORRECT("A0210"),
    /**
     * 密码不合规，校验错误
     */
    ERROR_PASSWORD_NON_COMPLIANCE("A0211"),
    /**
     * 用户名不合规
     */
    ERROR_USERNAME_NON_COMPLIANCE("A0212"),
    /**
     * 邮件名不合规
     */
    ERROR_EMAIL_NON_COMPLIANCE("A0213"),
    /**
     * 邮件已存在
     */
    ERROR_EMAIL_EXISTED("A0214"),
    /**
     * 权限错误
     */
    ERROR_PERMISSION("A0300", 401),
    /**
     * 权限不允许
     */
    ERROR_PERMISSION_NOT_ALLOWED("A0310", 401),
    /**
     * 登陆状态过期
     */
    ERROR_LOGIN_EXPIRED("A0311"),

    /**
     * 用量上限
     */
    ERROR_USAGE_LIMIT("A0401"),
    /**
     * 请求次数过多
     */
    ERROR_REQUEST_OVERRUN("A0501", 429),// TOO MANY REQUESTS
    /**
     * 上传文件错误
     */
    ERROR_FILE_UPLOAD("A0700"),
    /**
     * 上传文件类型错误
     */
    ERROR_FILE_UNMATCHED("A0701", 400),// BAD REQUEST
    /**
     * 文件过大
     */
    ERROR_FILE_OVERSIZE("A0702"),
    /**
     * 文件已存在
     */
    ERROR_FILE_EXISTED("A0703"),
    /**
     * 文件未找到
     */
    ERROR_FILE_NOT_FOUND("A0704", 404),
    /**
     * 文件相关错误
     */
    ERROR_FILE("A0705"),
    /**
     * 运行错误
     */
    ERROR_RUNTIME("B0001"),
    /**
     * 运行超时
     */
    ERROR_RUNTIME_OVERTIME("B0002"),
    /**
     * 缺少参数
     */
    ERROR_PARAM_MISSING("B0003"),
    /**
     * 非法参数
     */
    ERROR_ILLEGAL_PARAM("B0004"),
    /**
     * 数据库错误
     */
    ERROR_DATABASE("C0300"),
    /**
     * 表不存在
     */
    ERROR_TABLE_NOT_EXIST("C0311"),
    /**
     * 列（列族）不存在
     */
    ERROR_COLUMN_NOT_EXIST("C0312"),
    /**
     * 表已存在
     */
    ERROR_TABLE_EXISTED("C0313"),
    /**
     * 列（列族）已存在
     */
    ERROR_COLUMN_EXISTED("C0314"),
    /**
     * 表已被禁用
     */
    ERROR_TABLE_DISABLED("C0315"),
    /**
     * 数据已存在
     */
    ERROR_DATA_EXISTED("C0316"),
    /**
     * 数据不存在
     */
    ERROR_DATA_NOT_EXIST("C0317", 404),
    /**
     * 数据非空
     */
    ERROR_DATA_NOT_EMPTY("D0005", 403),

    // 对应Exception
    ERROR_ARRAY_OUT_BOUND("D0001", 500),
    ERROR_NULL("D0002", 500),
    ERROR_CLASS_CAST("D0003", 500),
    ERROR_ILLEGAL_ARGUMENT("D0004", 500),
    ERROR_ILLEGAL_STATE("D0005", 500),
    ERROR_PARSE("D0006", 500),
    ERROR_ILLEGAL_THREAD_STATE("D0007", 500),
    ERROR_ILLEGAL_ACCESS("D0008", 500),
    ERROR_ILLEGAL_FORMAT("D0009", 500),
    ERROR_IO("D0010", 500),
    /**
     * 其他未归类错误
     */
    ERROR_EXCEPTION("D0000", 500);

    private final String value;
    private final int status;

    ErrorCode(String value) {
        this(value, 200);
    }

    ErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 由{@link ErrorCode}获得布尔值状态
     *
     * @return Error值成功为 {@code true}
     */
    public boolean getState() {
        return this == SUCCESS;
    }

    /**
     * 由布尔值获取 {@link ErrorCode}
     *
     * @param state 状态
     * @return {@code true} 返回{@link #SUCCESS}， 否则只返回{@link #ERROR_EXCEPTION}
     */
    public static ErrorCode getErrorByBoolean(boolean state) {
        if (state) {
            return SUCCESS;
        }
        return ERROR_EXCEPTION;
    }

    /**
     * 由名称得到{@link ErrorCode}枚举类
     *
     * @param error 错误码名称
     * @return {@link ErrorCode}枚举类
     */
    public static ErrorCode getErrorByName(String error) {
        for (ErrorCode e : values()) {
            if (e.name().equals(error)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 由错误码得到{@link ErrorCode}枚举类
     *
     * @param error 错误码
     * @return {@link ErrorCode}枚举类
     */
    public static ErrorCode getErrorByValue(String error) {
        for (ErrorCode code : values()) {
            if (code.value.equalsIgnoreCase(error)) {
                return code;
            }
        }
        return null;
    }

    /**
     * 由{@link Exception}得到{@link ErrorCode}
     *
     * @param exception {@link Exception}
     * @return {@link ErrorCode}
     */
    public static ErrorCode getErrorFromThrowable(Throwable exception) {
        if (exception instanceof FileNotFoundException) {
            return ErrorCode.ERROR_FILE_NOT_FOUND;
        }
        if (exception instanceof IOException) {
            return ErrorCode.ERROR_IO;
        }
        if (exception instanceof NullPointerException) {
            return ErrorCode.ERROR_NULL;
        }
        if (exception instanceof ParseException) {
            return ErrorCode.ERROR_PARSE;
        }
        if (exception instanceof MissingServletRequestParameterException) {
            return ErrorCode.ERROR_PARAM_MISSING;
        }
        if (exception instanceof IllegalThreadStateException) {
            return ErrorCode.ERROR_ILLEGAL_THREAD_STATE;
        }
        if (exception instanceof IllegalAccessException) {
            return ErrorCode.ERROR_ILLEGAL_ACCESS;
        }
        if (exception instanceof IllegalFormatException) {
            return ErrorCode.ERROR_ILLEGAL_FORMAT;
        }
        if (exception instanceof IllegalArgumentException) {
            return ErrorCode.ERROR_ILLEGAL_ARGUMENT;
        }
        if (exception instanceof ClassCastException) {
            return ErrorCode.ERROR_CLASS_CAST;
        }
        if (exception instanceof IllegalStateException) {
            return ErrorCode.ERROR_ILLEGAL_STATE;
        }
        if (exception instanceof RuntimeException) {
            return ErrorCode.ERROR_RUNTIME;
        }
        return ErrorCode.ERROR_EXCEPTION;
    }

    @Override
    public String toString() {
        if (this == ErrorCode.SUCCESS) {
            return "SUCCESS";
        }
        return "Error: " + this.name() + ", Error Code: " + value;
    }

    public static class ErrorCodeSerializer extends StdSerializer<ErrorCode> {
        protected ErrorCodeSerializer() {
            super(ErrorCode.class);
        }

        @Override
        public void serialize(ErrorCode value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(value.getValue());
        }
    }

    public static class ErrorCodeDeserializer extends StdDeserializer<ErrorCode> {
        protected ErrorCodeDeserializer() {
            super(ErrorCode.class);
        }

        @Override
        public ErrorCode deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
            String code = parser.getValueAsString();
            return getErrorByValue(code);
        }
    }
}
