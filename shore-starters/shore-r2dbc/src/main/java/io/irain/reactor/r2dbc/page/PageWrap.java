package io.irain.reactor.r2dbc.page;

import io.irain.reactor.commons.domain.PageResult;
import io.irain.reactor.commons.domain.Search;
import io.irain.reactor.core.util.OptionalBean;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author youta
 **/
@AllArgsConstructor
@NoArgsConstructor
public class PageWrap<T> {

    private R2dbcEntityTemplate template;
    private Class<T> clz;
    private Criteria criteria;
    private Pageable pageable;
    private Sort sort;


    /**
     * template
     *
     * @param template r2dbcEntityTemplate
     * @return {@link PageWrap}
     */
    public final PageWrap<T> template(R2dbcEntityTemplate template) {
        this.template = template;
        return this;
    }

    /**
     * 条件
     *
     * @param criteria criteria
     * @return list
     */
    public PageWrap<T> where(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }


    /**
     * 排序
     *
     * @param sort sort
     * @return list
     */
    public PageWrap<T> sorted(Sort sort) {
        this.sort = sort;
        return this;
    }

    /**
     * 分页
     *
     * @param pageable pageable
     * @return list
     */
    public PageWrap<T> pageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    /**
     * 分页
     *
     * @param search search
     * @return list
     */
    public PageWrap<T> search(Search search) {
        this.pageable = IPageUtil.pageRequest(search);
        return this;
    }

    /**
     * 执行
     *
     * @return list
     */
    public Mono<PageResult<T>> page() {
        return Mono.zip(selectList(), selectCount())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(pageT());
    }

    /**
     * 分页结果转换
     *
     * @param fn  转换函数
     * @param <R> 泛型
     * @return list
     */
    public <R> Mono<PageResult<R>> page(Function<List<T>, List<R>> fn) {
        return Mono.zip(selectList().flatMap(x -> Mono.just(fn.apply(x))),
                        selectCount())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(pageR());
    }


    /**
     * 查询列表
     *
     * @return list
     */
    private Mono<List<T>> selectList() {
        Query query;
        if (sort != null) {
            query = query(criteria)
                    .sort(sort)
                    .with(pageable);
        } else {
            query = query(criteria)
                    .with(pageable);
        }

        return OptionalBean.ofNullable(template.select(clz)
                        .matching(query)
                        .all()
                        .collectList())
                .orElse(Mono.empty());
    }

    /**
     * 查询总数
     *
     * @return list
     */
    private Mono<Long> selectCount() {
        return OptionalBean.ofNullable(template.select(clz)
                        .matching(query(criteria))
                        .count())
                .orElse(Mono.just(0L));
    }


    /**
     * 获取分页函数
     *
     * @return list
     */
    private Function<PageImpl<T>, PageResult<T>> pageT() {
        return page ->
                PageResult.<T>builder()
                        .list(page.getContent())
                        .total((int) page.getTotalElements())
                        .size(pageable.getPageSize())
                        .current(pageable.getPageNumber() + 1)
                        .pages(IPageUtil.getPages((int) page.getTotalElements(), pageable.getPageSize()))
                        .build();
    }

    private <M> Function<PageImpl<M>, PageResult<M>> pageR() {
        return page ->
                PageResult.<M>builder()
                        .list(page.getContent())
                        .total((int) page.getTotalElements())
                        .size(pageable.getPageSize())
                        .current(pageable.getPageNumber() + 1)
                        .pages(IPageUtil.getPages((int) page.getTotalElements(), pageable.getPageSize()))
                        .build();
    }

    /**
     * build
     *
     * @param <T> T
     * @param clz clz
     * @return {@link PageWrap}
     */
    public static <T> PageWrap<T> build(Class<T> clz) {
        return new PageWrap<>(clz);
    }

    /**
     * 构造器
     *
     * @param clz clz
     */
    public PageWrap(Class<T> clz) {
        this.clz = clz;
    }

}
