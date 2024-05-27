package io.irain.reactor.r2dbc.page;

import io.irain.reactor.commons.domain.Search;
import org.springframework.data.domain.PageRequest;

/**
 * @author youta
 **/
@SuppressWarnings("unused")
public class IPageUtil {

    private IPageUtil() {
    }

    /**
     * 获取分页请求
     *
     * @param current 当前页
     * @param size    页大小
     * @return 分页请求
     */
    public static PageRequest pageRequest(int current, int size) {
        return PageRequest.of(current - 1, size);
    }

    /**
     * 获取分页请求
     *
     * @param search 搜索
     * @return 分页请求
     */
    public static PageRequest pageRequest(Search search) {
        return PageRequest.of(search.getCurrent() - 1, search.getSize());
    }

    /**
     * 获取页数
     *
     * @param total 总数
     * @param size  页大小
     * @return 页数
     */
    public static int getPages(int total, int size) {
        return total % size == 0 ? (total / size) : (total / size + 1);
    }


}
