package com.example.commons.core.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.example.commons.core.exceptions.ApiException;
import com.example.commons.core.model.Errors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Api断言
 * <p>
 *
 * @author : 21
 * @since : 2023/12/19 9:50
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiAssert {

    /**
     * 判断对象是否相等
     *
     * @param errors
     * @param obj1
     * @param obj2
     */
    public static void equals(Errors errors, Object obj1, Object obj2) {
        if (!Objects.equals(obj1, obj2)) {
            failure(errors);
        }
    }

    /**
     * 判断是否为true
     *
     * @param errors
     * @param condition
     */
    public static void isTrue(Errors errors, boolean condition) {
        if (!condition) {
            failure(errors);
        }
    }

    /**
     * 判断是否为false
     *
     * @param errors
     * @param condition
     */
    public static void isFalse(Errors errors, boolean condition) {
        if (condition) {
            failure(errors);
        }
    }

    /**
     * 判断是空
     *
     * @param errors
     * @param conditions
     */
    public static void isNull(Errors errors, Object... conditions) {
        if (ObjectUtil.isAllNotEmpty(conditions)) {
            failure(errors);
        }
    }

    /**
     * 判断不为空
     *
     * @param errors
     * @param conditions
     */
    public static void notNull(Errors errors, Object... conditions) {
        if (ObjectUtil.isAllEmpty(conditions)) {
            failure(errors);
        }
    }

    /**
     * 判断不为空
     *
     * @param errors
     * @param array
     */
    public static void notEmpty(Errors errors, Object[] array) {
        if (ArrayUtil.isEmpty(array)) {
            failure(errors);
        }
    }

    /**
     * 判断每个元素是否为kong
     *
     * @param errors
     * @param array
     */
    public static void noNullElements(Errors errors, Object... array) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    failure(errors);
                }
            }
        }
    }

    /**
     * 判断集合不为空
     *
     * @param errors
     * @param collection
     */
    public static void notEmpty(Errors errors, Collection<?> collection) {
        if (CollUtil.isEmpty(collection)) {
            failure(errors);
        }
    }

    /**
     * 判断集合为空
     *
     * @param errors
     * @param collection
     */
    public static void isEmpty(Errors errors, Collection<?> collection) {
        if (CollUtil.isNotEmpty(collection)) {
            failure(errors);
        }
    }

    /**
     * 判断Map不为空
     *
     * @param errors
     * @param map
     */
    public static void notEmpty(Errors errors, Map<?, ?> map) {
        if (MapUtil.isEmpty(map)) {
            failure(errors);
        }
    }

    /**
     * 判断Map为空
     *
     * @param errors
     * @param map
     */
    public static void isEmpty(Errors errors, Map<?, ?> map) {
        if (MapUtil.isNotEmpty(map)) {
            failure(errors);
        }
    }

    /**
     * 判断字符串不为空
     *
     * @param errors
     * @param str
     */
    public static void notBlank(Errors errors, CharSequence str) {
        if (StringUtils.isBlank(str)) {
            failure(errors);
        }
    }

    /**
     * 判断字符串为空
     *
     * @param errors
     * @param str
     */
    public static void isBlank(Errors errors, CharSequence str) {
        if (StringUtils.isNotBlank(str)) {
            failure(errors);
        }
    }

    /**
     * 失败
     *
     * @param errors 异常错误码
     */
    public static void failure(Errors errors) {
        throw new ApiException(errors);
    }

    /**
     * <p>
     * 失败结果
     * </p>
     *
     * @param errors    异常错误码
     * @param exception 异常
     */
    public static void failure(Errors errors, Exception exception) {
        throw new ApiException(errors, exception);
    }
}

