package me.youm.reactor.security.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author youta
 */
@Getter
@Setter
@ConfigurationProperties(TokenProperties.PREFIX)
public class TokenProperties {

    private static final String[] ENDPOINTS = {
            "/actuator/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/api-docs/**",
            "/swagger/api-docs",
            "/swagger-ui/**",
            "/doc.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/druid/**",
            "/error/**",
            "/assets/**",
            "/oauth/token",
            "/oauth/logout",
            "/swagger-resources",
            "/favicon.ico"
    };

    /**
     * 前缀
     */
    public static final String PREFIX = "shore.auth";

    /**
     * 是否开启token验证
     */
    private Boolean enable = Boolean.TRUE;

    private List<String> ignoreUrl = new ArrayList<>();

    /**
     * 首次加载合并ENDPOINTS
     */
    @PostConstruct
    public void initIgnoreUrl() {
        Collections.addAll(ignoreUrl, ENDPOINTS);
    }

}
