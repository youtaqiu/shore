package sh.rime.reactor.security.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import sh.rime.reactor.commons.annotation.Anonymous;
import sh.rime.reactor.commons.annotation.RequestMethodEnum;
import sh.rime.reactor.core.properties.AuthProperties;
import sh.rime.reactor.security.authentication.AuthenticationManager;
import sh.rime.reactor.security.authentication.CustomAuthorizationManager;
import sh.rime.reactor.security.authentication.PostLoginAuthConverter;
import sh.rime.reactor.security.authentication.TokenServerSecurityContextRepository;
import sh.rime.reactor.security.domain.CurrentUser;
import sh.rime.reactor.security.handler.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * WebSecurityConfig
 *
 * @author youta
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@RegisterReflectionForBinding(CurrentUser.class)
@ComponentScan(basePackages = {"sh.rime.reactor.security.authentication",
        "sh.rime.reactor.security.grant",
        "sh.rime.reactor.security.handler",
        "sh.rime.reactor.security.repository"
})
@SuppressWarnings("all")
public class WebSecurityAutoconfigure {

    private final AuthenticationManager authenticationManager;
    private final TokenServerSecurityContextRepository tokenServerSecurityContextRepository;
    private final TokenServerAuthenticationSuccessHandler tokenServerAuthenticationSuccessHandler;
    private final TokenServerAuthenticationFailureHandler tokenServerAuthenticationFailureHandler;
    private final TokenServerLogoutSuccessHandler tokenServerLogoutSuccessHandler;
    private final AuthEntryPoint authEntryPoint;
    private final AuthAccessDeniedHandler authAccessDeniedHandler;
    private final CustomAuthorizationManager customAuthorizationManager;
    private final PostLoginAuthConverter postLoginAuthConverter;
    private final AuthProperties authProperties;

    /**
     * requestMappingHandlerMapping
     */
    protected final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationManager                   the authentication manager
     * @param tokenServerSecurityContextRepository    the token server security context repository
     * @param tokenServerAuthenticationSuccessHandler the token server authentication success handler
     * @param tokenServerAuthenticationFailureHandler the token server authentication failure handler
     * @param tokenServerLogoutSuccessHandler         the token server logout success handler
     * @param authEntryPoint                          the auth entry point
     * @param authAccessDeniedHandler                 the auth access denied handler
     * @param customAuthorizationManager              the custom authorization manager
     * @param postLoginAuthConverter                  the post login auth converter
     * @param authProperties                          the auth properties
     * @param requestMappingHandlerMapping            the request mapping handler mapping
     */
    public WebSecurityAutoconfigure(AuthenticationManager authenticationManager,
                                    TokenServerSecurityContextRepository tokenServerSecurityContextRepository,
                                    TokenServerAuthenticationSuccessHandler tokenServerAuthenticationSuccessHandler,
                                    TokenServerAuthenticationFailureHandler tokenServerAuthenticationFailureHandler,
                                    TokenServerLogoutSuccessHandler tokenServerLogoutSuccessHandler,
                                    AuthEntryPoint authEntryPoint, AuthAccessDeniedHandler authAccessDeniedHandler,
                                    CustomAuthorizationManager customAuthorizationManager,
                                    PostLoginAuthConverter postLoginAuthConverter, AuthProperties authProperties,
                                    RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.authenticationManager = authenticationManager;
        this.tokenServerSecurityContextRepository = tokenServerSecurityContextRepository;
        this.tokenServerAuthenticationSuccessHandler = tokenServerAuthenticationSuccessHandler;
        this.tokenServerAuthenticationFailureHandler = tokenServerAuthenticationFailureHandler;
        this.tokenServerLogoutSuccessHandler = tokenServerLogoutSuccessHandler;
        this.authEntryPoint = authEntryPoint;
        this.authAccessDeniedHandler = authAccessDeniedHandler;
        this.customAuthorizationManager = customAuthorizationManager;
        this.postLoginAuthConverter = postLoginAuthConverter;
        this.authProperties = authProperties;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * 配置认证管理器
     *
     * @param http serverHttpSecurity
     * @return 认证管理器
     */
    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        loadAnonymousUrls();
        http.authorizeExchange(authorizeRequests -> {
                            if (!authProperties.getEnable()) {
                                authorizeRequests.anyExchange().permitAll();
                                return;
                            }
                            authorizeRequests
                                    .pathMatchers(HttpMethod.GET, authProperties.getExcludePatterns()
                                            .toArray(new String[0])).permitAll()
                                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                    .anyExchange().access(customAuthorizationManager);
                        }
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .accessDeniedHandler(authAccessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                )
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .logout(logoutSpec -> logoutSpec.logoutUrl(authProperties.getLogoutPattern())
                        .logoutSuccessHandler(tokenServerLogoutSuccessHandler));
        return http.build();
    }

    /**
     * 加载匿名访问的url
     */
    private void loadAnonymousUrls() {
        if (SpringUtil.getApplicationContext() == null) {
            return;
        }
        RequestMappingHandlerMapping handlerMapping = SpringUtil.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            // 处理注解信息
            boolean needLogin = getAnnotation(handlerMethod, Anonymous.class) == null;
            if (!needLogin) {
                Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
                RequestMethodEnum request = RequestMethodEnum.find(methods.isEmpty()
                        ? RequestMethodEnum.ALL.getType()
                        : methods.stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Method not found"))
                        .name());
                setPath(request, requestMappingInfo);
            }
        }
    }

    /**
     * 设置路径
     *
     * @param method             请求方法
     * @param requestMappingInfo 请求映射信息
     */
    private void setPath(RequestMethodEnum method, RequestMappingInfo requestMappingInfo) {
        Set<PathPattern> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        if (patterns.isEmpty()) {
            return;
        }
        switch (Objects.requireNonNull(method)) {
            case GET -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getGetExcludePatterns().addAll(collect);
            }
            case POST -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getPostExcludePatterns().addAll(collect);
            }
            case PUT -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getPutExcludePatterns().addAll(collect);
            }
            case PATCH -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getPatchExcludePatterns().addAll(collect);
            }
            case DELETE -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getDeleteExcludePatterns().addAll(collect);
            }
            default -> {
                Set<String> collect = patterns
                        .stream()
                        .map(PathPattern::getPatternString)
                        .collect(Collectors.toSet());
                authProperties.getExcludePatterns().addAll(collect);
            }
        }

    }

    /**
     * 配置认证过滤器
     *
     * @return 认证过滤器
     */
    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        filter.setSecurityContextRepository(tokenServerSecurityContextRepository);
        filter.setServerAuthenticationConverter(postLoginAuthConverter);
        filter.setAuthenticationSuccessHandler(tokenServerAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(tokenServerAuthenticationFailureHandler);
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, authProperties.getLoginPattern()));

        return filter;
    }

    /**
     * 用户信息验证管理器，可按需求添加多个按顺序执行
     */
    @Bean("customReactiveAuthenticationManager")
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }


    /**
     * 获取注解.
     *
     * @param handlerMethod   处理方法
     * @param annotationClass 注解类
     * @param <A>             注解类
     * @return 注解对象
     */
    @SuppressWarnings("SameParameterValue")
    protected static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationClass) {
        A annotation = handlerMethod.getMethodAnnotation(annotationClass);
        if (annotation == null) {
            Class<?> clazz = handlerMethod.getBeanType();
            annotation = clazz.getAnnotation(annotationClass);

            if (annotation == null) {
                annotation = clazz.getPackage().getAnnotation(annotationClass);
            }
        }
        return annotation;
    }

}
