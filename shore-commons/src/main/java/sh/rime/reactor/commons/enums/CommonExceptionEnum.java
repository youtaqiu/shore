package sh.rime.reactor.commons.enums;

import sh.rime.reactor.commons.exception.ServerFailure;
import lombok.AllArgsConstructor;

/**
 * CommonExceptionEnum is an enum that represents different types of common exceptions, such as unauthorized, forbidden, bad request, etc.
 * Each enum value represents a specific exception type and includes an error code and a message.
 * This enum implements the ServerFailure interface, which means it provides methods for retrieving the error code and message.
 *
 * @author youta
 */
@AllArgsConstructor
public enum CommonExceptionEnum implements ServerFailure {

    /**
     * unauthorized
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * forbidden
     */
    FORBIDDEN(403, "Access Denied"),

    /**
     * 400
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * sign header not found
     */
    SIGN_HEADER_NOT_FOUND(4001, "Sign header param not found"),


    /**
     * 文件大小超限
     */
    FILE_SIZE_TOO_LONG_ERROR(7001, "文件大小超限"),

    /**
     * 存储空间不存在
     */
    BUCKET_NOT_EXIST(7002, "存储空间不存在"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(7003, "文件上传失败"),

    /**
     * 阿里云oss错误
     */
    ALI_YUN_OSS_ERROR(7004, "阿里云oss错误"),

    /**
     * oss复制文件错误
     */
    OSS_COPY_OBJECT_ERROR(7005, "oss复制文件错误"),

    /**
     * 客户端信息未找到
     */
    CLIENT_NOT_FOUND_ERROR(8001, "客户端信息未找到"),

    /**
     * 登出token解析失败
     */
    LOGOUT_TOKEN_ERROR(1001, "Logout failed"),

    /**
     * 登录请求体解析失败
     */
    LOGIN_BODY_PARSE_ERROR(1002, "Login failed"),

    /**
     * 登录令牌缓存失败
     */
    LOGIN_TOKEN_CACHE_ERROR(1003, "Login failed"),
    ;

    private final int code;

    private final String msg;

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return msg;
    }

}
