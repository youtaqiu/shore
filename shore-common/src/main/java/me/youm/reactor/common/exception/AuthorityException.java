package me.youm.reactor.common.exception;

import me.youm.reactor.common.enums.EnumInterface;
import org.springframework.http.HttpStatus;

/**
 * @author youta
 */
public class AuthorityException extends BaseException implements EnumInterface {

    private static final long serialVersionUID = -109638013567525177L;

    public AuthorityException(){

    }

    public AuthorityException(int code, String message) {
        super(code, message);
    }

    public AuthorityException(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}
