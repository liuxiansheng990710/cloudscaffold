package com.example.provider.quartz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.model.quartz.entity.mysql.Job;
import com.example.model.quartz.mapper.JobMapper;
import com.example.provider.quartz.common.QuartzManage;

import io.seata.spring.annotation.GlobalTransactional;

/**
 * <p>
 * 定时任务 服务实现类
 * <p>
 *
 * @author : 21
 * @since : 2023/10/7 18:22
 */
@Service
public class JobService extends ServiceImpl<JobMapper, Job> implements IService<Job> {

    @Autowired
    private QuartzManage quartzManage;

    @Transactional(rollbackFor = Exception.class)
    public Job create(Job resources) {
        save(resources);
        quartzManage.addJob(resources);
        return resources;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Job resources) {
        updateById(resources);
        quartzManage.updateJobCron(resources);
    }

    /**
     * 更改定时任务状态
     *
     * @param quartzJob
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePaused(Job quartzJob) {
        if (quartzJob.getPaused()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setPaused(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setPaused(true);
        }
        update(quartzJob);
    }

    /**
     * 立即执行定时任务
     *
     * @param quartzJob
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(Job quartzJob) {
        quartzManage.runAJobNow(quartzJob);
    }

    @Transactional(rollbackFor = Exception.class)
    @GlobalTransactional(rollbackFor = Exception.class)
    public void delete(Job quartzJob) {
        quartzManage.deleteJob(quartzJob);
        removeById(quartzJob);
    }

    /**
     * del 批量删除
     *
     * @param jobs 多个定时任务id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteList(List<Job> jobs) {
        quartzManage.deleteJobs(jobs);
        removeBatchByIds(jobs);
    }
}
