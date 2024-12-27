package com.example.provider.quartz.controller.sys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.commons.web.controller.SuperController;
import com.example.model.quartz.entity.mysql.Job;
import com.example.provider.quartz.services.JobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "定时任务相关接口")
@RestController
@RequestMapping("/sys")
public class SysJobRestController extends SuperController {

    @Autowired
    private JobService jobService;

    @Operation(summary = "新增定时任务", description = "新增定时任务")
    @PostMapping("/jobs")
    public void create(@RequestBody Job convent) {
//        checkUnique(convent);
        jobService.create(convent);
    }

    @Operation(summary = "修改定时任务", description = "使用场景:后台-根据定时任务ID修改定时任务")
    @Parameter(name = "id", description = "定时任务的ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "Long"))
    @PutMapping("/jobs/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody Job convent) {
        convent.setId(id);
//        checkUnique(convent);
        jobService.update(convent);
    }

    @Operation(summary = "更改定时任务状态", description = "更改定时任务状态")
    @Parameter(name = "id", description = "定时任务的ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "Long"))
    @PutMapping(value = "/jobs/{id}/paused")
    public void updatePaused(@PathVariable("id") Long id) {
        Job job = jobService.getById(id);
        jobService.updatePaused(job);
    }

    @Operation(summary = "批量删除定时任务", description = "批量删除定时任务")
    @Parameter(name = "ids", description = "定时任务的ID集合(英文逗号隔开)", required = true, in = ParameterIn.PATH, schema = @Schema(type = "Long"))
    @DeleteMapping("/jobs/{ids}")
    public void delete(@PathVariable("ids") List<Long> ids) {
        List<Job> list = new ArrayList<>();
        for (Long id : ids) {
            Job job = jobService.getById(id);
            list.add(job);
        }
        jobService.deleteList(list);
    }

    @Operation(summary = "执行定时任务", description = "执行定时任务")
    @Parameter(name = "id", description = "定时任务的ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "Long"))
    @PutMapping(value = "/jobs/{id}/run")
    public void execution(@PathVariable("id") Long id) {
        Job job = jobService.getById(id);
        jobService.execute(job);
    }

    @Operation(summary = "根据Id查询定时任务", description = "根据Id查询定时任务")
    @Parameter(name = "id", description = "定时任务的ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "Long"))
    @GetMapping("/jobs/{id}")
    public Job getJobById(@PathVariable("id") Long id) {
        return jobService.getById(id);
    }

//    private void checkUnique(Job job) {
//        //定时任务名称存在相同的 抛出
//        ApiAssert.isFalse(Err.x110048, jobService.exist(Wrappers.<Job>lambdaQuery()
//                        .eq(Job::getTaskName, job.getTaskName())
//                        .ne(Objects.nonNull(job.getId()), Job::getId, job.getId())
//                )
//        );
//        //参数，cron表达式，Class名称都相同的 抛出
//        ApiAssert.isFalse(Err.x110049, jobService.exist(Wrappers.<Job>lambdaQuery()
//                        .eq(Job::getParams, job.getParams())
//                        .eq(Job::getClassName, job.getClassName())
//                        .eq(Job::getCron, job.getCron())
//                        .ne(Objects.nonNull(job.getId()), Job::getId, job.getId())
//                )
//        );
//    }
//
//    /**
//     * 校验cron表达试，及是否有正确的执行时间
//     *
//     * @param cronExpression
//     */
//    boolean checkCronExpression(String cronExpression) {
//        boolean validExpression = CronExpression.isValidExpression(cronExpression);
//        ApiAssert.isTrue(Err.x110050, validExpression);
//        Date triggerTime = CronUtils.getNextTriggerTime(cronExpression);
//        ApiAssert.notNull(Err.x110066, triggerTime);
//        return true;
//    }

}
