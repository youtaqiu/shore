package io.irain.reactor.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 令牌信息.
 *
 * @author guer
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class CurrentUser extends Token implements UserDetails {

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色
     */
    private List<RoleInfo> roleInfos;

    /**
     * 头像
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String avatar;

    /**
     * 昵称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickName;

    /**
     * 用户ID
     */
    private String id;

    /**
     * 登录类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int type;


    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 是否过期
     */
    @Builder.Default
    private boolean accountNonExpired = true;

    /**
     * 是否锁定
     */
    @Builder.Default
    private boolean accountNonLocked = true;

    /**
     * 是否过期
     */
    @Builder.Default
    private boolean credentialsNonExpired = true;

    /**
     * 权限
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 获取权限
     *
     * @return 权限
     */
    @Override
    @JsonIgnore
    public List<SimpleGrantedAuthority> getAuthorities() {
        if (this.roleInfos == null || this.roleInfos.isEmpty()) {
            this.roleInfos = defaultRoleInfos();
        }
        return this.roleInfos.stream()
                .map(RoleInfo::getRole)
                .map(RoleEnum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 默认构造函数
     */
    public CurrentUser() {
        this.roleInfos = defaultRoleInfos();
    }

    /**
     * 默认用户角色组
     *
     * @return 角色组
     */
    public static List<RoleInfo> defaultRoleInfos() {
        return List.of(RoleInfo.builder()
                .role(RoleEnum.ROLE_USER)
                .roleName("普通用户")
                .build());
    }

    /**
     * 设置权限
     *
     * @param authorities 权限
     */
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 设置是否过期
     *
     * @param accountNonExpired 是否过期
     */
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * 是否锁定
     *
     * @return 是否锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 设置是否锁定
     *
     * @param accountNonLocked 是否锁定
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * 是否过期
     *
     * @return 是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 设置是否过期
     *
     * @param credentialsNonExpired 是否过期
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * 是否启用
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

