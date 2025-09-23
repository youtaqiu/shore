package run.vexa.reactor.limit.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.util.Assert;
import run.vexa.reactor.limit.aspect.LimitAspect;
import run.vexa.reactor.limit.provider.LimitProvider;
import run.vexa.reactor.limit.provider.RedissonLimitProvider;
import org.redisson.api.RedissonReactiveClient;

import static org.mockito.Mockito.mock;

class LimitAutoConfigurationTest {

    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LimitAutoConfiguration.class))
            .withBean(RedissonReactiveClient.class, () -> mock(RedissonReactiveClient.class))
            .withBean(MessageSource.class, StaticMessageSource::new);

    @Test
    void beansAreCreated() {
        contextRunner.run(context -> {
            Assert.notNull(context.getBean(LimitAspect.class), "LimitAspect should be created");
            Assert.notNull(context.getBean(LimitProvider.class), "LimitProvider should be created");
            Assert.isTrue(context.getBean(LimitProvider.class) instanceof RedissonLimitProvider, "LimitProvider should be RedissonLimitProvider");
        });
    }
}


