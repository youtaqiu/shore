package me.youm.reactor.security;

import me.youm.reactor.security.props.TokenProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author xiyu
 **/
@Configuration
public class SecurityConfigure {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public TokenProperties tokenProperties(){
        return new TokenProperties();
    }
}
