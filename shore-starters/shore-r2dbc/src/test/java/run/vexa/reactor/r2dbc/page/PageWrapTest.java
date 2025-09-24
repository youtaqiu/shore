package run.vexa.reactor.r2dbc.page;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.H2Dialect;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.domain.PageResult;
import run.vexa.reactor.commons.domain.Search;
import run.vexa.reactor.security.domain.CurrentUser;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link PageWrap}.
 */
class PageWrapTest {

    private static final Duration BLOCK_TIMEOUT = Duration.ofSeconds(5);

    private DatabaseClient databaseClient;
    private R2dbcEntityTemplate template;

    @BeforeEach
    void setUp() {
        ConnectionFactory connectionFactory = new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                        .inMemory("h2db-" + System.nanoTime())
                        .property("DB_CLOSE_DELAY", "-1")
                        .username("sa")
                        .build());
        databaseClient = DatabaseClient.create(connectionFactory);
        template = new R2dbcEntityTemplate(databaseClient, H2Dialect.INSTANCE);
        initializeSchema();
    }

    @Test
    void pageReturnsPaginatedResultWithSort() {
        insertUser(1L, "user1");
        insertUser(2L, "user2");
        insertUser(3L, "user3");

        PageWrap<TestUser> pageWrap = PageWrap.build(TestUser.class)
                .template(template)
                .where(Criteria.where("user_id").greaterThan(0))
                .sorted(Sort.by(Sort.Direction.DESC, "user_id"))
                .pageable(PageRequest.of(0, 2));

        StepVerifier.create(pageWrap.page())
                .assertNext(result -> {
                    assertEquals(3, result.getTotal());
                    assertEquals(2, result.getSize());
                    assertEquals(1, result.getCurrent());
                    assertEquals(2, result.getPages());
                    List<Long> userIds = result.getList().stream()
                            .map(TestUser::getUserId)
                            .toList();
                    assertEquals(List.of(3L, 2L), userIds);
                })
                .verifyComplete();
    }

    @Test
    void pageAppliesMappingFunction() {
        insertUser(1L, "user1");
        insertUser(2L, "user2");
        insertUser(3L, "user3");

        PageWrap<TestUser> pageWrap = PageWrap.build(TestUser.class)
                .template(template)
                .where(Criteria.where("user_id").greaterThan(0))
                .pageable(PageRequest.of(1, 2));

        Mono<PageResult<String>> pageResult = pageWrap.page(users -> users.stream()
                .map(TestUser::getUsername)
                .toList());

        StepVerifier.create(pageResult)
                .assertNext(result -> {
                    assertEquals(3, result.getTotal());
                    assertEquals(2, result.getSize());
                    assertEquals(2, result.getCurrent());
                    assertEquals(2, result.getPages());
                    assertEquals(List.of("user3"), result.getList());
                })
                .verifyComplete();
    }

    @Test
    void testWhere() {
        Criteria criteria = Criteria.where("user_id").is(1);

        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .where(criteria);

        Criteria actualCriteria = (Criteria) ReflectionTestUtils.getField(pageWrap, "criteria");

        assertEquals(criteria, actualCriteria, "Criteria should match the provided criteria");
    }

    @Test
    void testSorted() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .sorted(sort);
        Sort actualSort = (Sort) ReflectionTestUtils.getField(pageWrap, "sort");
        assertEquals(sort, actualSort, "Sort should match the provided sort");
    }

    @Test
    void testPageable() {
        Pageable pageable = PageRequest.of(0, 10);

        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .pageable(pageable);
        Pageable actualPageable = (Pageable) ReflectionTestUtils.getField(pageWrap, "pageable");
        assertEquals(pageable, actualPageable, "Pageable should match the provided pageable");
    }

    @Test
    void testSearch() {
        Search search = new Search();
        search.setCurrent(1);
        search.setSize(20);

        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .search(search);

        Pageable expectedPageable = PageRequest.of(search.getCurrent() - 1, search.getSize());
        Pageable actualPageable = (Pageable) ReflectionTestUtils.getField(pageWrap, "pageable");
        assertEquals(expectedPageable, actualPageable, "Pageable should match the search criteria");
    }

    private void initializeSchema() {
        executeSql("DROP TABLE IF EXISTS test_user");
        executeSql("CREATE TABLE test_user (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, user_id BIGINT NOT NULL, username VARCHAR(64) NOT NULL)");
    }

    private void executeSql(String sql) {
        databaseClient.sql(sql)
                .fetch()
                .rowsUpdated()
                .then()
                .block(BLOCK_TIMEOUT);
    }

    private void insertUser(long userId, String username) {
        TestUser user = new TestUser(null, userId, username);
        template.insert(TestUser.class)
                .using(user)
                .block(BLOCK_TIMEOUT);
    }

    @Table("test_user")
    static class TestUser {

        @Id
        private Long id;

        @Column("user_id")
        private Long userId;

        private String username;

        TestUser() {
        }

        TestUser(Long id, Long userId, String username) {
            this.id = id;
            this.userId = userId;
            this.username = username;
        }

        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        Long getUserId() {
            return userId;
        }

        void setUserId(Long userId) {
            this.userId = userId;
        }

        String getUsername() {
            return username;
        }

        void setUsername(String username) {
            this.username = username;
        }
    }
}
