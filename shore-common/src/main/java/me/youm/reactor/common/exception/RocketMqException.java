package me.youm.reactor.common.exception;


import me.youm.reactor.common.enums.EnumInterface;

/**
 * @author youta
 */
public class RocketMqException extends BaseException implements EnumInterface {

    private static final long serialVersionUID = -109638013567529871L;

    public RocketMqException(){

    }
    public RocketMqException(EnumInterface enums, Object... args) {
        super(enums.getCode(), enums.getMsg(), args);
    }

    public RocketMqException(int code, String message) {
        super(code, message);
    }

    public RocketMqException(String message) {
        super(8000, message);
    }
}
