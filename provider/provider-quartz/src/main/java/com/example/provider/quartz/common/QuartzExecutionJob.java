package com.example.provider.quartz.common;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.utils.ApplicationUtils;
import com.example.commons.core.utils.ThreadUtils;
import com.example.model.quartz.entity.mysql.Job;
import com.example.model.quartz.entity.mysql.JobLog;
import com.example.provider.quartz.cons.QuartzCons;
import com.example.provider.quartz.services.JobLogService;
import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 定时任务执行Bean
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 14:32
 */

@Async
@Slf4j
public class QuartzExecutionJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        Job quartzJob = (Job) context.getMergedJobDataMap().get(QuartzCons.JOB_KEY_PREFIX);
        JSONObject params = quartzJob.getParams();
        //定时任务日志
        JobLog jobLog = new JobLog();
        jobLog.setTaskName(quartzJob.getTaskName());
        jobLog.setClassName(quartzJob.getClassName());
        jobLog.setParams(params);
        jobLog.setCron(quartzJob.getCron());
        jobLog.setCreateTime(new Date());
        jobLog.setJobId(quartzJob.getId());
        long startTime = System.currentTimeMillis();
        try {
            //执行任务
            QuartzRunnable task = new QuartzRunnable(quartzJob.getClassName(), quartzJob.getId(), params);
            ThreadUtils.execute(task);
            String runTime = System.currentTimeMillis() - startTime + "ms";
            jobLog.setRunTime(runTime);
            jobLog.setSuccessed(true);
        } catch (Exception e) {
            log.error("Failed to execution scheduled task, jobId {}, name {}, params {}, exception {}", quartzJob.getId(), quartzJob.getTaskName(), params, Throwables.getStackTraceAsString(e));
            jobLog.setRunTime(System.currentTimeMillis() - startTime + "ms");
            jobLog.setSuccessed(false);
            jobLog.setException(Throwables.getStackTraceAsString(e));
        } finally {
            JobLogService jobLogService = ApplicationUtils.getBean(JobLogService.class);
            jobLogService.save(jobLog);
        }

    }
}
