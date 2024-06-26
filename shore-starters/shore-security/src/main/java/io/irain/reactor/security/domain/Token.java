package io.irain.reactor.security.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author youta
 **/
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Token implements Serializable {

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
     * 用户id
     */
    private String userId;

    /**
     * 权限
     */
    private List<String> authority;

    /**
     * 角色
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<RoleInfo> roles;

    /**
     * 客户端类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer clientType;

}
