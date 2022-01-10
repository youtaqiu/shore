package me.youm.reactor.common.exception;


import me.youm.reactor.common.enums.EnumInterface;

/**
 * @author youta
 */
public class PayException extends BaseException implements EnumInterface {

    private static final long serialVersionUID = -109638013567529871L;

    public PayException(EnumInterface enums, Object... args) {
        super(enums.getCode(), enums.getMsg(), args);
    }

    public PayException(int code, String message) {
        super(code, message);
    }

    public PayException(String message) {
        super(60000, message);
    }

    public PayException() { }

}
