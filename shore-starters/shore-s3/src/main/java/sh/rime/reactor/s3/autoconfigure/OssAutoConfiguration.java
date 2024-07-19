package sh.rime.reactor.s3.autoconfigure;

import sh.rime.reactor.s3.core.OssTemplate;
import sh.rime.reactor.s3.props.OssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author youta
 */
@Configuration
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoConfiguration {


    /**
     * OSS模板
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
