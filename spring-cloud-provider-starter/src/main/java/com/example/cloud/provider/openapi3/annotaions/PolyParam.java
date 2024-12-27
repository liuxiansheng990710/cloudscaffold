package com.example.cloud.provider.openapi3.annotaions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PolyParam {

    /**
     * 多态请求参数子类实体,用于body中参数
     */
    Class<?> paramType() default Void.class;

    /**
     * 多态返回参数子类实体
     */
    Class<?> resultType() default Void.class;

    /**
     * 标签（在Swagger文档接口标签后拼接该字段，该字段会以括号包围）
     */
    String name() default "";

    /**
     * 该值会与参数字段property()拼接在路径参数中,若该参数为空则按数字递增
     * <p>例如请求路径（...?type=1）</p>
     */
    String type() default "";

}
