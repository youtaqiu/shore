package io.irain.reactor.commons.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.type.TypeBindings;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Pair is a class that represents a key-value pair.
 * It includes a key and a value, and provides methods for manipulating the key-value pair.
 * This class is also serializable and cloneable, which means it can be converted to a byte stream and restored later, and it can be cloned to create a copy.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 * @author youta
 */
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
@JsonSerialize(using = Pair.PairJsonSerializer.class)
@JsonDeserialize(using = Pair.PairJsonDeserializer.class)
public class Pair<K, V> implements Serializable, Cloneable {

    /**
     * The constant EMPTY.
     */
    public static final Pair<?, ?> EMPTY = Pair.of(null, null);

    /**
     * key
     */
    private final K key;

    /**
     * value
     */
    private final V value;

    /**
     * Get Key.
     *
     * @return the k
     */
    public K key() {
        return key;
    }

    /**
     * Get Value
     *
     * @return the v
     */
    public V value() {
        return value;
    }

    /**
     * To map map.
     *
     * @return the map
     */
    public Map<K, V> toMap() {
        return Map.of(key, value);
    }

    /**
     * To entry map . entry.
     *
     * @return the map . entry
     */
    public Map.Entry<K, V> toEntry() {
        return Map.entry(key, value);
    }

    /**
     * Of pair.
     *
     * @param <K>   the type parameter
     * @param <V>   the type parameter
     * @param entry the entry
     * @return the pair
     */
    public static <K, V> Pair<K, V> of(Map.Entry<K, V> entry) {
        return of(entry.getKey(), entry.getValue());
    }

    /**
     * Map pair.
     *
     * @param <S>           the type parameter
     * @param <U>           the type parameter
     * @param keyFunction   the key function
     * @param valueFunction the value function
     * @return the pair
     */
    public <S, U> Pair<S, U> map(Function<K, S> keyFunction, Function<V, U> valueFunction) {
        return of(keyFunction.apply(key), valueFunction.apply(value));
    }

    /**
     * Key map pair.
     *
     * @param <S>         the type parameter
     * @param keyFunction the key function
     * @return the pair
     */
    public <S> Pair<S, V> keyMap(Function<K, S> keyFunction) {
        return map(keyFunction, Function.identity());
    }

    /**
     * Value map pair.
     *
     * @param <U>           the type parameter
     * @param valueFunction the value function
     * @return the pair
     */
    public <U> Pair<K, U> valueMap(Function<V, U> valueFunction) {
        return map(Function.identity(), valueFunction);
    }

    /**
     * Flat map pair.
     *
     * @param <S>        the type parameter
     * @param <U>        the type parameter
     * @param biFunction the bi function
     * @return the pair
     */
    public <S, U> Pair<S, U> flatMap(BiFunction<K, V, Pair<S, U>> biFunction) {
        return biFunction.apply(key, value);
    }

    @Serial
    private static final long serialVersionUID = -2303547536834226401L;

    @SuppressWarnings("unchecked")
    @Override
    protected Pair<K, V> clone() throws CloneNotSupportedException {
        return (Pair<K, V>) super.clone();
    }

    @Override
    public String toString() {
        return "{" + key + ":" + value + "}";
    }

    /**
     * The type Pair json serializer.
     */
    static class PairJsonSerializer extends StdScalarSerializer<Pair<Object, Object>> implements ContextualSerializer {

        /**
         * Instantiates a new Pair json serializer.
         */
        protected PairJsonSerializer() {
            super(Pair.class, false);
        }

        private JsonSerializer<Object> keySerializer;

        @Override
        public void serialize(Pair<Object, Object> pair, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject(pair);
            keySerializer.serialize(pair.key(), gen, provider);
            gen.writeObject(pair.value());
            gen.writeEndObject();
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
            keySerializer = prov.findKeySerializer(Object.class, property);
            return this;
        }
    }

    /**
     * The type Pair json deserializer.
     */
    static class PairJsonDeserializer extends StdScalarDeserializer<Pair<Object, Object>> implements ContextualDeserializer {

        private KeyDeserializer keyDeserializer;
        private JavaType valueType;

        /**
         * Instantiates a new Pair json deserializer.
         */
        protected PairJsonDeserializer() {
            super(Pair.class);
        }

        @Override
        public Pair<Object, Object> deserialize(JsonParser p, DeserializationContext context) throws IOException {
            p.nextToken();
            String name = p.currentName();
            JsonNode valueNode = context.readTree(p).get(name);
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            return Pair.of(keyDeserializer.deserializeKey(name, context), mapper.convertValue(valueNode, valueType));
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) throws JsonMappingException {
            JavaType contextualType = context.getContextualType();
            TypeBindings bindings = contextualType.getBindings();
            JavaType keyType = bindings.getBoundType(0);
            valueType = bindings.getBoundType(1);
            keyDeserializer = context.findKeyDeserializer(keyType, property);
            return this;
        }
    }
}
