package run.vexa.reactor.limit.autoconfigure;

import run.vexa.reactor.limit.aspect.LimitAspect;
import run.vexa.reactor.limit.provider.LimitProvider;
import run.vexa.reactor.limit.provider.RedissonLimitProvider;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 限流自动配置
 *
 * @author youta
 **/
@Configuration
public class LimitAutoConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LimitAutoConfiguration() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }


    /**
     * limit aspect.
     *
     * @param provider      the provider
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
