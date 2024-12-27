package com.example.cloud.sentinel.parser;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.example.cloud.sentinel.config.properties.AuthOriginConfiguration;

/**
 * <p>
 * Sentinel鉴权规则-自定义鉴权参数
 * <p>
 *
 * @author : 21
 * @since : 2023/8/17 10:52
 */

@Component
public class AuthOriginParser implements RequestOriginParser {

    @Autowired
    private AuthOriginConfiguration authOriginConfiguration;

    @Override
    public String parseOrigin(HttpServletRequest request) {
        return request.getHeader(authOriginConfiguration.getOrigin());
    }
}
