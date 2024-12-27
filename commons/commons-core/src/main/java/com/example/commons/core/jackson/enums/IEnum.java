package com.example.commons.core.jackson.enums;

import java.util.Objects;

import com.example.commons.core.jackson.deserializer.IEnumDeserializer;
import com.example.commons.core.jackson.serializer.IEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = IEnumSerializer.class)
@JsonDeserialize(using = IEnumDeserializer.class)
public interface IEnum {

    /**
     * 获取枚举值
     *
     * @return
     */
    int getValue();

    /**
     * 获取枚举对象
     *
     * @param clazz 实现IEnum的类
     * @param value 枚举值
     * @param <T>
     * @return
     */
    static <T extends IEnum> T getInstance(Class<T> clazz, int value) {
        //如果clazz是枚举类，该方法会获取到所有枚举常量的数组  否则返回null
        T[] enumConstants = clazz.getEnumConstants();
        if (Objects.isNull(enumConstants)) {
            return null;
        }
        for (T t : enumConstants) {
            if (t.getValue() == value) {
                return t;
            }
        }
        return null;
    }

}
