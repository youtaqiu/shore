package sh.rime.reactor.r2dbc.query;

import sh.rime.reactor.commons.domain.Search;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/**
 * @author youta
 **/
class QueryWrapTest {

    @SuppressWarnings("unchecked")
    @Test
    void testList() {
        R2dbcEntityTemplate template = Mockito.mock(R2dbcEntityTemplate.class);
        DatabaseClient databaseClient = Mockito.mock(DatabaseClient.class);
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = Mockito.mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec<?> rowsFetchSpec = Mockito.mock(RowsFetchSpec.class);
        BiFunction<Row, RowMetadata, UserDemo> rowFunction = Mockito.mock(BiFunction.class);

        when(template.getDatabaseClient()).thenReturn(databaseClient);
        when(databaseClient.sql(anyString())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(anyString(), any())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.map(any(BiFunction.class))).thenReturn(rowsFetchSpec);
        when(genericExecuteSpec.map(rowFunction).all()).thenReturn(Flux.just(new UserDemo(), new UserDemo(), new UserDemo()));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        Flux<UserDemo> flux = queryWrap.list();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testPage() {
        R2dbcEntityTemplate template = Mockito.mock(R2dbcEntityTemplate.class);
        DatabaseClient databaseClient = Mockito.mock(DatabaseClient.class);
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = Mockito.mock(DatabaseClient.GenericExecuteSpec.class);
        BiFunction<Row, RowMetadata, UserDemo> rowFunction = Mockito.mock(BiFunction.class);
        RowsFetchSpec<?> rowsFetchSpec = Mockito.mock(RowsFetchSpec.class);
        when(template
                .getDatabaseClient())
                .thenReturn(databaseClient);
        when(databaseClient
                .sql(anyString()))
                .thenReturn(genericExecuteSpec);
        when(genericExecuteSpec
                .bind(anyString(), any()))
                .thenReturn(genericExecuteSpec);
        when(genericExecuteSpec
                .map(any(BiFunction.class)))
                .thenReturn(rowsFetchSpec);
        when(genericExecuteSpec
                .map(rowFunction)
                .all())
                .thenReturn(Flux.just(new UserDemo(), new UserDemo(), new UserDemo()));
        when(genericExecuteSpec
                .map(rowFunction)
                .one())
                .thenReturn(Mono.just(new UserDemo()));
        when(genericExecuteSpec
                .map(rowFunction)
                .first())
                .thenReturn(Mono.just(new UserDemo()));
        when(QueryWrap.<UserDemo>build()
                .template(template)
                .count("select * from user"))
                .thenReturn(Mono.just(3L));
        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);
        var pageResultMono = queryWrap
                .search(new Search())
                .page();
        StepVerifier.create(pageResultMono)
                .expectNextMatches(pageResult ->
                        pageResult.getCurrent() == 1 &&
                                pageResult.getSize() == 10 &&
                                pageResult.getTotal() == 3 &&
                                pageResult.getPages() == 1 &&
                                pageResult.getList().size() == 3)
                .verifyComplete();
    }
}

