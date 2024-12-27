package com.example.cloud.provider.autoconfigure;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.commons.core.annotaions.ResponseWrapper;
import com.example.commons.core.cons.RequestCons;
import com.example.commons.core.model.ApiResponses;
import com.example.commons.web.utils.ResponseUtils;

/**
 * <p>
 * 微服务接口统一返回处理
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 10:44
 */

@RestControllerAdvice("com.example.provider")
public class CommonsControllerAdvice implements RequestBodyAdvice, ResponseBodyAdvice<Object> {

    //-------------------------------------------------------------响应---------这里只有成功才会调用----Feign成功时也会进来-------------------------------------------------------//
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        //必须有两个参数中的一个 返回为true时,运行beforeBodyWrite方法
        RestController restController = returnType.getDeclaringClass().getAnnotation(RestController.class);
        ResponseBody responseBody = returnType.getMethodAnnotation(ResponseBody.class);
        return (Objects.nonNull(restController) || Objects.nonNull(responseBody));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Object object = body;
        boolean wrapper = true;
        ResponseWrapper annotation = returnType.getMethodAnnotation(ResponseWrapper.class);
        if (Objects.nonNull(annotation)) {
            wrapper = annotation.wrapper();
        }
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        HttpStatus status = HttpStatus.OK;
        if (Objects.nonNull(annotation)) {
            status = annotation.status();
            servletResponse.setStatus(status.value());
        }
        if (wrapper) {
            object = body instanceof ApiResponses ? body : ResponseUtils.<Object>success(servletResponse, status, body);
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        ResponseUtils.responseAndPrint(servletRequest, servletResponse, object);
        return object;
    }

    //-------------------------------------------------------请求----------- 这里只对@RequestBody注解的参数起作用-----------------------------------------------------------//

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        //不校验,直接走到beforeBodyRead方法
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        //读取请求体之前,暂不修改请求
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        //将请求体存储使用
        Optional<HttpServletRequest> optionalRequest = Optional.of(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        optionalRequest.ifPresent(request -> request.setAttribute(RequestCons.REQ_BODY, body));
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        //请求体为空时,直接返回
        return body;
    }
}
