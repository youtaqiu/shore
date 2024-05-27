package io.irain.reactor.security.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author youta
 **/
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class TokenInfo {

    /**
     * 用户名
     */
    private String username;

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 有效期
     */
    private long expiresIn;

    /**
     * 权限
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> authority;

    private String userId;
    private String clientId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer clientType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<RoleInfo> roles;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String avatar;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickName;

}
