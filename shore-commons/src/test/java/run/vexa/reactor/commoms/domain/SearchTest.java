package run.vexa.reactor.commoms.domain;


import org.junit.jupiter.api.Test;
import run.vexa.reactor.commons.domain.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Search unit test.
 *
 * @author rained
 **/
class SearchTest {

    @Test
    void testSearchInitialization() {
        Search search = new Search();
        assertEquals(1, search.getCurrent());
        assertEquals(10, search.getSize());
        assertNull(search.getKeyword());
    }

    @Test
    void testSearchSettersAndGetters() {
        Search search = new Search();
        search.setCurrent(2);
        search.setSize(20);
        search.setKeyword("test");

        assertEquals(2, search.getCurrent());
        assertEquals(20, search.getSize());
        assertEquals("test", search.getKeyword());
    }
}

