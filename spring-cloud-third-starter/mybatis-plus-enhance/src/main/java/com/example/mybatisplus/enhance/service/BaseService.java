package com.example.mybatisplus.enhance.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.commons.core.model.BaseConvert;

/**
 * <p>
 * 业务Service类 继承于MyBatis-plus 具体差异参考方法注释
 * <p>
 *
 * @author : 21
 * @see com.baomidou.mybatisplus.extension.service.IService
 * @since : 2024/2/2 18:19
 */

public interface BaseService<T extends BaseConvert> extends IService<T> {

    <V> List<V> listEntitys(Wrapper<T> queryWrapper, Function<? super T, V> mapper);

    <V> List<V> listEntitys(Function<? super T, V> mapper);

    <V> List<V> listEntitys(Wrapper<T> queryWrapper, Class<V> clazz);

    <V> List<V> listEntitys(Class<V> clazz);

    T getOne(AbstractWrapper<T, ?, ?> queryWrapper);

    <V> V getObj(AbstractWrapper<T, ?, ?> queryWrapper, Function<? super Object, V> mapper);

    <V> V entity(AbstractWrapper<T, ?, ?> queryWrapper, Function<? super T, V> mapper);

    <V> V entity(AbstractWrapper<T, ?, ?> queryWrapper, Class<V> clazz);

    Map<String, Object> getMap(AbstractWrapper<T, ?, ?> queryWrapper);

    <V extends BaseConvert> Page<V> page(Page<T> page, Wrapper<T> wrapper, Class<V> clazz);

    <V extends BaseConvert> Page<V> page(Page<T> page, Class<V> clazz);

    /**
     * 转换为clzz传入类型
     *
     * @param clazz
     * @param <K>
     * @param <E>
     * @return
     */
    default <K extends BaseConvert, E> Function<T, E> convert(Class<E> clazz) {
        return e -> e.convert(clazz);
    }
}
