package com.example.model.quartz.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.quartz.entity.mysql.JobLog;

/**
 * <p>
 * 定时任务日志 Mapper 接口
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 17:46
 */
@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {

}
