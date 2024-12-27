package com.example.commons.core.jackson.serializer;

import java.io.IOException;

import com.example.commons.core.jackson.enums.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <p>
 * IEnum接口JSON序列化实现（jackson）
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 17:45
 */

public class IEnumSerializer extends JsonSerializer<IEnum> {

    @Override
    public void serialize(IEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getValue());
    }

}
