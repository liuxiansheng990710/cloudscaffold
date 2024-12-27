package com.example.commons.core.jackson.deserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import com.alibaba.fastjson.util.TypeUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

/**
 * <p>
 * 字符串时间戳反序列化时，反序列化成Date类型
 * <p>
 *
 * @author : 21
 * @since : 2023/9/22 18:07
 */

@JacksonStdImpl
public class DateStrDeserializer extends DateDeserializers.DateDeserializer {

    public DateStrDeserializer() {
        super();
    }

    public DateStrDeserializer(DateDeserializers.DateDeserializer base, DateFormat df, String formatString) {
        super(base, df, formatString);
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return TypeUtils.castToDate(p.getText().trim());
        }
        return super._parseDate(p, ctxt);
    }

}
