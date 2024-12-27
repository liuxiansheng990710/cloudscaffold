package com.example.cloud.openfeign.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestHeaderParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestParamParameterProcessor;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;

import com.example.cloud.openfeign.converter.Date2StringConverter;
import com.example.cloud.openfeign.decoder.FeignResponseSpringDecoder;
import com.example.cloud.openfeign.decoder.error.FeignErrorDecoder;

import feign.Contract;
import feign.Feign;
import feign.Request;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;

/**
 * <p>
 * Openfeign配置类
 * <p>
 *
 * @author : 21
 * @since : 2023/7/17 15:25
 */

@ConditionalOnClass({Feign.class})
@Configuration
public class OpenfeignAutoConfiguration {

    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters, ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        return new OptionalDecoder(new ResponseEntityDecoder(new FeignResponseSpringDecoder(messageConverters, customizers)));
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(2L, TimeUnit.SECONDS, 5L, TimeUnit.SECONDS, Boolean.TRUE);
    }

    @Bean
    public Contract feignContract() {
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(new Date2StringConverter());
        List<AnnotatedParameterProcessor> annotatedArgumentResolvers = new ArrayList<>();
        annotatedArgumentResolvers.add(new PathVariableParameterProcessor());
        annotatedArgumentResolvers.add(new RequestParamParameterProcessor());
        annotatedArgumentResolvers.add(new RequestHeaderParameterProcessor());
        return new SpringMvcContract(annotatedArgumentResolvers, defaultConversionService);
    }

}
