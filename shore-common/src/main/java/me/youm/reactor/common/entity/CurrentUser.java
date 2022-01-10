package me.youm.reactor.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author youta
 */
@ToString
@Setter
@Getter
public class CurrentUser implements Serializable {

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
    private String authorities;

}
