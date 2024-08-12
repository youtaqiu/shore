package sh.rime.reactor.r2dbc.query;

import cn.hutool.system.UserInfo;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;
import sh.rime.reactor.commons.domain.Pair;
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
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.core.util.BeanUtil;

import java.util.Objects;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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
                        pageResult.getCurrent() == 1
                                && pageResult.getSize() == 10
                                && pageResult.getTotal() == 3
                                && pageResult.getPages() == 1
                                && pageResult.getList().size() == 3)
                .verifyComplete();
        var pageResultFuncMono = queryWrap
                .search(new Search())
                .page(x -> BeanUtil.copyToList(x, UserInfo.class));
        StepVerifier.create(pageResultFuncMono)
                .expectNextMatches(pageResult ->
                        pageResult.getCurrent() == 1
                                && pageResult.getSize() == 10
                                && pageResult.getTotal() == 3
                                && pageResult.getPages() == 1
                                && pageResult.getList().size() == 3);
    }

    @Test
    void testBind() {
        // Arrange
        QueryWrap<UserDemo> queryWrap = QueryWrap.build();
        Pair<String, Object> pair1 = Pair.of("key1", "value1");
        Pair<String, Object> pair2 = Pair.of("key2", "value2");

        // Act
        queryWrap = queryWrap.bind(pair1, pair2);

        assertNotNull(queryWrap);
    }

    @Test
    void testSql() {
        // Arrange
        QueryWrap<UserDemo> queryWrap = QueryWrap.build();
        String sql = "SELECT * FROM user";

        // Act
        queryWrap = queryWrap.sql(sql);

        assertNotNull(queryWrap);
    }

    @Test
    void testOne() {
        // Arrange
        Result queryResult = getResult();

        when(queryResult.rowsFetchSpec.one()).thenReturn(Mono.just(new UserDemo()));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(queryResult.template)
                .sql("SELECT * FROM user")
                .row(queryResult.rowFunction);

        // Act
        Mono<UserDemo> result = queryWrap.one();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void testFirst() {
        // Arrange
        Result queryResult = getResult();
        when(queryResult.rowsFetchSpec().first()).thenReturn(Mono.just(new UserDemo()));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(queryResult.template())
                .sql("SELECT * FROM user")
                .row(queryResult.rowFunction());

        // Act
        Mono<UserDemo> result = queryWrap.first();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    private static Result getResult() {
        R2dbcEntityTemplate template = Mockito.mock(R2dbcEntityTemplate.class);
        DatabaseClient databaseClient = Mockito.mock(DatabaseClient.class);
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = Mockito.mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec<UserDemo> rowsFetchSpec = Mockito.mock(RowsFetchSpec.class);
        BiFunction<Row, RowMetadata, UserDemo> rowFunction = Mockito.mock(BiFunction.class);

        when(template.getDatabaseClient()).thenReturn(databaseClient);
        when(databaseClient.sql(anyString())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(anyString(), any())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.map(rowFunction)).thenReturn(rowsFetchSpec);
        return new Result(template, rowsFetchSpec, rowFunction);
    }

    private record Result(R2dbcEntityTemplate template, RowsFetchSpec<UserDemo> rowsFetchSpec,
                          BiFunction<Row, RowMetadata, UserDemo> rowFunction) {
    }

    @Test
    void testPageSqlThrowsExceptionWhenSqlContainsLimit() {
        // Arrange
        QueryWrap<Object> queryWrap = new QueryWrap<>();
        String sqlWithLimit = "SELECT * FROM user WHERE id = 1 LIMIT 10";

        // Act & Assert
        assertThrows(ServerException.class, () -> ReflectionTestUtils.invokeMethod(queryWrap, "pageSql", sqlWithLimit));
    }

    @Test
    void testPageSqlReturnsModifiedSqlWhenNoLimit() {
        // Arrange
        QueryWrap<Object> queryWrap = new QueryWrap<>();
        String sqlWithoutLimit = "SELECT * FROM user WHERE id = 1";

        // Act
        String pageSql = ReflectionTestUtils.invokeMethod(queryWrap, "pageSql", sqlWithoutLimit);

        // Assert
        Assert.isTrue(pageSql.equals(sqlWithoutLimit + " limit :limit offset :offset"), "SQL modification failed");
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testSpecWhenPairsIsNotNull() {
        // Arrange
        R2dbcEntityTemplate template = Mockito.mock(R2dbcEntityTemplate.class);
        DatabaseClient databaseClient = Mockito.mock(DatabaseClient.class);
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = Mockito.mock(DatabaseClient.GenericExecuteSpec.class);

        when(template.getDatabaseClient()).thenReturn(databaseClient);
        when(databaseClient.sql(anyString())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(anyString(), any())).thenReturn(genericExecuteSpec);

        Pair<String, Object> pair1 = Pair.of("key1", "value1");
        Pair<String, Object> pair2 = Pair.of("key2", 123);

        QueryWrap<Object> queryWrap = new QueryWrap<>();
        queryWrap.template(template);

        // Act

        ReflectionTestUtils.invokeMethod(queryWrap, "spec",
                "SELECT * FROM table WHERE column1 = :key1 AND column2 = :key2", new Pair[]{pair1, pair2});

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(genericExecuteSpec, times(2)).bind(keyCaptor.capture(), valueCaptor.capture());

        assertEquals("key1", keyCaptor.getAllValues().get(0));
        assertEquals("value1", valueCaptor.getAllValues().get(0));
        assertEquals("key2", keyCaptor.getAllValues().get(1));
        assertEquals(123, valueCaptor.getAllValues().get(1));
    }


}

