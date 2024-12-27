package com.example.commons.core.jackson.deserializer;

import java.io.IOException;

import com.example.commons.core.jackson.enums.IEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

/**
 * <p>
 * IEnum接口JSON反序列化实现（jackson）
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 17:48
 */

public class IEnumDeserializer extends JsonDeserializer<IEnum> implements ContextualDeserializer {

    /**
     * 枚举类Class
     */
    private Class<? extends IEnum> rawClass;

    public IEnumDeserializer(Class<? extends IEnum> rawClass) {
        this.rawClass = rawClass;
    }

    public IEnumDeserializer() {
    }

    @Override
    public IEnum deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        return IEnum.getInstance(rawClass, jsonParser.readValueAs(Integer.class));
    }

    /**
     * ContextualDeserializer用于处理那些在反序列化时需要获取一些上下文信息的情况（目标类型的泛型参数，或者与目标类型相关的自定义注解）
     * @param ctxt
     * @param property
     * @return
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        //获取目标的类型（实现IEnum的类）
        Class<? extends IEnum> targetClass = (Class<? extends IEnum>) property.getType().getRawClass();
        return new IEnumDeserializer(targetClass);
    }
}
