package sh.rime.reactor.http.core;

import org.springframework.http.codec.ClientCodecConfigurer;

import java.util.function.Consumer;

/**
 * Client codec configurer consumer.
 *
 * @author rained
 **/
public interface ClientCodecConfigurerConsumer {

    /**
     * hold a consumer of {@link ClientCodecConfigurer}
     *
     * @return {@link Consumer<ClientCodecConfigurer>}
     */
    Consumer<ClientCodecConfigurer> consumer();
}
