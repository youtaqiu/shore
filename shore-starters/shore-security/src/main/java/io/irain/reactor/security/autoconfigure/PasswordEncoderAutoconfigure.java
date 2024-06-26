package io.irain.reactor.security.autoconfigure;

import io.irain.reactor.security.filter.CacheBodyGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author youta
 **/
@Configuration
public class PasswordEncoderAutoconfigure {

    /**
     * BCrypt密码编码
     *
     * @return PasswordEncoder
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 缓存body过滤器
     *
     * @return CacheBodyGlobalFilter
     */
    @Bean
    public CacheBodyGlobalFilter cacheBodyGlobalFilter() {
        return new CacheBodyGlobalFilter();
    }

}
