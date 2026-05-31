package run.vexa.reactor.redis.util;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Jackson serializer utils test.
 *
 * @author youta
 **/
class JacksonSerializerUtilsTest {


    @Test
    void testJsonSerializer() {
        UserDemo user = new UserDemo("Alice", 25);
        RedisSerializer<UserDemo> serializer = JacksonSerializerUtils.json(UserDemo.class);

        byte[] serializedUser = serializer.serialize(user);
        assertNotNull(serializedUser);

        UserDemo deserializedUser = serializer.deserialize(serializedUser);
        assertNotNull(deserializedUser);
        assertEquals(user.getName(), deserializedUser.getName());
        assertEquals(user.getAge(), deserializedUser.getAge());
    }

    @Test
    void testJsonWithoutArguments() {
        RedisSerializer<Object> serializer = JacksonSerializerUtils.json();
        PolymorphicValue value = new PolymorphicValue("default");

        byte[] bytes = serializer.serialize(value);
        assertNotNull(bytes);

        Object restored = serializer.deserialize(bytes);
        assertTrue(restored instanceof PolymorphicValue);
        assertEquals(value.getValue(), ((PolymorphicValue) restored).getValue());
    }

    @Test
    void testUtilityConstructorIsInaccessible() throws Exception {
        Constructor<JacksonSerializerUtils> constructor = JacksonSerializerUtils.class.getDeclaredConstructor();

        constructor.trySetAccessible();
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    }

    /**
     * The type User demo.
     */
    @Setter
    @Getter
    static class UserDemo {
        private String name;
        private int age;

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        UserDemo() {
        }

        UserDemo(String name, int age) {
            this.name = name;
            this.age = age;
        }

    }

    @Setter
    @Getter
    static class PolymorphicValue {
        private String value;

        PolymorphicValue() {
        }

        PolymorphicValue(String value) {
            this.value = value;
        }
    }

}
