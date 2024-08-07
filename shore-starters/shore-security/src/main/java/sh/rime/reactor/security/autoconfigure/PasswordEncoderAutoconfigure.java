package sh.rime.reactor.security.autoconfigure;

import sh.rime.reactor.security.filter.CacheBodyGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoderAutoconfigure is a class that configures password encoders.
 * @author youta
 **/
@Configuration
public class PasswordEncoderAutoconfigure {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public PasswordEncoderAutoconfigure() {
    }

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
