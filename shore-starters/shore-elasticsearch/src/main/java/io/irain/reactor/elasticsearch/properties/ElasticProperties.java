package io.irain.reactor.elasticsearch.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author youta
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticProperties {

    private List<String> uris;
    private String username;
    private String password;

}
