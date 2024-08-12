package sh.rime.reactor.commoms.function;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.domain.Search;
import sh.rime.reactor.commons.function.Customizer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Customizer unit test.
 *
 * @author rained
 **/
class CustomizerTest {

    @Test
    void testCustomizeKeyword() {
        Search search = new Search();

        Customizer<Search> setName = p -> p.setKeyword("John Doe");
        setName.customize(search);

        assertEquals("John Doe", search.getKeyword(), "The name should be 'John Doe'");
    }

    @Test
    void testCustomizeSize() {
        Search search = new Search();

        Customizer<Search> setSize = p -> p.setSize(30);
        setSize.customize(search);

        assertEquals(30, search.getSize(), "The size should be 30");
    }

    @Test
    void testMultipleCustomizers() {
        Search search = new Search();

        // 定义多个Customizer
        Customizer<Search> setKeyword = p -> p.setKeyword("Jane Doe");
        Customizer<Search> setSize = p -> p.setSize(25);

        // 应用定制
        setKeyword.customize(search);
        setSize.customize(search);

        // 验证name和age是否都被正确设置
        assertEquals("Jane Doe", search.getKeyword(), "The keyword should be 'Jane Doe'");
        assertEquals(25, search.getSize(), "The size should be 25");
    }
}

