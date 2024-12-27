package com.example.spring.boot.cache.genertor;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.SimpleKeyGenerator;

public class ToStringKeyGenerator extends SimpleKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        Object generate = super.generate(target, method, params);
        return generate.toString();
    }
}
