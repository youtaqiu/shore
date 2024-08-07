package sh.rime.reactor.security.domain;

import sh.rime.reactor.commons.enums.GrantType;
import lombok.*;

/**
 * LoginRequest is a class that represents login request information.
 *
 * @author youta
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginRequest {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LoginRequest() {
    }


    /**
     * 登录类型 {@link GrantType#getValue()}
     */
    private String type;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 验证码(可以是授权码)
     */
    private String code;

}
