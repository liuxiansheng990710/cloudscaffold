package com.example.mybatisplus.enhance.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.example.commons.core.model.BaseConvert;
import com.example.mybatisplus.enhance.service.BaseService;

/**
 * <p>
 * 业务ServiceImpl类 继承于MyBatis-plus 具体差异参考方法注释
 * <p>
 *
 * @author : 21
 * @see com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
 * @since : 2024/2/2 18:22
 */

public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseConvert> extends ServiceImpl<M, T> implements BaseService<T> {

    /**
     * LIMIT 1 STRING
     */
    private static final String LIMIT_1_STRING = "LIMIT 1";

    /**
     * LIMIT 1 QUERY WRAPPER
     */
    Wrapper<T> LIMIT_1_QUERY_WRAPPER = new QueryWrapper<T>().last(LIMIT_1_STRING);

    //-------------------------------------------------------------------重写的方法(纠结要不要都重写ing~~~)---------------------------------------------------------------------------//

    /**
     * @param queryWrapper
     * @param throwEx
     * @return
     * @see com.baomidou.mybatisplus.extension.service.impl.ServiceImpl#getOne(Wrapper, boolean)
     * 1. 主要是在查询最后LIMT 1(默认升序) 解决查询集合导致内存O(n)
     * 2. 空查询默认直接LIMT 1
     */
    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        if (isEmptyWrapper(queryWrapper) && throwEx) {
            return getBaseMapper().selectOne(LIMIT_1_QUERY_WRAPPER);
        }
        if (queryWrapper instanceof AbstractWrapper && throwEx) {
            AbstractWrapper<T, ?, ?> wrapper = (AbstractWrapper<T, ?, ?>) queryWrapper;
            wrapper.last(LIMIT_1_STRING);
            return getBaseMapper().selectOne(wrapper);
        }
        return SqlHelper.getObject(log, getBaseMapper().selectList(queryWrapper));
    }

    //--------------------------------------------------------------------------新增的方法---------------------------------------------------------------------------//

    /**
     * 根据Wrapper条件查询实体集合
     *
     * @param queryWrapper
     * @param mapper
     * @param <V>
     * @return
     */
    @Override
    public <V> List<V> listEntitys(Wrapper<T> queryWrapper, Function<? super T, V> mapper) {
        return list(queryWrapper).stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 无条件查询实体集合
     *
     * @param mapper
     * @param <V>
     * @return
     */
    @Override
    public <V> List<V> listEntitys(Function<? super T, V> mapper) {
        return list(Wrappers.emptyWrapper()).stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 根据Wrapper条件查询实体集合（返回传入实体类型）
     *
     * @param queryWrapper
     * @param clazz
     * @param <V>
     * @return
     */
    @Override
    public <V> List<V> listEntitys(Wrapper<T> queryWrapper, Class<V> clazz) {
        return listEntitys(queryWrapper, convert(clazz));
    }

    /**
     * 无条件查询实体集合 （返回传入实体类型）
     *
     * @param clazz
     * @param <V>
     * @return
     */
    @Override
    public <V> List<V> listEntitys(Class<V> clazz) {
        return listEntitys(convert(clazz));
    }

    /**
     * <p>
     * 查询一条记录
     * </P>
     * <p>
     * 主要差距：增加LIMIT 1 解决查询内存O(n)问题
     * </P>
     *
     * @param queryWrapper
     * @return
     */
    @Override
    public T getOne(AbstractWrapper<T, ?, ?> queryWrapper) {
        if (isEmptyWrapper(queryWrapper)) {
            return SqlHelper.getObject(log, list(LIMIT_1_QUERY_WRAPPER));
        }
        queryWrapper.last(LIMIT_1_STRING);
        return SqlHelper.getObject(log, list(queryWrapper));
    }

    /**
     * <p>
     * 查询一条记录的某个字段 (返回类型)
     * </P>
     * <p>
     * 主要差距：增加LIMIT 1 解决查询内存O(n)问题
     * </P>
     *
     * @param queryWrapper
     * @param mapper
     * @param <V>
     * @return
     */
    @Override
    public <V> V getObj(AbstractWrapper<T, ?, ?> queryWrapper, Function<? super Object, V> mapper) {
        if (isEmptyWrapper(queryWrapper)) {
            return SqlHelper.getObject(log, listObjs(LIMIT_1_QUERY_WRAPPER, mapper));
        }
        queryWrapper.last(LIMIT_1_STRING);
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    /**
     * 获取一条记录实体
     *
     * @param queryWrapper
     * @param mapper
     * @param <V>
     * @return
     */
    @Override
    public <V> V entity(AbstractWrapper<T, ?, ?> queryWrapper, Function<? super T, V> mapper) {
        if (isEmptyWrapper(queryWrapper)) {
            return SqlHelper.getObject(log, listEntitys(LIMIT_1_QUERY_WRAPPER, mapper));
        }
        queryWrapper.last(LIMIT_1_STRING);
        return SqlHelper.getObject(log, listEntitys(queryWrapper, mapper));
    }

    /**
     * 获取一条记录实体(返回传入实体类型)
     *
     * @param queryWrapper
     * @param clazz
     * @param <V>
     * @return
     */
    @Override
    public <V> V entity(AbstractWrapper<T, ?, ?> queryWrapper, Class<V> clazz) {
        if (isEmptyWrapper(queryWrapper)) {
            return SqlHelper.getObject(log, listEntitys(LIMIT_1_QUERY_WRAPPER, convert(clazz)));
        }
        queryWrapper.last(LIMIT_1_STRING);
        return SqlHelper.getObject(log, listEntitys(queryWrapper, convert(clazz)));
    }

    /**
     * 获取一条记录(Map对象)
     *
     * @param queryWrapper
     * @return
     */
    @Override
    public Map<String, Object> getMap(AbstractWrapper<T, ?, ?> queryWrapper) {
        if (isEmptyWrapper(queryWrapper)) {
            return SqlHelper.getObject(log, listMaps(LIMIT_1_QUERY_WRAPPER));
        }
        queryWrapper.last(LIMIT_1_STRING);
        return SqlHelper.getObject(log, listMaps(queryWrapper));
    }

    /**
     * 带条件翻页查询（返回传入实体类型）
     *
     * @param page
     * @param wrapper
     * @param clazz
     * @param <V>
     * @return
     */
    @Override
    public <V extends BaseConvert> Page<V> page(Page<T> page, Wrapper<T> wrapper, Class<V> clazz) {
        Page<T> oldPage = getBaseMapper().selectPage(page, wrapper);
        return (Page<V>) oldPage.convert(convert(clazz));
    }

    /**
     * 无条件翻页查询 (返回传入实体类型)
     *
     * @param page
     * @param clazz
     * @param <V>
     * @return
     */
    @Override
    public <V extends BaseConvert> Page<V> page(Page<T> page, Class<V> clazz) {
        Page<T> oldPage = getBaseMapper().selectPage(page, Wrappers.emptyWrapper());
        return (Page<V>) oldPage.convert(convert(clazz));
    }

    /**
     * 判断是否未空的Wrapper
     *
     * @param wrapper
     * @return
     */
    protected boolean isEmptyWrapper(Wrapper<T> wrapper) {
        return Objects.equals(Wrappers.emptyWrapper(), wrapper) || Objects.isNull(wrapper);
    }
}
