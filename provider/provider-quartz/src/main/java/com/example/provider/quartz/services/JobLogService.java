package com.example.provider.quartz.services;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.model.quartz.entity.mysql.JobLog;
import com.example.model.quartz.mapper.JobLogMapper;

/**
 * <p>
 * 定时任务日志 服务实现类
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 17:45
 */
@Service
public class JobLogService extends ServiceImpl<JobLogMapper, JobLog> implements IService<JobLog> {

}
