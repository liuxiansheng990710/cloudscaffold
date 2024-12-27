package com.example.model.quartz.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.quartz.entity.mysql.Job;

/**
 * <p>
 * 定时任务 Mapper 接口
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 18:03
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

}
