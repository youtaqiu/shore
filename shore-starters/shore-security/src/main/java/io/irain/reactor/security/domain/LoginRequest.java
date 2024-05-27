package io.irain.reactor.security.domain;

import io.irain.reactor.commons.enums.GrantType;
import lombok.*;

/**
 * @author youta
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {


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
