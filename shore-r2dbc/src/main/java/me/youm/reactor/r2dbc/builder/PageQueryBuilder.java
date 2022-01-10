package me.youm.reactor.r2dbc.builder;

import me.youm.reactor.r2dbc.page.IPage;
import me.youm.reactor.r2dbc.util.IPageUtil;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.function.Function;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author youta
 **/
public class PageQueryBuilder<T> {

    private final R2dbcEntityTemplate template;
    private final Class<T> clz;
    private Criteria criteria;
    private Pageable pageable;
    private Sort sort;

    public PageQueryBuilder(R2dbcEntityTemplate template, Class<T> clz) {
        this.template = template;
        this.clz = clz;
    }

    public PageQueryBuilder<T> where(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }

    public PageQueryBuilder<T> sorted(Sort sort) {
        this.sort = sort;
        return this;
    }

    public PageQueryBuilder<T> pageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public Mono<IPage<T>> apply() {
        return Mono.zip(selectList(), selectCount())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(getPageFunction())
                ;
    }


    public <R> Mono<IPage<R>> apply(Function<List<T>, Mono<List<R>>> fn) {
        return Mono.zip(selectList().flatMap(fn), selectCount())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(getPageIPageFunction())
                ;
    }

    public <R> Mono<IPage<R>> flatTuple(Function<Tuple2<List<T>, Long>, Mono<Tuple2<List<R>, Long>>> fn) {
        return Mono.zip(selectList(), selectCount())
                .flatMap(fn)
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(getPageIPageFunction())
                ;
    }

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
        return template.select(clz)
                .matching(query)
                .all()
                .collectList();
    }

    private Mono<Long> selectCount() {
        return template.select(clz)
                .matching(query(criteria))
                .count();
    }


    private Function<PageImpl<T>, IPage<T>> getPageFunction() {
        return page -> {
            IPage<T> noticeIPage = new IPage<>();
            noticeIPage.setList(page.getContent());
            noticeIPage.setTotal((int) page.getTotalElements());
            noticeIPage.setSize(pageable.getPageSize());
            noticeIPage.setCurrent(pageable.getPageNumber() + 1);
            noticeIPage.setPages(IPageUtil.getPages(noticeIPage.getTotal(), pageable.getPageSize()));
            return noticeIPage;
        };
    }

    private <R> Function<PageImpl<R>, IPage<R>> getPageIPageFunction() {
        return page -> {
            IPage<R> noticeIPage = new IPage<>();
            noticeIPage.setList(page.getContent());
            noticeIPage.setTotal((int) page.getTotalElements());
            noticeIPage.setSize(pageable.getPageSize());
            noticeIPage.setCurrent(pageable.getPageNumber() + 1);
            noticeIPage.setPages(IPageUtil.getPages(noticeIPage.getTotal(), pageable.getPageSize()));
            return noticeIPage;
        };
    }

}
