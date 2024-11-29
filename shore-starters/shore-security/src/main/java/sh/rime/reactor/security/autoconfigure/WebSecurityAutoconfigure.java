package sh.rime.reactor.security.autoconfigure;

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
import org.springframework.web.method.HandlerMethod;
import sh.rime.reactor.core.properties.AuthProperties;
import sh.rime.reactor.security.anonymous.AnonymousUrlLoader;
import sh.rime.reactor.security.authentication.*;
import sh.rime.reactor.security.handler.*;

import java.lang.annotation.Annotation;
import java.util.LinkedList;


/**
 * WebSecurityConfig
 *
 * @author youta
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
@ComponentScan(basePackages = {
        "sh.rime.reactor.security.authentication",
        "sh.rime.reactor.security.handler",
        "sh.rime.reactor.security.grant"
})
@SuppressWarnings("all")
public class WebSecurityAutoconfigure {

    private final AuthenticationManager authenticationManager;
    private final TokenServerSecurityContextRepository tokenServerSecurityContextRepository;
    private final TokenServerAuthenticationSuccessHandler tokenServerAuthenticationSuccessHandler;
    private final TokenServerAuthenticationFailureHandler tokenServerAuthenticationFailureHandler;
    private final TokenServerLogoutSuccessHandler tokenServerLogoutSuccessHandler;
    private final ReactiveAuthEntryPoint reactiveAuthEntryPoint;
    private final AuthAccessDeniedHandler authAccessDeniedHandler;
    private final CustomAuthorizationManager customAuthorizationManager;
    private final ReactiveServerAuthenticationConverter reactiveServerAuthenticationConverter;
    private final AuthProperties authProperties;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param authenticationManager                   the authentication manager
     * @param tokenServerSecurityContextRepository    the token server security context repository
     * @param tokenServerAuthenticationSuccessHandler the token server authentication success handler
     * @param tokenServerAuthenticationFailureHandler the token server authentication failure handler
     * @param tokenServerLogoutSuccessHandler         the token server logout success handler
     * @param reactiveAuthEntryPoint                  the auth entry point
     * @param authAccessDeniedHandler                 the auth access denied handler
     * @param customAuthorizationManager              the custom authorization manager
     * @param reactiveServerAuthenticationConverter   the post login auth converter
     * @param authProperties                          the auth properties
     */
    public WebSecurityAutoconfigure(AuthenticationManager authenticationManager,
                                    TokenServerSecurityContextRepository tokenServerSecurityContextRepository, TokenServerAuthenticationSuccessHandler tokenServerAuthenticationSuccessHandler,
                                    TokenServerAuthenticationFailureHandler tokenServerAuthenticationFailureHandler,
                                    TokenServerLogoutSuccessHandler tokenServerLogoutSuccessHandler,
                                    ReactiveAuthEntryPoint reactiveAuthEntryPoint, AuthAccessDeniedHandler authAccessDeniedHandler,
                                    CustomAuthorizationManager customAuthorizationManager,
                                    ReactiveServerAuthenticationConverter reactiveServerAuthenticationConverter, AuthProperties authProperties) {
        this.authenticationManager = authenticationManager;
        this.tokenServerSecurityContextRepository = tokenServerSecurityContextRepository;
        this.tokenServerAuthenticationSuccessHandler = tokenServerAuthenticationSuccessHandler;
        this.tokenServerAuthenticationFailureHandler = tokenServerAuthenticationFailureHandler;
        this.tokenServerLogoutSuccessHandler = tokenServerLogoutSuccessHandler;
        this.reactiveAuthEntryPoint = reactiveAuthEntryPoint;
        this.authAccessDeniedHandler = authAccessDeniedHandler;
        this.customAuthorizationManager = customAuthorizationManager;
        this.reactiveServerAuthenticationConverter = reactiveServerAuthenticationConverter;
        this.authProperties = authProperties;
    }

    /**
     * 配置认证管理器
     *
     * @param http serverHttpSecurity
     * @return 认证管理器
     */
    @Bean
    public SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        AnonymousUrlLoader urlLoader = new AnonymousUrlLoader(authProperties);
        urlLoader.loadAnonymousUrls();
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
                .formLogin(formLoginSpec -> formLoginSpec.disable())
                .securityContextRepository(tokenServerSecurityContextRepository)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .accessDeniedHandler(authAccessDeniedHandler)
                        .authenticationEntryPoint(reactiveAuthEntryPoint)
                )
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .logout(logoutSpec -> logoutSpec.logoutUrl(authProperties.getLogoutPattern())
                        .logoutSuccessHandler(tokenServerLogoutSuccessHandler));
        return http.build();
    }



    /**
     * 配置认证过滤器
     *
     * @return 认证过滤器
     */
    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        filter.setSecurityContextRepository(tokenServerSecurityContextRepository);
        filter.setServerAuthenticationConverter(reactiveServerAuthenticationConverter);
        filter.setAuthenticationSuccessHandler(tokenServerAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(tokenServerAuthenticationFailureHandler);
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,
                authProperties.getLoginPattern()));

        return filter;
    }

    /**
     * 用户信息验证管理器，可按需求添加多个按顺序执行
     *
     * @return 用户信息验证管理器
     */
    @Bean("customReactiveAuthenticationManager")
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
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
