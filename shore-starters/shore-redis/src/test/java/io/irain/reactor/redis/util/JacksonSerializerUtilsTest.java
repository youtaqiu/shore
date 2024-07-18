package io.irain.reactor.redis.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
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

    static class UserDemo {
        private String name;
        private int age;

        public UserDemo() {
        }

        public UserDemo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // getters and setters

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}
