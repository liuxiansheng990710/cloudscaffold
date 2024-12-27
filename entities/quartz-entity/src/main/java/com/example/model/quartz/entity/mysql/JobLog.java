package com.example.model.quartz.entity.mysql;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.commons.core.model.BaseModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 定时任务日志
 * <p>
 *
 * @author : 21
 * @since : 2023/10/7 18:22
 */
@TableName(value = "sys_job_log")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobLog extends BaseModel {

    private static final long serialVersionUID = 1L;

    //--------------------------------------------------------------数据库字段常量--------------------------------------------------------------
    public static final String JOB_ID = "job_id";
    public static final String CLASS_NAME = "class_name";
    public static final String CREATE_TIME = "create_time";
    public static final String CRON = "cron";
    public static final String EXCEPTION = "exception";
    public static final String SUCCESSED = "successed";
    public static final String TASK_NAME = "task_name";
    public static final String PARAMS = "params";
    public static final String RUN_TIME = "run_time";
    //--------------------------------------------------------------字段--------------------------------------------------------------
    @Schema(description = "任务id")
    @TableField(JOB_ID)
    private Long jobId;
    @Schema(description = "Class名称")
    @TableField(CLASS_NAME)
    private String className;
    @Schema(description = "创建时间")
    @TableField(value = CREATE_TIME, fill = FieldFill.INSERT)
    private Date createTime;
    @Schema(description = "cron表达式")
    @TableField(CRON)
    private String cron;
    @Schema(description = "异常信息")
    @TableField(EXCEPTION)
    private String exception;
    @Schema(description = "是否成功")
    @TableField(SUCCESSED)
    private Boolean successed;
    @Schema(description = "任务名称")
    @TableField(TASK_NAME)
    private String taskName;
    @Schema(description = "参数")
    @TableField(value = PARAMS)
    private JSONObject params;
    @Schema(description = "运行时间")
    @TableField(RUN_TIME)
    private String runTime;

}
