package com.example.cloud.provider.openapi3.annotaions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PolyParams {
    /**
     * 多态请求参数父类实体,用于body中参数
     */
    Class<?> paramType() default Void.class;

    /**
     * 多态返回参数父类实体
     */
    Class<?> resultType() default Void.class;

    /**
     * 多态情况下字段名，与值name结合区分多态情况下不同接口;
     * 该属性会与值name()拼接再Url属性中
     * <p>
     * 注：该参数主要区分多态情况下的请求路径，若接收参数不需要该值应在Swagger文档中表明，或与前端沟通
     * </p>
     */
    String property() default "type";

    PolyParam[] value();
}
