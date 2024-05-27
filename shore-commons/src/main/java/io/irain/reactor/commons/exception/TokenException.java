package io.irain.reactor.commons.exception;

import java.io.Serial;
import java.text.MessageFormat;

/**
 * token exception.
 *
 * @author youta
 **/
public class TokenException extends ServerException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 国际化key
     */
    private static final String MESSAGE_KEY = "unauthorized";

    /**
     * 错误信息
     */
    protected String msg;
    /**
     * 错误码
     */
    protected int code;

    /**
     * 获取错误key
     *
     * @return 错误key
     */
    public String getKey() {
        return MESSAGE_KEY;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getErrorCode() {
        return 401;
    }

    /**
     * 获取错误信息
     *
     * @param msgFormat 消息格式
     * @param args      参数
     */
    public TokenException(String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = 401;
        this.msg = MessageFormat.format(msgFormat, args);
    }


    /**
     * 构造一个应用基础异常
     */
    public TokenException() {
        super(401, MESSAGE_KEY);
    }

}
