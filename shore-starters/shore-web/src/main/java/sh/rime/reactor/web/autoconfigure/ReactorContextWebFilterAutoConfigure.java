package sh.rime.reactor.web.autoconfigure;

import sh.rime.reactor.web.filter.ReactorContextWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ReactorContextWebFilterAutoConfigure is a configuration class that sets up the creation of ReactorContextWebFilter instances.
 * It includes a method for creating the ReactorContextWebFilter.
 *
 * @author youta
 */
@Configuration(proxyBeanMethods = false)
public class ReactorContextWebFilterAutoConfigure {

    /**
     * reactor context web filter.
     *
     * @return reactor context web filter
     */
    @Bean
    public ReactorContextWebFilter reactorContextWebFilter() {
        return new ReactorContextWebFilter();
    }

}
