package com.example.model.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.auth.entity.mysql.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
