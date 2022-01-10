package me.youm.reactor.common.myassert;

import lombok.NoArgsConstructor;
import me.youm.reactor.common.enums.EnumInterface;
import me.youm.reactor.common.exception.BusinessException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author xiyu
 **/
@NoArgsConstructor
public class $ extends Assert {

    public static void state(boolean expression, EnumInterface exceptionEnum) {
        if (!expression) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void isTrue(boolean expression, EnumInterface exceptionEnum) {
        if (!expression) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void isNotTrue(boolean expression, EnumInterface exceptionEnum) {
        if (expression) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void hasText(@Nullable String str, EnumInterface exceptionEnum) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void hasLength(@Nullable String str, EnumInterface exceptionEnum) {
        if (!StringUtils.hasLength(str)) {
            throw new BusinessException(exceptionEnum);
        }
    }


    public static void hasText(@Nullable CharSequence str, EnumInterface exceptionEnum) {
        if (!StringUtils.hasText(str)) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void hasLength(@Nullable CharSequence str, EnumInterface exceptionEnum) {
        if (!StringUtils.hasLength(str)) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void isNull(@Nullable Object object, EnumInterface exceptionEnum) {
        if (object != null) {
            throw new BusinessException(exceptionEnum);
        }
    }


    public static void isNotNull(@Nullable Object object, EnumInterface exceptionEnum) {
        if (object == null) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void notEmpty(@Nullable Object[] array, EnumInterface exceptionEnum) {
        if (ObjectUtils.isEmpty(array)) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void notEmpty(@Nullable Collection<?> collection, EnumInterface exceptionEnum) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(exceptionEnum);
        }
    }

    public static void notEmpty(@Nullable Map<?, ?> map, EnumInterface exceptionEnum) {
        if (CollectionUtils.isEmpty(map)) {
            throw new BusinessException(exceptionEnum);
        }
    }
}
