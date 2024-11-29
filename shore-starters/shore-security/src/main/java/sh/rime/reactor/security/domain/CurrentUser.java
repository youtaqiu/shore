package sh.rime.reactor.security.domain;

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
    @JsonIgnore
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
     * -- SETTER --
     *  是否启用
     *
     */
    @Setter
    private boolean enabled;

    /**
     * 是否过期
     * -- SETTER --
     *  设置是否过期
     *
     */
    @Setter
    @Builder.Default
    private boolean accountNonExpired = true;

    /**
     * 是否锁定
     * -- SETTER --
     *  设置是否锁定
     *
     */
    @Setter
    @Builder.Default
    private boolean accountNonLocked = true;

    /**
     * 是否过期
     * -- SETTER --
     *  设置是否过期
     *
     */
    @Setter
    @Builder.Default
    private boolean credentialsNonExpired = true;

    /**
     * 权限
     * -- SETTER --
     *  设置权限
     *
     */
    @Setter
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
     * 是否锁定
     *
     * @return 是否锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
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

}

