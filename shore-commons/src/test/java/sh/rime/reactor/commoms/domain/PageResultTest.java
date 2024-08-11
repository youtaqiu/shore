package sh.rime.reactor.commoms.domain;

import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.domain.PageResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult unit test.
 *
 * @author rained
 **/
class PageResultTest {

    @Test
    void testDefaultConstructor() {
        PageResult<String> pageResult = new PageResult<>();

        assertEquals(0, pageResult.getCurrent());
        assertEquals(0, pageResult.getSize());
        assertNull(pageResult.getList());
        assertEquals(0, pageResult.getTotal());
        assertEquals(0, pageResult.getPages());
    }

    @Test
    void testConstructorWithList() {
        List<String> data = Arrays.asList("Item1", "Item2", "Item3");
        PageResult<String> pageResult = new PageResult<>(data);

        assertEquals(data, pageResult.getList());
        assertThrows(UnsupportedOperationException.class, () -> pageResult.getList().add("Item4"));
    }

    @Test
    void testConstructorWithTotalListAndPages() {
        List<String> data = Arrays.asList("Item1", "Item2", "Item3");
        PageResult<String> pageResult = new PageResult<>(100, data, 10);

        assertEquals(100, pageResult.getTotal());
        assertEquals(data, pageResult.getList());
        assertEquals(10, pageResult.getPages());
        assertThrows(UnsupportedOperationException.class, () -> pageResult.getList().add("Item4"));
    }

    @Test
    void testBuilder() {
        List<String> data = Arrays.asList("Item1", "Item2", "Item3");
        PageResult<String> pageResult = PageResult.<String>builder()
                .current(1)
                .size(10)
                .total(100)
                .list(data)
                .pages(10)
                .build();

        assertEquals(1, pageResult.getCurrent());
        assertEquals(10, pageResult.getSize());
        assertEquals(100, pageResult.getTotal());
        assertEquals(data, pageResult.getList());
        assertEquals(10, pageResult.getPages());
    }

    @Test
    void testEmpty() {
        PageResult<String> emptyResult = new PageResult<String>().empty();

        assertNotNull(emptyResult);
        assertEquals(1, emptyResult.getCurrent());
        assertEquals(10, emptyResult.getSize());
        assertEquals(0, emptyResult.getTotal());
        assertEquals(0, emptyResult.getPages());
        assertTrue(emptyResult.getList().isEmpty());
    }
}
