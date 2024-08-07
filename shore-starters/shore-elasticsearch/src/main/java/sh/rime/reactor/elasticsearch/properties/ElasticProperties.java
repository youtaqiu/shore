package sh.rime.reactor.elasticsearch.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * ElasticSearch配置属性
 *
 * @author youta
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticProperties {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ElasticProperties() {
    }

    private List<String> uris;
    private String username;
    private String password;

}
