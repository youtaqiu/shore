package me.youm.reactor.common.enums;

import lombok.AllArgsConstructor;


/**
 * @author youta
 */
@AllArgsConstructor
public enum CommonExceptionEnum implements EnumInterface {

    /**
     * 通用异常枚举
     */
    INVALID_PARAM(4000, "invalid param [{0}] -> [{1}]"),
    PARAM_ERROR(4000, "param error"),
    PARAM_TYPE_ERROR(4001, "param error: [Some parameter type does not match]"),
    PARAM_EMPTY_ERROR(4002, "param error: [The required parameter cannot be empty]"),
    PARAM_LENGTH_ERROR(4003, "param error: [The parameter length exceeds the limit]"),
    FEIGN_REMOTE_ERROR(5000, "feign remote service error"),
    FEIGN_SERVER_NOT_FOUND_ERROR(5001, "feign remote service error: [Server not found]"),
    RSOCKET_REMOTE_ERROR(6000, "rsocket remote service error"),
    RSOCKET_SERVER_NOT_FOUND_ERROR(6001, "rsocket remote service error: [Server not found]"),
    RSOCKET_SERVER_INVOKED_ERROR(6002, "rsocket remote service error: [Server invoked failed]"),
    ;

    private final int code;

    private final String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
