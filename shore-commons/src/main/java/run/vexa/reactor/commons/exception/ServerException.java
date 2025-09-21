package run.vexa.reactor.commons.exception;


import lombok.Getter;

import java.io.Serial;
import java.text.MessageFormat;

/**
 * ServerException is a class that represents server exceptions.
 * It includes an error code and an error message.
 * This class extends RuntimeException, which means it represents exceptions that can be thrown during the normal operation of the Java Virtual Machine.
 * ServerException is also serializable, which means it can be converted to a byte stream and restored later.
 *
 * @author youta
 */
@Getter
public class ServerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The default error message.
     */
    public static final String DEFAULT_MSG;

    /**
     * The error code.
     */
    private final int errorCode;

    static {
        DEFAULT_MSG = "The server is busy, please try again later.";
    }

    /**
     * 构造一个应用基础异常.
     */
    public ServerException() {
        this(500);
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param errorCode 错误码
     */
    public ServerException(int errorCode) {
        super(DEFAULT_MSG);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param message            异常信息
     * @param cause              导致的原因
     * @param enableSuppression  启用抑制
     * @param writableStackTrace 写入异常栈
     */
    public ServerException(String message, Throwable cause, boolean enableSuppression,
                           boolean writableStackTrace) {
        this(message, cause, enableSuppression, writableStackTrace, 500);
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param message            异常信息
     * @param cause              导致的原因
     * @param enableSuppression  启用抑制
     * @param writableStackTrace 写入异常栈
     * @param errorCode          错误码
     */
    public ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                           int errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param message 异常信息
     * @param cause   导致的原因
     */
    public ServerException(String message, Throwable cause) {
        this(message, cause, 500);
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param message   异常信息
     * @param cause     导致的原因
     * @param errorCode 错误码
     */
    public ServerException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param message 异常信息
     */
    public ServerException(String message) {
        this(message, 500);
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param message   异常信息
     * @param errorCode 错误码
     */
    public ServerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    /**
     * 构造一个应用基础异常.
     *
     * @param message   异常信息
     * @param errorCode 错误码
     */
    public ServerException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param failure 异常枚举
     * @param args    参数
     */
    public ServerException(ServerFailure failure, Object... args) {
        super(MessageFormat.format(failure.message(), args));
        this.errorCode = failure.code();
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param cause 导致的原因
     */
    public ServerException(Throwable cause) {
        this(cause, 500);
    }

    /**
     * 构造一个应用基础异常.
     *
     * @param cause     导致的原因
     * @param errorCode 错误码
     */
    public ServerException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

}
