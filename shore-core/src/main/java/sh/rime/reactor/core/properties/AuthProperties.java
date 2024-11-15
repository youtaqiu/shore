package sh.rime.reactor.core.properties;

import sh.rime.reactor.commons.annotation.RequestMethodEnum;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * auth properties.
 *
 * @author youta
 **/
@Getter
@ConfigurationProperties("shore.security")
public class AuthProperties {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public AuthProperties() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 设置是否开启
     * enable 是否开启
     */
    @Setter
    private Boolean enable = true;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();


    /**
     * 排除路径.
     */
    @Getter
    private final Set<String> excludePatterns = new HashSet<>();

    /**
     * 设置get排除路径.
     * getExcludePatterns get排除路径
     */
    @Setter
    private Set<String> getExcludePatterns = new HashSet<>();
    private Set<String> postExcludePatterns = new HashSet<>();
    private Set<String> putExcludePatterns = new HashSet<>();
    private Set<String> patchExcludePatterns = new HashSet<>();
    private Set<String> deleteExcludePatterns = new HashSet<>();

    /**
     * 设置登录路径.
     * loginPattern 登录路径
     */
    @Setter
    private String loginPattern = "/login";
    /**
     * 设置登出路径.
     */
    @Setter
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
     * 设置post排除路径.
     *
     * @param postExcludePatterns post排除路径
     */
    public void setPostExcludePatterns(Set<String> postExcludePatterns) {
        this.postExcludePatterns = Collections.unmodifiableSet(postExcludePatterns);
    }

    /**
     * 设置put排除路径.
     *
     * @param putExcludePatterns put排除路径
     */
    public void setPutExcludePatterns(Set<String> putExcludePatterns) {
        this.putExcludePatterns = Collections.unmodifiableSet(putExcludePatterns);
    }

    /**
     * 设置patch排除路径.
     *
     * @param patchExcludePatterns patch排除路径
     */
    public void setPatchExcludePatterns(Set<String> patchExcludePatterns) {
        this.patchExcludePatterns = Collections.unmodifiableSet(patchExcludePatterns);
    }

    /**
     * 设置delete排除路径.
     *
     * @param deleteExcludePatterns delete排除路径
     */
    public void setDeleteExcludePatterns(Set<String> deleteExcludePatterns) {
        this.deleteExcludePatterns = Collections.unmodifiableSet(deleteExcludePatterns);
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
        Collections.addAll(excludePatterns, ENDPOINTS);
    }


}
