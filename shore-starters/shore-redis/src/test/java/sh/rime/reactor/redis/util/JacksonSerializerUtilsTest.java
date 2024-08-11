package sh.rime.reactor.redis.util;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The type Jackson serializer utils test.
 *
 * @author youta
 **/
@SuppressWarnings("unused")
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

}
