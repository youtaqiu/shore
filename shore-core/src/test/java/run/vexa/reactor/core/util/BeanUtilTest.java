package run.vexa.reactor.core.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BeanUtil unit test.
 *
 * @author rained
 **/
class BeanUtilTest {

    private TestBean source;
    private TestBean target;

    @BeforeEach
    void setUp() {
        source = new TestBean();
        source.setId(1);
        source.setName("Test Name");

        target = new TestBean();
    }

    @Test
    void testCopy() {
        BeanUtil.copy(source, target);

        assertEquals(source.getId(), target.getId());
        assertEquals(source.getName(), target.getName());
    }

    @Test
    void testCopyCreatesNewInstance() {
        TestBean copied = BeanUtil.copy(source, TestBean.class);

        assertNotSame(source, copied);
        assertEquals(source.getId(), copied.getId());
        assertEquals(source.getName(), copied.getName());
    }

    @Test
    void testCopyWithNullProperties() {
        source.setName(null);

        BeanUtil.copy(source, target);

        assertEquals(source.getId(), target.getId());
        assertNull(target.getName());
    }

    @Test
    void testBeanToMap() {
        Map<String, Object> map = BeanUtil.beanToMap(source);

        assertEquals(1, map.get("id"));
        assertEquals("Test Name", map.get("name"));
    }

    @Test
    void testCopyToList() {
        List<TestBean> list = List.of(source);
        List<TestBean> copiedList = BeanUtil.copyToList(list, TestBean.class);

        assertEquals(1, copiedList.size());
        assertEquals(source.getId(), copiedList.getFirst().getId());
        assertEquals(source.getName(), copiedList.getFirst().getName());
    }

    @Test
    void testCopyToListWithNullCollection() {
        List<TestBean> copiedList = BeanUtil.copyToList(null, TestBean.class);

        assertTrue(copiedList.isEmpty());
    }

    @Test
    void testCopyToListWithEmptyCollection() {
        List<TestBean> sourceList = new ArrayList<>();

        List<TestBean> copiedList = BeanUtil.copyToList(sourceList, TestBean.class);

        assertTrue(copiedList.isEmpty());
        assertNotSame(sourceList, copiedList);
    }

    @Test
    void testGetInstance() {
        ObjectMapper objectMapper = BeanUtil.getInstance();
        ObjectMapper secondInstance = BeanUtil.getInstance();

        assertNotNull(objectMapper);
        assertSame(objectMapper, secondInstance);

        ObjectMapper copy = objectMapper.copy();

        assertNotSame(objectMapper, copy);
        assertTrue(objectMapper.getFactory().isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES));
        assertTrue(copy.getFactory().isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES));
        assertTrue(copy.getFactory().isEnabled(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature()));
    }

    // 测试用的简单的Bean类
    @Setter
    @Getter
    static class TestBean {
        private Integer id;
        private String name;

    }
}
