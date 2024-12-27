package com.example.spring.boot.redisson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.spring.boot.redisson.enums.LockType;
import com.example.spring.boot.redisson.excutor.strategy.LockTimeoutStrategy;
import com.example.spring.boot.redisson.excutor.strategy.ReleaseTimeoutStrategy;

/**
 * <p>
 * 分布式锁注解
 * <p>
 *
 * @author : 21
 * @since : 2023/10/26 9:33
 */

@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Klock {

    /**
     * 锁的名称
     */
    String name() default "";

    /**
     * 锁类型，默认可重入锁
     */
    LockType lockType() default LockType.REENTRANT;

    /**
     * 尝试加锁，最多等待时间
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 上锁以后xxx秒自动解锁
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 自定义业务key（SPEL表达式）
     */
    String[] keys() default {};

    /**
     * 重试获取锁最大间隔（毫秒）
     */
    long retryMaxTime() default 6200L;

    /**
     * 加锁超时的处理策略（默认：继续执行业务逻辑，不做任何处理）
     */
    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.NO_OPERATION;

    /**
     * 自定义加锁超时的处理策略（需要与加锁方法在同一类中）
     * <P>参数为处理策略的方法名</P>
     */
    String customLockTimeoutStrategy() default "";

    /**
     * 释放锁时超时的处理策略（默认：继续执行业务逻辑，不做任何处理）
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.NO_OPERATION;

    /**
     * 自定义释放锁时已超时的处理策略
     * <P>参数为处理策略的方法名</P>
     */
    String customReleaseTimeoutStrategy() default "";

    /**
     * 自定义错误信息
     */
    String error() default "";

}
