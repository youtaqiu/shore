package me.youm.reactor.common.exception;

import java.io.Serializable;

/**
 * @author youta
 */
public class RedissonLockException extends RuntimeException implements Serializable {

    public RedissonLockException() {
        super();
    }

    public RedissonLockException(String message) {
        super(message);
    }

    public RedissonLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedissonLockException(Throwable cause) {
        super(cause);
    }

    protected RedissonLockException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
