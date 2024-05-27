package io.irain.reactor.core.properties;

import io.irain.reactor.commons.annotation.RequestMethodEnum;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author youta
 **/
@Getter
@ConfigurationProperties("shore.security")
public class AuthProperties {

    private Boolean enable = true;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * 分隔符.
     */
    private static final String SEPARATOR = "<=>";


    /**
     * 排除路径.
     */
    private final Set<String> excludePatterns = new HashSet<>();
    private Set<String> getExcludePatterns = new HashSet<>();
    private Set<String> postExcludePatterns = new HashSet<>();
    private Set<String> putExcludePatterns = new HashSet<>();
    private Set<String> patchExcludePatterns = new HashSet<>();
    private Set<String> deleteExcludePatterns = new HashSet<>();

    private String loginPattern = "/login";
    private String logoutPattern = "/logout";
    private final Long renewTimeSeconds = 3600L;

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
            "/docs",
            "/favicon.ico"
    };

    /**
     * 设置是否开启
     *
     * @param enable 是否开启
     */
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
     * 设置get排除路径.
     *
     * @param getExcludePatterns get排除路径
     */
    public void setGetExcludePatterns(Set<String> getExcludePatterns) {
        this.getExcludePatterns = getExcludePatterns;
    }

    /**
     * 设置post排除路径.
     *
     * @param postExcludePatterns post排除路径
     */
    public void setPostExcludePatterns(Set<String> postExcludePatterns) {
        this.postExcludePatterns = postExcludePatterns;
    }

    /**
     * 设置put排除路径.
     *
     * @param putExcludePatterns put排除路径
     */
    public void setPutExcludePatterns(Set<String> putExcludePatterns) {
        this.putExcludePatterns = putExcludePatterns;
    }

    /**
     * 设置patch排除路径.
     *
     * @param patchExcludePatterns patch排除路径
     */
    public void setPatchExcludePatterns(Set<String> patchExcludePatterns) {
        this.patchExcludePatterns = patchExcludePatterns;
    }

    /**
     * 设置删除排除路径.
     *
     * @param deleteExcludePatterns 删除排除路径
     */
    public void setDeleteExcludePatterns(Set<String> deleteExcludePatterns) {
        this.deleteExcludePatterns = deleteExcludePatterns;
    }

    /**
     * 设置登录路径.
     *
     * @param loginPattern 登录路径
     */
    public void setLoginPattern(String loginPattern) {
        this.loginPattern = loginPattern;
    }

    /**
     * 设置登出路径.
     *
     * @param logoutPattern 登出路径
     */
    public void setLogoutPattern(String logoutPattern) {
        this.logoutPattern = logoutPattern;
    }


    /**
     * 判断路径是否在排除列表中.
     *
     * @param path 路径
     * @return 是否在排除列表中
     */
    @SuppressWarnings("unused")
    private boolean exclude(String path) {
        if (CollectionUtils.isEmpty(excludePatterns)) {
            return false;
        }

        return excludePatterns.stream().anyMatch(pattern -> MATCHER.match(pattern, path));
    }

    /**
     * 是否排除路径.
     *
     * @param method 请求方法
     * @param path   路径
     * @return 是否排除路径
     */
    public boolean exclude(RequestMethodEnum method, String path) {
        return switch (method) {
            case GET -> getGetExcludePatterns().stream().anyMatch(pattern -> MATCHER.match(pattern, path));
            case POST -> getPostExcludePatterns().stream().anyMatch(pattern -> MATCHER.match(pattern, path));
            case PUT -> getPutExcludePatterns().stream().anyMatch(pattern -> MATCHER.match(pattern, path));
            case DELETE -> getDeleteExcludePatterns().stream().anyMatch(pattern -> MATCHER.match(pattern, path));
            case PATCH -> getPatchExcludePatterns().stream().anyMatch(pattern -> MATCHER.match(pattern, path));
            case ALL -> getExcludePatterns().stream().anyMatch(pattern -> MATCHER.match(pattern, path));
        };
    }

    /**
     * 首次加载合并ENDPOINTS
     */
    @PostConstruct
    public void initIgnoreUrl() {
        Collections.addAll(getExcludePatterns, ENDPOINTS);
    }


}
