package me.youm.reactor.r2dbc;

import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author youta
 **/
@Configuration
@EnableConfigurationProperties(R2dbcProperties.class)
@EnableTransactionManagement
public class R2dbcConfiguration {

}
