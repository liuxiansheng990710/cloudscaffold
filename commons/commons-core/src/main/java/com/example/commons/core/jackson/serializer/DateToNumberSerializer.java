package com.example.commons.core.jackson.serializer;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <p>
 * jackson Date类型序列化为Number（时间戳）
 * <p>
 *
 * @author : 21
 * @since : 2023/9/22 17:58
 */

public class DateToNumberSerializer extends JsonSerializer<Date> {

    public final static DateToNumberSerializer instance = new DateToNumberSerializer();

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.getTime());
    }
}
