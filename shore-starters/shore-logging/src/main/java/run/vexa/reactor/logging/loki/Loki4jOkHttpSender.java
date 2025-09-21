package run.vexa.reactor.logging.loki;

import com.github.loki4j.client.http.HttpConfig;
import com.github.loki4j.client.http.Loki4jHttpClient;
import com.github.loki4j.logback.HttpSender;

import java.util.function.Function;

/**
 * Loki sender that is backed by OkHttp
 *
 * @author youta
 */
public class Loki4jOkHttpSender implements HttpSender {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public Loki4jOkHttpSender() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    @Override
    public Function<HttpConfig, Loki4jHttpClient> getHttpClientFactory() {
        return Loki4jOkHttpClient::new;
    }

    @Override
    public HttpConfig.Builder getConfig() {
        return HttpConfig.builder();
    }

}
