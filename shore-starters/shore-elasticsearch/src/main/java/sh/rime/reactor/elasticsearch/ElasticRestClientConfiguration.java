package sh.rime.reactor.elasticsearch;

import sh.rime.reactor.elasticsearch.properties.ElasticProperties;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * @author youta
 **/
@Configuration
@EnableConfigurationProperties(ElasticProperties.class)
public class ElasticRestClientConfiguration {

    private final ElasticProperties elasticProperties;

    /**
     * @param elasticProperties elasticProperties
     */
    public ElasticRestClientConfiguration(ElasticProperties elasticProperties) {
        this.elasticProperties = elasticProperties;
    }

    /**
     * restClientBuilder
     *
     * @return RestClientBuilder
     */
    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = elasticProperties
                .getUris()
                .stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticProperties.getUsername(), elasticProperties.getPassword()));
        Header[] defaultHeaders = new Header[]{
                new BasicHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
        };
        return RestClient.builder(hosts)
                .setDefaultHeaders(defaultHeaders)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setMaxConnPerRoute(100)
                                .setKeepAliveStrategy((response, context) -> Duration.ofMinutes(5).toMillis())
                                .setMaxConnTotal(100)
                                .addInterceptorLast(
                                        (HttpResponseInterceptor)
                                                (response, context) -> response.addHeader("X-Elastic-Product", "Elasticsearch"))
                ).setRequestConfigCallback(builder -> {
                    builder.setConnectTimeout(-1);
                    builder.setSocketTimeout(60000);
                    builder.setConnectionRequestTimeout(-1);
                    return builder;
                });
    }

}
