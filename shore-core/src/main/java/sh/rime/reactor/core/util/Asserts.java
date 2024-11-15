package sh.rime.reactor.core.util;

import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.commons.exception.ServerFailure;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 断言
 *
 * @author youta
 */
@SuppressWarnings("unused")
public final class Asserts {


    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    private Asserts() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 断言状态
     *
     * @param expression    表达式
     * @param exceptionEnum 异常枚举
     */
    public static void state(boolean expression, ServerFailure exceptionEnum) {
        if (!expression) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言正确
     *
     * @param expression    表达式
     * @param exceptionEnum 异常枚举
     */
    public static void isTrue(boolean expression, ServerFailure exceptionEnum) {
        if (!expression) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言正确
     *
     * @param expression 表达式
     * @param exception  异常
     */
    public static void isTrue(boolean expression, ServerException exception) {
        if (!expression) {
            throw exception;
        }
    }

    /**
     * 断言错误
     *
     * @param expression    表达式
     * @param exceptionEnum 异常枚举
     */
    public static void isNotTrue(boolean expression, ServerFailure exceptionEnum) {
        if (expression) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言有值
     *
     * @param str           字符串
     * @param exceptionEnum 异常枚举
     */
    public static void hasText(@Nullable String str, ServerFailure exceptionEnum) {
        if (!StringUtils.hasText(str)) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言有值
     *
     * @param str           字符串
     * @param exceptionEnum 异常枚举
     */
    public static void hasLength(@Nullable String str, ServerFailure exceptionEnum) {
        if (!StringUtils.hasLength(str)) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言有值
     *
     * @param str       字符串
     * @param exception 异常
     */
    public static void hasLength(@Nullable String str, ServerException exception) {
        if (!StringUtils.hasLength(str)) {
            throw exception;
        }
    }


    /**
     * 断言有值
     *
     * @param str           字符串
     * @param exceptionEnum 异常枚举
     */
    public static void hasText(@Nullable CharSequence str, ServerFailure exceptionEnum) {
        if (!StringUtils.hasText(str)) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言有值
     *
     * @param str           字符串
     * @param exceptionEnum 异常枚举
     */
    public static void hasLength(@Nullable CharSequence str, ServerFailure exceptionEnum) {
        if (!StringUtils.hasLength(str)) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言对象为空
     *
     * @param object        对象
     * @param exceptionEnum 异常枚举
     */
    public static void isNull(@Nullable Object object, ServerFailure exceptionEnum) {
        if (object != null) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言对象不为空
     *
     * @param object        对象
     * @param exceptionEnum 异常枚举
     */
    public static void isNotNull(@Nullable Object object, ServerFailure exceptionEnum) {
        if (object == null) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言集合不为空
     *
     * @param array         对象集合
     * @param exceptionEnum 异常枚举
     */
    public static void notEmpty(@Nullable Object[] array, ServerFailure exceptionEnum) {
        if (ObjectUtils.isEmpty(array)) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言集合不为空
     *
     * @param collection    集合
     * @param exceptionEnum 异常枚举
     */
    public static void notEmpty(@Nullable Collection<?> collection, ServerFailure exceptionEnum) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ServerException(exceptionEnum);
        }
    }

    /**
     * 断言集合不为空
     *
     * @param map           集合
     * @param exceptionEnum 异常枚举
     */
    public static void notEmpty(@Nullable Map<?, ?> map, ServerFailure exceptionEnum) {
        if (CollectionUtils.isEmpty(map)) {
            throw new ServerException(exceptionEnum);
        }
    }
}
