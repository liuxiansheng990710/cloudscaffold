package com.example.cloud.openfeign.decoder;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.HttpStatus;

import cn.hutool.core.lang.ParameterizedTypeImpl;
import feign.FeignException;
import feign.Response;

/**
 * <p>
 * Feign 解码器 结合Spring
 * <p>
 *
 * @author : 21
 * @since : 2023/7/17 15:20
 */

public class FeignResponseSpringDecoder extends SpringDecoder {

    public FeignResponseSpringDecoder(ObjectFactory<HttpMessageConverters> messageConverters, ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        super(messageConverters, customizers);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(new Type[]{type}, null, ApiResponses.class);
        Object object = super.decode(response, parameterizedType);
        if (object instanceof ApiResponses) {
            ApiResponses<?> responses = (ApiResponses<?>) object;
            if (responses.getStatus() >= HttpStatus.OK.value() && responses.getStatus() <= HttpStatus.MULTIPLE_CHOICES.value()) {
                return responses.getResult();
            }
        }
        return object;
    }
}
