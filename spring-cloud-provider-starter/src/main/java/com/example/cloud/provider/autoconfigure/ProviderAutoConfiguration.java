package com.example.cloud.provider.autoconfigure;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.example.cloud.provider.undertow.UndertowServerFactoryCustomizer;
import com.example.commons.core.utils.JacksonUtils;
import com.example.commons.web.servlet.interceptors.ApiInterceptor;
import com.example.commons.web.servlet.resolver.ServerHandlerExceptionResolver;

import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 微服务自动配置类
 * Spring Boot Web 应用中启用并配置 Spring MVC 功能
 * <p>
 *
 * @author : 21
 * @since : 2023/9/22 16:25
 */

//应用程序是一个 Web 应用程序时,才会应用被注解的配置
@ConditionalOnWebApplication
//启用 Spring MVC 框架的功能 如控制器、请求映射、数据绑定、视图解析等
@EnableWebMvc
@Configuration
@Slf4j
public class ProviderAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnClass(Undertow.class)
    public UndertowServerFactoryCustomizer undertowServerFactoryCustomizer() {
        return new UndertowServerFactoryCustomizer();
    }

    /**
     * 开启spring请求监听 监听 Servlet 请求的生命周期（Servlet 在服务器端运行，用于接收和响应来自客户端的请求）
     * ConditionalOnMissingBean 确保在RequestContextListener不存在时，注册该Bean，防止重复注册
     * 使用RequestContextHolder来获取请求上下文 {@link com.example.commons.core.utils.ApplicationUtils#getRequest}
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    /**
     * mvc下 自定义序列化与反序列化
     *
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(JacksonUtils.wrapperObjectMapper());
        converters.forEach(stringMessageConvert());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
    }

    /**
     * 使用自定义全局异常类替换默认全局异常处理
     *
     * @param resolvers
     */
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.removeIf(DefaultHandlerExceptionResolver.class::isInstance);
        resolvers.add(new ServerHandlerExceptionResolver());
    }

    /**
     * 为兼容测试，对直接返回字符串做响应处理
     *
     * @return
     */
    private Consumer<HttpMessageConverter<?>> stringMessageConvert() {
        return converter -> {
            if (converter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter stringHttpMessageConverter = (StringHttpMessageConverter) converter;
                stringHttpMessageConverter.setSupportedMediaTypes(JacksonUtils.getMediaTypes());
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        };
    }

}
