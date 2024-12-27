package com.example.commons.core.modelmapper.json;

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import com.alibaba.fastjson.JSONArray;

/**
 * <p>
 * Converts  {@link JSONArray} to {@link JSONArray}
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 15:08
 */
public class JSONArrayToJSONArrayConverter implements ConditionalConverter<JSONArray, JSONArray> {

    @Override
    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return JSONArray.class.isAssignableFrom(sourceType)
                && JSONArray.class.isAssignableFrom(destinationType)
                ? MatchResult.FULL : MatchResult.NONE;
    }

    @Override
    public JSONArray convert(MappingContext<JSONArray, JSONArray> mappingContext) {
        if (mappingContext.getSource() == null) {
            return null;

        } else if (mappingContext.getSourceType().equals(mappingContext.getDestinationType())) {
            JSONArray source = mappingContext.getSource();
            return (JSONArray) source.clone();
        } else {
            throw new Errors().addMessage("Unsupported mapping types[%s->%s]",
                    mappingContext.getSourceType().getName(), mappingContext.getDestinationType())
                    .toMappingException();
        }

    }
}
