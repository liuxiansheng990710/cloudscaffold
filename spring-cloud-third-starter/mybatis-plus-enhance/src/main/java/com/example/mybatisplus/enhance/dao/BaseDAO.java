package com.example.mybatisplus.enhance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.commons.core.model.BaseConvert;
import com.example.mybatisplus.enhance.service.impl.BaseServiceImpl;

/**
 * <p>
 * 业务DAO类 继承于MyBatis-plus
 * <p>
 *
 * @author : 21
 * @see com.example.mybatisplus.enhance.service.impl.BaseServiceImpl
 * @since : 2024/2/4 9:25
 */

public class BaseDAO<M extends BaseMapper<T>, T extends BaseConvert> extends BaseServiceImpl<M, T> {

}
