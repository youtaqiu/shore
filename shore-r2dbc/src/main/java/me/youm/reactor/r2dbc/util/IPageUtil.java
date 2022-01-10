package me.youm.reactor.r2dbc.util;

import me.youm.reactor.r2dbc.page.IPage;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @author youta
 **/
public class IPageUtil {

    public static PageRequest pageRequest(int current, int size) {
        return PageRequest.of(current - 1, size);
    }

    public static int getPages(int total, int size) {
        return total % size == 0 ? (total / size) : (total / size + 1);
    }

    public static <T> Mono<IPage<T>> toPage(Integer current, Integer size, Flux<T> flux, Mono<Long> counts) {
        return flux.collectList()
                .map(IPage::new)
                .flatMap(page -> counts
                        .map(count -> page.setCurrent(current)
                                .setSize(size)
                                .setTotal(count.intValue())
                                .setPages(getPages(count.intValue(), size))
                        )
                );

    }

}
