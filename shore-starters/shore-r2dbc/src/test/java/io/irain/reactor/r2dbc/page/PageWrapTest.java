package io.irain.reactor.r2dbc.page;

import io.irain.reactor.commons.domain.PageResult;
import io.irain.reactor.commons.domain.Search;
import io.irain.reactor.security.domain.CurrentUser;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.H2Dialect;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * @author youta
 **/
class PageWrapTest {

    ConnectionFactory connectionFactory = new H2ConnectionFactory(
            H2ConnectionConfiguration.builder()
                    .inMemory("TOKEN_INFO")
                    .username("sa")
                    .build()
    );

    @SuppressWarnings("all")
    String sql = "CREATE TABLE IF NOT EXISTS TOKEN_INFO ( id BIGINT AUTO_INCREMENT PRIMARY KEY,user_id BIGINT NOT NULL)";
    @SuppressWarnings("all")
    String insertSql = "INSERT INTO TOKEN_INFO (user_id) VALUES (1)";

    @Test
    void test() {
        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(databaseClient, H2Dialect.INSTANCE);
        Mono<PageResult<CurrentUser>> result = databaseClient.sql(sql)
                .fetch()
                .rowsUpdated()
                .filter(i -> i > 0)
                .flatMap(x ->
                        PageWrap.build(CurrentUser.class)
                                .template(template)
                                .search(new Search())
                                .page());
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
