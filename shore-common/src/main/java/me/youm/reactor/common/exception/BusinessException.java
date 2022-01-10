package me.youm.reactor.common.exception;

import lombok.Getter;
import me.youm.reactor.common.enums.EnumInterface;

/**
 * 异常状态码、异常消息 构造器CodeEnum
 *
 * @author youta
 */
@Getter
public class BusinessException extends BaseException implements EnumInterface {

    private static final long serialVersionUID = -109638013567529871L;

    public BusinessException(){
    }

    public BusinessException(String message) {
        super(500,message);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(EnumInterface enums, Object... args) {
        super(enums.getCode(), enums.getMsg(), args);
    }

}
