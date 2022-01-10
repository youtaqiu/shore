package me.youm.reactor.common.exception;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 基础异常
 *
 * @author youta
 */
public class BaseException extends RuntimeException implements Serializable {
    protected String msg;
    protected int code;

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(int code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = code;
        this.msg = MessageFormat.format(msgFormat, args);
    }

    public BaseException() {
    }

    public String getMsg() {
        return this.msg;
    }

    public int getCode() {
        return this.code;
    }
}
