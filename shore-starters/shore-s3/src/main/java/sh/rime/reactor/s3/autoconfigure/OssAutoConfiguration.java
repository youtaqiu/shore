package sh.rime.reactor.s3.autoconfigure;

import sh.rime.reactor.s3.core.OssTemplate;
import sh.rime.reactor.s3.props.OssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对象存储自动配置.
 *
 * @author youta
 */
@Configuration
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public OssAutoConfiguration() {
    }

    /**
     * OSS模板
     *
     * @param properties OSS配置属性
     * @return OSS模板
     */
    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    @ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
    public OssTemplate ossTemplate(OssProperties properties) {
        return new OssTemplate(properties);
    }

}
