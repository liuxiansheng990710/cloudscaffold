package com.example.commons.web.servlet.interceptors;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.commons.core.cons.RequestCons;
import com.example.commons.core.utils.ApplicationUtils;

/**
 * <p>
 * 请求拦截器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/14 16:52
 */

public class ApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(RequestCons.REQ_BEGIN_TIME, System.currentTimeMillis());
        //HandlerMethod表示处理请求的方法,它提供了对方法的详细信息的访问,包括方法名、方法参数、返回类型等
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            setPathAttribute(request, handlerMethod);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 设置请求路径等参数
     *
     * @param request
     * @param handlerMethod
     */
    private void setPathAttribute(HttpServletRequest request, HandlerMethod handlerMethod) {
        String mapping = "";
        //获取`类上`的请求路径
        RequestMapping requestMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
        if (Objects.nonNull(requestMapping)) {
            mapping = requestMapping.value()[0];
        }
        String methodMapping = "";
        //获取`方法上`的请求路径
        RequestMapping methodAnnotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (Objects.nonNull(methodAnnotation)) {
            String[] methodMappings = methodAnnotation.value();
            methodMapping = ArrayUtils.isEmpty(methodMappings) ? methodMapping : methodMappings[0];
        }
        String requsetMapping = mapping + methodMapping;
        //获取服务路径
        String webServerPath = ApplicationUtils.getWebServers().path();
        //获取请求上下文相对路径,为解决@PathVariable参数在URI上情况
        String reqUrl = request.getRequestURI().substring(request.getContextPath().length());
        request.setAttribute(RequestCons.REQ_URL, webServerPath + reqUrl);
        request.setAttribute(RequestCons.REQ_MAPPING, webServerPath + requsetMapping);
        request.setAttribute(RequestCons.REQ_METHOD, request.getMethod());
    }

}
