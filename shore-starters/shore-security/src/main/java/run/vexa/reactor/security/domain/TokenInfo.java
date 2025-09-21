package run.vexa.reactor.security.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * TokenInfo is a class that represents token information.
 *
 * @author youta
 **/
@Getter
@Setter
@ToString
@AllArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class TokenInfo {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public TokenInfo() {
    }

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
