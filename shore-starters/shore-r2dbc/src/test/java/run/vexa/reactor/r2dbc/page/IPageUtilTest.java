package run.vexa.reactor.r2dbc.page;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import run.vexa.reactor.commons.domain.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author youta
 **/
class IPageUtilTest {

    @Test
    void testPageRequestWithInt() {
        PageRequest pageRequest = IPageUtil.pageRequest(1, 10);
        assertEquals(0, pageRequest.getPageNumber());
        assertEquals(10, pageRequest.getPageSize());
    }

    @Test
    void testPageRequestWithSearch() {
        Search search = new Search();
        PageRequest pageRequest = IPageUtil.pageRequest(search);
        assertEquals(0, pageRequest.getPageNumber());
        assertEquals(10, pageRequest.getPageSize());
    }

    @Test
    void testGetPages() {
        int total = 10;
        int size = 5;
        int pages = IPageUtil.getPages(total, size);
        assertEquals(2, pages);
    }

}
