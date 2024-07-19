package sh.rime.reactor.log.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author youta
 **/
@Setter
@Getter
@ConfigurationProperties(LoggingProperties.PREFIX)
public class LoggingProperties {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "shore.log";
    private Boolean enabled = true;
    private Boolean console = true;

}
