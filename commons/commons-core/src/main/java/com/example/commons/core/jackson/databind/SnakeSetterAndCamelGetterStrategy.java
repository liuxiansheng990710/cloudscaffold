package com.example.commons.core.jackson.databind;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * <p>
 * 蛇形参数绑定为驼峰参数（一般用于第三方响应结果转换实体）
 * 用法 : 实体使用jackson序列化的时候，使用jsonNaming配合使用 直接加到实体类上
 *
 * @author : 21
 * @JsonNaming(SnakeSetterAndCamelGetterStrategy.class) <p>
 * @since : 2023/9/23 21:13
 */

public class SnakeSetterAndCamelGetterStrategy extends PropertyNamingStrategy {

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return PropertyNamingStrategies.SNAKE_CASE.nameForSetterMethod(config, method, defaultName);
    }
}
