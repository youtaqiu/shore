package run.vexa.reactor.r2dbc.query;

import cn.hutool.core.lang.Pair;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.lang.NonNull;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.vexa.reactor.commons.domain.PageResult;
import run.vexa.reactor.commons.domain.Search;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.r2dbc.page.IPageUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * 查询包装
 *
 * @param <T> 泛型
 * @author youta
 **/
@AllArgsConstructor
@SuppressWarnings("unused")
public class QueryWrap<T> {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public QueryWrap() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * r2dbcEntityTemplate
     */
    private R2dbcEntityTemplate template;
    /**
     * page
     */
    private Mono<PageResult<T>> pageInfo;
    /**
     * list
     */
    private Flux<T> list;
    /**
     * one
     */
    private Mono<T> one;
    /**
     * first
     */
    private Mono<T> first;
    /**
     * count
     */
    private String sql;

    /**
     * search
     */
    private Search search;
    /**
     * row function
     */
    private BiFunction<Row, RowMetadata, T> rowFunction;
    /**
     * bind pairs
     */
    private Pair<String, ?>[] pairs;

    /**
     * template
     *
     * @param template r2dbcEntityTemplate
     * @return {@link QueryWrap}
     */
    public final QueryWrap<T> template(R2dbcEntityTemplate template) {
        this.template = template;
        return this;
    }

    /**
     * search
     *
     * @param search search
     * @return {@link QueryWrap}
     */
    public final QueryWrap<T> search(Search search) {
        this.search = search;
        return this;
    }


    /**
     * rowFunction
     *
     * @param rowFunction rowFunction
     * @return {@link QueryWrap}
     */
    public final QueryWrap<T> row(BiFunction<Row, RowMetadata, T> rowFunction) {
        this.rowFunction = rowFunction;
        return this;
    }


    /**
     * pairs
     *
     * @param pairs pairs
     * @return {@link QueryWrap}
     */
    @SafeVarargs
    @SuppressWarnings("all")
    public final QueryWrap<T> bind(Pair<String, ?>... pairs) {
        this.pairs = pairs;
        return this;
    }

    /**
     * sql
     *
     * @param sql sql
     * @return {@link QueryWrap}
     */
    public final QueryWrap<T> sql(String sql) {
        this.sql = sql;
        return this;
    }

    /**
     * sql
     *
     * @param search search
     * @return {@link QueryWrap}
     */
    public final QueryWrap<T> sql(Search search) {
        this.search = search;
        return this;
    }

    /**
     * select one
     *
     * @return T
     */
    public Mono<T> one() {
        this.one = this.spec(sql, getPairs())
                .map(rowFunction)
                .one();
        return this.one;
    }

    /**
     * get pairs
     * @return pairs
     */
    private List<Pair<String, ?>> getPairs() {
        return Option.of(pairs)
                .map(List::of)
                .getOrElse(List.of());
    }

    /**
     * select first
     *
     * @return T
     */
    public Mono<T> first() {

        this.first = this.spec(sql, getPairs())
                .map(rowFunction)
                .first();
        return this.first;
    }

    /**
     * select list
     *
     * @return T list
     */
    public Flux<T> list() {
        this.list = this.spec(sql, getPairs())
                .map(rowFunction)
                .all();
        return this.list;
    }


    /**
     * select page
     *
     * @return PageResult
     */
    @NonNull
    public final Mono<PageResult<T>> page() {
        return Mono.zip(this.pageSpec(search, this.pageSql(sql), pairs)
                        .map(rowFunction)
                        .all()
                        .collectList(), this.count(sql, pairs))
                .map(tuple2 -> PageResult.<T>builder()
                        .list(tuple2.getT1())
                        .total(Math.toIntExact(tuple2.getT2()))
                        .size(search.getSize())
                        .current(search.getCurrent())
                        .pages(IPageUtil.getPages(Math.toIntExact(tuple2.getT2()), search.getSize()))
                        .build());
    }

    /**
     * select page
     *
     * @param fn  fn
     * @param <W> W
     * @return PageResult
     */
    public <W> Mono<PageResult<W>> page(Function<List<T>, List<W>> fn) {
        return Mono.zip(this.pageSpec(search, this.pageSql(sql), pairs)
                        .map(rowFunction)
                        .all()
                        .collectList(), this.count(sql, pairs))
                .map(tuple2 -> PageResult.<W>builder()
                        .list(fn.apply(tuple2.getT1()))
                        .total(Math.toIntExact(tuple2.getT2()))
                        .size(search.getSize())
                        .current(search.getCurrent())
                        .pages(IPageUtil.getPages(Math.toIntExact(tuple2.getT2()), search.getSize()))
                        .build());
    }


    /**
     * page spec
     *
     * @param search search
     * @param sql    sql
     * @param pairs  pairs
     * @return spec
     */
    private DatabaseClient.GenericExecuteSpec pageSpec(@NonNull Search search, @NonNull String sql, Pair<String, ?>[] pairs) {
        return this.spec(sql, getPairs())
                .bind("limit", search.getSize())
                .bind("offset", (search.getCurrent() - 1) * search.getSize());
    }

    /**
     * 查询总数
     *
     * @param sql   sql
     * @param pairs pairs
     * @return count
     */
    @SafeVarargs
    @NonNull
    public final Mono<Long> count(@NonNull String sql, Pair<String, ?>... pairs) {
        return this.spec(this.countSql(sql), getPairs())
                .map((row, rowMetadata) -> row.get(0, Long.class))
                .one();
    }

    /**
     * spec
     *
     * @param countSql countSql
     * @param pairs    pairs
     * @return spec
     */
    private DatabaseClient.GenericExecuteSpec spec(String countSql, List<Pair<String, ?>> pairs) {
        var client = this.template.getDatabaseClient();
        var spec = client.sql(countSql);
        if (pairs != null) {
            for (Pair<String, ?> pair : pairs) {
                spec = spec.bind(pair.getKey(), pair.getValue());
            }
        }
        return spec;
    }

    /**
     * 获取分页sql
     *
     * @param sql sql
     */
    @NonNull
    private String pageSql(@NonNull String sql) {
        if (sql.toLowerCase().contains("limit")) {
            throw new ServerException("sql error");
        }
        var limitSql = " limit :limit offset :offset";
        return sql + limitSql;
    }

    /**
     * 获取查询总数sql
     *
     * @param sql sql
     */
    @NonNull
    private String countSql(@NonNull String sql) {
        var prefixSql = "select count(*) from (";
        var suffixSql = ") as count_data";
        return prefixSql + sql + suffixSql;
    }

    /**
     * build
     *
     * @param <T> T
     * @return {@link QueryWrap}
     */
    public static <T> QueryWrap<T> build() {
        return new QueryWrap<>();
    }
}
