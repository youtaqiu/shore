package io.irain.reactor.core.util;


import io.irain.reactor.commons.exception.ServerException;
import io.irain.reactor.commons.exception.ServerFailure;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author youta
 */
@SuppressWarnings("unused")
public class OptionalBean<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1905122041950251207L;

    private static final OptionalBean<?> EMPTY = new OptionalBean<>();

    /**
     * 数据
     */
    private final T value;

    private OptionalBean() {
        this.value = null;
    }

    /**
     * 空值会抛出空指针
     *
     * @param value value
     */
    private OptionalBean(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * 包装一个不能为空的 bean
     *
     * @param value value
     * @param <T>   T
     * @return T
     */

    public static <T> OptionalBean<T> of(T value) {
        return new OptionalBean<>(value);
    }

    /**
     * 包装一个可能为空的 bean
     *
     * @param value value
     * @param <T>   T
     * @return T
     */

    public static <T> OptionalBean<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * 取出具体的值
     *
     * @return T
     */
    public T get() {
        return Objects.isNull(value) ? null : value;
    }

    /**
     * 取出一个可能为空的对象
     *
     * @param fn  fn
     * @param <R> R
     * @return R
     */
    public <R> OptionalBean<R> getBean(Function<? super T, ? extends R> fn) {
        return Objects.isNull(value) ? OptionalBean.empty() : OptionalBean.ofNullable(fn.apply(value));
    }

    /**
     * 如果目标值为空 获取一个默认值
     *
     * @param other other
     * @return T
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * 如果目标值为空 通过lambda表达式获取一个值
     *
     * @param other other
     * @return T
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * 如果目标值为空 抛出一个异常
     *
     * @param exceptionSupplier exceptionSupplier
     * @param <X>               X
     * @return X
     * @throws X Throwable
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 否则进行抛出异常
     * @param exceptionEnum 异常枚举
     * @return T
     * @param <X> X
     * @throws X Throwable
     */
    public <X extends Throwable> T orElseThrow(ServerFailure exceptionEnum) throws X {
        if (value != null) {
            return value;
        } else {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 如果目标值不为空 返回true
     * @return boolean
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * 如果目标值不为空 执行一个lambda表达式
     * @param consumer consumer
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    /**
     * 空值常量
     *
     * @param <T> T
     * @return T
     */

    public static <T> OptionalBean<T> empty() {

        @SuppressWarnings("unchecked")
        OptionalBean<T> none = (OptionalBean<T>) EMPTY;
        return none;
    }

}
