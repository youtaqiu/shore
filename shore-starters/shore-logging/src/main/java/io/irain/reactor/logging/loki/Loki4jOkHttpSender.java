package io.irain.reactor.logging.loki;

import com.github.loki4j.client.http.HttpConfig;
import com.github.loki4j.client.http.Loki4jHttpClient;
import com.github.loki4j.logback.AbstractHttpSender;

import java.util.function.Function;

/**
 * Loki sender that is backed by OkHttp
 *
 * @author youta
 */
public class Loki4jOkHttpSender extends AbstractHttpSender {

	@Override
	public Function<HttpConfig, Loki4jHttpClient> getHttpClientFactory() {
		return Loki4jOkHttpClient::new;
	}

	@Override
	public HttpConfig.Builder getConfig() {
		HttpConfig.Builder builder = HttpConfig.builder();
		super.fillHttpConfig(builder);
		return builder;
	}
}
