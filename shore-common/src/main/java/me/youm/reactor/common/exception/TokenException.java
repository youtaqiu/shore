package me.youm.reactor.common.exception;

import me.youm.reactor.common.enums.EnumInterface;
import org.springframework.http.HttpStatus;

/**
 * @author youta
 */
public class TokenException extends BaseException implements EnumInterface {

    private static final long serialVersionUID = -109638013567525177L;

    public TokenException(){

    }

    public TokenException(int code, String message) {
        super(code, message);
    }

    public TokenException(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
