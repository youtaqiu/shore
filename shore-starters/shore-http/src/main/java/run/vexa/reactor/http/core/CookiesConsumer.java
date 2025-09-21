package run.vexa.reactor.http.core;

import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * Cookies consumer.
 *
 * @author rained
 **/
public interface CookiesConsumer {

    /**
     * hold a consumer of cookies map
     *
     * @return {@link Consumer< MultiValueMap <String, String>>}
     */
    Consumer<MultiValueMap<String, String>> consume();
}
