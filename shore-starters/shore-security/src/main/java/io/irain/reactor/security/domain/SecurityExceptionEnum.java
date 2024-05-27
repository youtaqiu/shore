package io.irain.reactor.security.domain;

import io.irain.reactor.commons.exception.ServerFailure;
import lombok.AllArgsConstructor;

/**
 * @author youta
 **/
@AllArgsConstructor
public enum SecurityExceptionEnum implements ServerFailure {

    /**
     * username not found
     */
    USERNAME_NOT_FOUND(1001, "Username not found"),

    /**
     * password not match
     */
    PASSWORD_NOT_MATCH(1002, "Password not match"),
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
