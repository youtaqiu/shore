package io.irain.reactor.elasticsearch;

import io.irain.reactor.elasticsearch.properties.ElasticProperties;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author youta
 **/
class ElasticRestClientConfigurationTest {

    @Test
    void testRestClientBuilder() {
        ElasticProperties properties = new ElasticProperties();
        properties.setUris(Collections.singletonList("http://localhost:9200"));
        properties.setUsername("user");
        properties.setPassword("password");

        ElasticRestClientConfiguration configuration = new ElasticRestClientConfiguration(properties);
        RestClientBuilder builder = configuration.restClientBuilder();
        assertNotNull(builder);

        RestClient client = builder.build();
        assertNotNull(client);
    }

}
