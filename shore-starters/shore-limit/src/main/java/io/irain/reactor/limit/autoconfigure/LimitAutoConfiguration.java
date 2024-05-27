package io.irain.reactor.limit.autoconfigure;

import io.irain.reactor.limit.aspect.LimitAspect;
import io.irain.reactor.limit.provider.LimitProvider;
import io.irain.reactor.limit.provider.RedissonLimitProvider;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author youta
 **/
@Configuration
public class LimitAutoConfiguration {


    /**
     * limit aspect.
     *
     * @param provider the provider
     * @param messageSource the message source
     * @return the limit aspect
     */
    @Bean
    public LimitAspect limitAspect(ObjectProvider<LimitProvider> provider, MessageSource messageSource) {
        return new LimitAspect(provider, messageSource);
    }

    /**
     * Redisson limit provider.
     *
     * @param redissonReactiveClient the redisson reactive client
     * @return the limit provider
     */
    @Bean
    public LimitProvider redissonLimitProvider(RedissonReactiveClient redissonReactiveClient) {
        return new RedissonLimitProvider(redissonReactiveClient);
    }

}
