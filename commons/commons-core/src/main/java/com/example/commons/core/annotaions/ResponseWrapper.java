package com.example.commons.core.annotaions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpStatus;

/**
 * <p>
 *  返回体包装（默认所有返回都进行包装）
 *  添加该注解后默认不进行包装
 * <p>
 *
 * @author : 21
 * @since : 2023/9/23 21:26
 */


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWrapper {

    /**
     * 是否对返回体进行包装
     */
    boolean wrapper() default false;

    HttpStatus status() default HttpStatus.OK;

}
