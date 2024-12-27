package com.example.model.base.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.base.entity.mysql.App;

@Mapper
public interface AppMapper extends BaseMapper<App> {

}
