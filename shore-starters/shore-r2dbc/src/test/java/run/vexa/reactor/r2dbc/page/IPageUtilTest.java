package run.vexa.reactor.r2dbc.page;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import run.vexa.reactor.commons.domain.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * IPageUtil 测试类
 *
 * @author youta
 **/
class IPageUtilTest {

    @Test
    void testPageRequestWithInt() {
        PageRequest pageRequest = IPageUtil.pageRequest(1, 10);
        assertEquals(0, pageRequest.getPageNumber()); // page number is 0-based
        assertEquals(10, pageRequest.getPageSize());

        pageRequest = IPageUtil.pageRequest(2, 20);
        assertEquals(1, pageRequest.getPageNumber());
        assertEquals(20, pageRequest.getPageSize());
    }

    @Test
    void testPageRequestWithSearch() {
        Search search = new Search();
        search.setCurrent(1);
        search.setSize(10);
        PageRequest pageRequest = IPageUtil.pageRequest(search);
        assertEquals(0, pageRequest.getPageNumber()); // page number is 0-based
        assertEquals(10, pageRequest.getPageSize());

        search.setCurrent(2);
        search.setSize(20);
        pageRequest = IPageUtil.pageRequest(search);
        assertEquals(1, pageRequest.getPageNumber());
        assertEquals(20, pageRequest.getPageSize());
    }

    @Test
    void testGetPages() {
        // 测试完全整除的情况
        assertEquals(2, IPageUtil.getPages(10, 5), "10/5 should be 2 pages");
        assertEquals(3, IPageUtil.getPages(15, 5), "15/5 should be 3 pages");

        // 测试有余数的情况
        assertEquals(3, IPageUtil.getPages(11, 5), "11/5 should be 3 pages");
        assertEquals(2, IPageUtil.getPages(11, 10), "11/10 should be 2 pages");

        // 测试边界情况
        assertEquals(0, IPageUtil.getPages(0, 5), "0 total should be 0 pages");
        assertEquals(1, IPageUtil.getPages(1, 5), "1 total should be 1 page");
        assertEquals(1, IPageUtil.getPages(5, 5), "exact fit should be 1 page");
        assertEquals(2, IPageUtil.getPages(6, 5), "one over should be 2 pages");
    }
}
