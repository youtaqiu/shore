package sh.rime.reactor.r2dbc.page;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.test.util.ReflectionTestUtils;
import sh.rime.reactor.commons.domain.PageResult;
import sh.rime.reactor.commons.domain.Search;
import sh.rime.reactor.security.domain.CurrentUser;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.H2Dialect;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author youta
 **/
class PageWrapTest {

    private ConnectionFactory connectionFactory;


    @BeforeEach
    public void setUp() {
        connectionFactory = new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                        .inMemory("h2db")
                        .username("sa")
                        .build());
    }

    @Test
    @SuppressWarnings("all")
    void testPage() {
        String sql = "CREATE TABLE IF NOT EXISTS t_user ( id BIGINT AUTO_INCREMENT PRIMARY KEY,user_id BIGINT NOT NULL)";
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

    @Test
    void testWhere() {
        // 设置测试用的模板和Criteria
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        Criteria criteria = Criteria.where("user_id").is(1);

        // 创建PageWrap对象并设置Criteria
        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .where(criteria);

        // 使用ReflectionTestUtils获取私有字段criteria的值
        Criteria actualCriteria = (Criteria) ReflectionTestUtils.getField(pageWrap, "criteria");

        // 验证设置的Criteria是否正确
        assertEquals(criteria, actualCriteria, "Criteria should match the provided criteria");
    }

    @Test
    void testSorted() {
        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(databaseClient, H2Dialect.INSTANCE);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .sorted(sort);
        Sort actualSort = (Sort) ReflectionTestUtils.getField(pageWrap, "sort");
        assertEquals(sort, actualSort, "Sort should match the provided sort");
    }

    @Test
    void testPageable() {
        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(databaseClient, H2Dialect.INSTANCE);

        Pageable pageable = PageRequest.of(0, 10);

        PageWrap<CurrentUser> pageWrap = PageWrap.build(CurrentUser.class)
                .template(template)
                .pageable(pageable);
        Pageable actualPageable = (Pageable) ReflectionTestUtils.getField(pageWrap, "pageable");
        assertEquals(pageable, actualPageable, "Pageable should match the provided pageable");
    }

    @Test
    void testSearch() {
        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(databaseClient, H2Dialect.INSTANCE);

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


}
