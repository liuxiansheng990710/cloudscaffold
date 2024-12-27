package com.example.commons.core.utils;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.objenesis.instantiator.util.ClassUtils;

import com.example.commons.core.modelmapper.jdk8.Jdk8Module;
import com.example.commons.core.modelmapper.json.FastJsonModule;
import com.example.commons.core.modelmapper.jsr310.Jsr310Module;
import com.example.commons.core.modelmapper.jsr310.Jsr310ModuleConfig;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 实体转换工具类
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 15:28
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanConvert {

    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        Jsr310ModuleConfig config = Jsr310ModuleConfig.builder()
                .dateTimePattern("yyyy-MM-dd HH:mm:ss") // default is yyyy-MM-dd HH:mm:ss
                .datePattern("yyyy-MM-dd") // default is yyyy-MM-dd
                .zoneId(ZoneOffset.UTC) // default is ZoneId.systemDefault()
                .build();
        modelMapper.registerModule(new Jsr310Module(config))
                .registerModule(new Jdk8Module())
                .registerModule(new FastJsonModule());
        //源对象和目标对象的类型必须完全一致，包括泛型参数
        modelMapper.getConfiguration().setFullTypeMatchingRequired(true);
        //源对象和目标对象的属性名必须完全一致，才能进行自动映射
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * 获取 modelMapper
     *
     * @return
     */
    public static ModelMapper getModelMapper() {
        return modelMapper;
    }

    /**
     * 单个对象转换
     *
     * @param source      源对象
     * @param targetClass 目标对象
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (Objects.isNull(source)) {
            return null;
        }
        return getModelMapper().map(source, targetClass);
    }

    /**
     * 列表转换
     *
     * @param list        源列表数据
     * @param targetClass 目标对象类型
     * @return 目标类型集合
     */
    public static <T> List<T> listConvert(List<?> list, Class<T> targetClass) {
        List<T> resultList = Collections.emptyList();
        if (CollUtil.isEmpty(list)) {
            return resultList;
        }
        resultList = new ArrayList<>(list.size());
        Iterator<?> iterator = list.iterator();
        //循环调用转换单个对象
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (Objects.nonNull(obj)) {
                T t = convert(obj, targetClass);
                resultList.add(t);
            }
        }
        return resultList;
    }

    /**
     * map转为bean
     *
     * @param map       源数据map
     * @param beanClass 目标对象类型
     * @return map(key, value) -> bean(属性,值)
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        T entity = ClassUtils.newInstance(beanClass);
        BeanMap beanMap = BeanMap.create(entity);
        beanMap.putAll(map);
        return entity;
    }

    /**
     * List<Map>转为List<bean>
     *
     * @param mapList   源数据map集合
     * @param beanClass 目标对象类型
     * @return List<map ( key, value )> -> List<bean(属性,值)>
     */
    public static <T> List<T> mapToBean(List<Map<String, Object>> mapList, Class<T> beanClass) {
        List<T> list = Collections.emptyList();
        if (CollUtil.isNotEmpty(mapList)) {
            list = new ArrayList<>(mapList.size());
            Map<String, Object> map;
            T bean;
            for (Map<String, Object> map1 : mapList) {
                map = map1;
                bean = mapToBean(map, beanClass);
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * Bean转换为Map
     *
     * @param bean 源数据对象
     * @return bean(属性, 值) -> map(key, value)
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Collections.emptyMap();
        if (null != bean) {
            BeanMap beanMap = BeanMap.create(bean);
            map = new HashMap<>(beanMap.keySet().size());
            for (Object key : beanMap.keySet()) {
                map.put(String.valueOf(key), beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * List<E>转换为List<Map<String, Object>>
     *
     * @param objList 源数据集合
     * @return List<bean ( 属性, 值 )> -> List<map ( key, value )>
     */
    public static <T> List<Map<String, Object>> beansToMap(List<T> objList) {
        List<Map<String, Object>> list = Collections.emptyList();
        if (CollUtil.isNotEmpty(objList)) {
            list = new ArrayList<>(objList.size());
            Map<String, Object> map;
            T bean;
            for (T anObjList : objList) {
                bean = anObjList;
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

}
