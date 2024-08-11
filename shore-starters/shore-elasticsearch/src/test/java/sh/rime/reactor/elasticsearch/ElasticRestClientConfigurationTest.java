package sh.rime.reactor.elasticsearch;

import sh.rime.reactor.elasticsearch.properties.ElasticProperties;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author youta
 **/
class ElasticRestClientConfigurationTest {

    @Test
    void testRestClientBuilder() {
        ElasticProperties properties = new ElasticProperties();
        properties.setUris(Collections.singletonList("http://localhost:9200"));
        properties.setUsername(Optional.ofNullable(System.getenv("ELASTIC_USERNAME")).orElse("elastic"));
        properties.setPassword(Optional.ofNullable(System.getenv("ELASTIC_PASSWORD")).orElse("changeme"));

        ElasticRestClientConfiguration configuration = new ElasticRestClientConfiguration(properties);
        RestClientBuilder builder = configuration.restClientBuilder();
        assertNotNull(builder);

        RestClient client = builder.build();
        assertNotNull(client);
    }

}
