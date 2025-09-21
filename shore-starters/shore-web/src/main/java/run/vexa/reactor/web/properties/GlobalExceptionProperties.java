package run.vexa.reactor.web.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * global exception properties
 *
 * @author youta
 **/
@Setter
@Getter
@ConfigurationProperties("shore.exception")
public class GlobalExceptionProperties {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public GlobalExceptionProperties() {
    }

    /**
     * 是否输出异常到响应结果.
     */
    private Boolean enable = true;

    /**
     * 异常处理返回状态码.
     */
    private int httpCode = 200;
}
