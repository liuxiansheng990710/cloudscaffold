package com.example.checke.support;

/**
 * <p>
 * 检查执行器（不同数据库类型可实现该接口）
 * <p>
 *
 * @author : 21
 * @since : 2024/3/13 14:47
 */

public interface CheckHandler<T> {

    void init();

    T check();

}
