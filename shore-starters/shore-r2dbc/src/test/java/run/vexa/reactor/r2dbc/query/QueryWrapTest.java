package run.vexa.reactor.r2dbc.query;

import cn.hutool.core.lang.Pair;
import cn.hutool.system.UserInfo;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.domain.Search;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.core.util.BeanUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * QueryWrap 测试类
 *
 * @author youta
 **/
class QueryWrapTest {

    private R2dbcEntityTemplate template;
    private DatabaseClient databaseClient;
    private DatabaseClient.GenericExecuteSpec genericExecuteSpec;
    private RowsFetchSpec<UserDemo> rowsFetchSpec;
    private BiFunction<Row, RowMetadata, UserDemo> rowFunction;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        template = mock(R2dbcEntityTemplate.class);
        databaseClient = mock(DatabaseClient.class);
        genericExecuteSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        rowsFetchSpec = mock(RowsFetchSpec.class);
        rowFunction = mock(BiFunction.class);

        when(template.getDatabaseClient()).thenReturn(databaseClient);
        when(databaseClient.sql(anyString())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(anyString(), any())).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.map(any(BiFunction.class))).thenReturn(rowsFetchSpec);
    }

    @Test
    void testListSuccess() {
        // Arrange
        when(genericExecuteSpec.map(rowFunction).all())
                .thenReturn(Flux.just(new UserDemo(), new UserDemo(), new UserDemo()));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.list())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testListEmptyResult() {
        // Arrange
        when(genericExecuteSpec.map(rowFunction).all())
                .thenReturn(Flux.empty());

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.list())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testListWithError() {
        // Arrange
        when(genericExecuteSpec.map(rowFunction).all())
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.list())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testPageWithCustomSearch() {
        // Arrange
        Search search = new Search();
        search.setCurrent(2);
        search.setSize(5);
        search.setProp("name");
        search.setOrder("asc");

        when(genericExecuteSpec.map(rowFunction).all())
                .thenReturn(Flux.just(new UserDemo(), new UserDemo()));
        when(QueryWrap.<UserDemo>build()
                .template(template)
                .count(anyString()))
                .thenReturn(Mono.just(7L));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.search(search).page())
                .expectNextMatches(pageResult ->
                        pageResult.getCurrent() == 2 &&
                        pageResult.getSize() == 5 &&
                        pageResult.getTotal() == 7 &&
                        pageResult.getPages() == 2 &&
                        pageResult.getList().size() == 2)
                .verifyComplete();
    }

    @Test
    void testPageWithTransformation() {
        // Arrange
        when(genericExecuteSpec.map(rowFunction).all())
                .thenReturn(Flux.just(new UserDemo(), new UserDemo()));
        when(QueryWrap.<UserDemo>build()
                .template(template)
                .count(anyString()))
                .thenReturn(Mono.just(2L));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap
                .search(new Search())
                .page(list -> BeanUtil.copyToList(list, UserInfo.class)))
                .expectNextMatches(pageResult ->
                        pageResult.getList() != null &&
                        pageResult.getList().size() == 2)
                .verifyComplete();
    }

    @Test
    void testOneWithNoResult() {
        // Arrange
        when(rowsFetchSpec.one()).thenReturn(Mono.empty());

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.one())
                .verifyComplete();
    }

    @Test
    void testFirstWithError() {
        // Arrange
        when(rowsFetchSpec.first()).thenReturn(Mono.error(new RuntimeException("Database error")));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.first())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testBindWithNullValue() {
        // Arrange
        QueryWrap<UserDemo> queryWrap = QueryWrap.build();
        Pair<String, Object> pair1 = Pair.of("key1", null);
        Pair<String, Object> pair2 = Pair.of("key2", "value2");

        // Act
        queryWrap.bind(pair1, pair2);

        // Assert
        assertNotNull(queryWrap);
    }

    @Test
    void testSqlWithComplexQuery() {
        // Arrange
        QueryWrap<UserDemo> queryWrap = QueryWrap.build();
        String complexSql = """
                SELECT u.*, d.name as dept_name
                FROM user u
                LEFT JOIN department d ON u.dept_id = d.id
                WHERE u.status = :status
                """;

        // Act
        queryWrap.sql(complexSql);

        // Assert
        assertNotNull(queryWrap);
    }

    @Test
    void testPageSqlWithVariousFormats() {
        // Test cases for different SQL formats
        testPageSqlFormat("SELECT * FROM user", true);
        testPageSqlFormat("SELECT * FROM user -- with comment", true);
        testPageSqlFormat("SELECT * FROM user LIMIT 10", false);
        testPageSqlFormat("SELECT * FROM user limit 5", false);
        testPageSqlFormat("SELECT * FROM user OFFSET 5", true);
    }

    private void testPageSqlFormat(String sql, boolean shouldSucceed) {
        QueryWrap<Object> queryWrap = new QueryWrap<>();
        if (shouldSucceed) {
            String result = ReflectionTestUtils.invokeMethod(queryWrap, "pageSql", sql);
            assertNotNull(result);
            assertTrue(result.toLowerCase().contains("limit"));
        } else {
            assertThrows(ServerException.class, () -> 
                ReflectionTestUtils.invokeMethod(queryWrap, "pageSql", sql));
        }
    }

    @Test
    void testSpecWithEmptyPairs() {
        // Arrange
        QueryWrap<Object> queryWrap = new QueryWrap<>();
        queryWrap.template(template);

        // Act
        DatabaseClient.GenericExecuteSpec result = ReflectionTestUtils.invokeMethod(
            queryWrap, 
            "spec",
            "SELECT * FROM table",
            Collections.emptyList()
        );

        // Assert
        assertNotNull(result);
        verify(genericExecuteSpec, never()).bind(anyString(), any());
    }

    @Test
    void testSpecWithMultipleBindings() {
        // Arrange
        QueryWrap<Object> queryWrap = new QueryWrap<>();
        queryWrap.template(template);

        List<Pair<String, Object>> pairs = List.of(
            Pair.of("key1", "value1"),
            Pair.of("key2", 123),
            Pair.of("key3", null)
        );

        // Act
        ReflectionTestUtils.invokeMethod(queryWrap, "spec",
            "SELECT * FROM table WHERE col1 = :key1 AND col2 = :key2 AND col3 = :key3",
            pairs
        );

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(genericExecuteSpec, times(3)).bind(keyCaptor.capture(), valueCaptor.capture());
        
        List<String> capturedKeys = keyCaptor.getAllValues();
        List<Object> capturedValues = valueCaptor.getAllValues();
        
        assertEquals("key1", capturedKeys.get(0));
        assertEquals("value1", capturedValues.get(0));
        assertEquals("key2", capturedKeys.get(1));
        assertEquals(123, capturedValues.get(1));
        assertEquals("key3", capturedKeys.get(2));
        assertNull(capturedValues.get(2));
    }

    @Test
    void testSearchWithSortingOptions() {
        // Arrange
        Search search = new Search();
        search.setProp("name");
        search.setOrder("desc");

        when(genericExecuteSpec.map(rowFunction).all())
                .thenReturn(Flux.just(new UserDemo(), new UserDemo()));
        when(QueryWrap.<UserDemo>build()
                .template(template)
                .count(anyString()))
                .thenReturn(Mono.just(2L));

        QueryWrap<UserDemo> queryWrap = QueryWrap.<UserDemo>build()
                .template(template)
                .sql("select * from user")
                .row(rowFunction);

        // Act & Assert
        StepVerifier.create(queryWrap.search(search).page())
                .expectNextMatches(pageResult -> {
                    // Verify sorting was applied
                    String sql = Objects.requireNonNull(ReflectionTestUtils.getField(queryWrap, "sql")).toString();
                    return sql.contains("order by") && sql.contains("desc");
                })
                .verifyComplete();
    }
}
