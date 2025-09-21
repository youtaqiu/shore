package run.vexa.reactor.elasticsearch.properties;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ElasticProperties unit test.
 *
 * @author rained
 **/
class ElasticPropertiesTest {

    private ElasticProperties elasticProperties;

    @BeforeEach
    void setUp() {
        elasticProperties = new ElasticProperties();
    }

    @Test
    void testDefaultValues() {
        // 测试初始值
        assertNull(elasticProperties.getUsername(), "Default username should be null");
        assertNull(elasticProperties.getPassword(), "Default password should be null");
        assertNull(elasticProperties.getUris(), "Default uris should be null");
    }

    @Test
    void testSetAndGetUsername() {
        String username = "elastic_user";
        elasticProperties.setUsername(username);
        assertEquals(username, elasticProperties.getUsername(), "Username should match the value set");
    }

    @Test
    void testSetAndGetPassword() {
        String password = Optional.ofNullable(System.getenv("ELASTIC_PASSWORD")).orElse("changeme");
        elasticProperties.setPassword(password);
        assertEquals(password, elasticProperties.getPassword(), "Password should match the value set");
    }

    @Test
    void testSetAndGetUris() {
        List<String> uris = Arrays.asList("http://localhost:9200", "http://localhost:9201");
        elasticProperties.setUris(uris);
        assertEquals(uris, elasticProperties.getUris(), "Uris should match the value set");
    }

    @Test
    void testElasticPropertiesConstructor() {
        ElasticProperties esProperties = new ElasticProperties();
        assertNotNull(esProperties, "ElasticProperties instance should be created");
    }
}

