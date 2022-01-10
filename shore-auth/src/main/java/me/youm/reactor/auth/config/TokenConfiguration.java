package me.youm.reactor.auth.config;

import cn.dev33.satoken.config.SaTokenConfig;
import me.youm.reactor.common.constants.AuthConstant;
import me.youm.reactor.security.props.TokenProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author youta
 **/
@Configuration
@ComponentScan(value = "me.youm.reactor.auth")
@EnableConfigurationProperties(TokenProperties.class)
@ConditionalOnProperty(value = TokenProperties.PREFIX + ".enable", havingValue = "true", matchIfMissing = true)
public class TokenConfiguration {

    @Primary
    @Bean(name="SaTokenConfigure")
    public SaTokenConfig getSaTokenConfig() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName(AuthConstant.HEADER_TOKEN);
        config.setTimeout( 24 * 60 * 60);
        config.setActivityTimeout(3600);
        config.setIsConcurrent(true);
        config.setIsShare(false);
        config.setTokenStyle("uuid");
        config.setTokenPrefix("bearer");
        config.setIsPrint(false);
        return config;
    }

}
