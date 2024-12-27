package com.example.model.quartz.entity.mysql;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.commons.core.model.BaseModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 定时任务表
 * <p>
 *
 * @author : 21
 * @since : 2023/10/7 18:21
 */
@TableName(value = "sys_job")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Job extends BaseModel {

    private static final long serialVersionUID = 1L;

    //--------------------------------------------------------------数据库字段常量--------------------------------------------------------------
    public static final String TASK_NAME = "task_name";
    public static final String CLASS_NAME = "class_name";
    public static final String PARAMS = "params";
    public static final String CRON = "cron";
    public static final String DESCRIPTION = "description";
    public static final String PAUSED = "paused";
    public static final String UPDATE_TIME = "update_time";
    public static final String CREATE_TIME = "create_time";
    public static final String DELETED = "deleted";
    public static final String CREATER = "creater";
    public static final String REVISER = "reviser";
    public static final String CREATE_BY = "create_by";
    public static final String UPDATE_BY = "update_by";
    //--------------------------------------------------------------字段--------------------------------------------------------------
    @Schema(description = "任务名称")
    @TableField(TASK_NAME)
    private String taskName;
    @Schema(description = "Class名称")
    @TableField(CLASS_NAME)
    private String className;
    @Schema(description = "参数")
    @TableField(value = PARAMS)
    private JSONObject params;
    @Schema(description = "cron表达式")
    @TableField(CRON)
    private String cron;
    @Schema(description = "是否暂停")
    @TableField(PAUSED)
    private Boolean paused;
    @Schema(description = "备注")
    @TableField(DESCRIPTION)
    private String description;
    @Schema(description = "创建时间")
    @TableField(value = CREATE_TIME, fill = FieldFill.INSERT)
    private Date createTime;
    @Schema(description = "更新时间")
    @TableField(value = UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @Schema(title = "删除标记", description = "0:未删除;1:已删除")
    @TableLogic
    @TableField(value = DELETED, fill = FieldFill.INSERT)
    private Boolean deleted;
    @Schema(description = "创建者")
    @TableField(value = CREATER, fill = FieldFill.INSERT)
    private String creater;
    @Schema(description = "修改者")
    @TableField(value = REVISER, fill = FieldFill.INSERT_UPDATE)
    private String reviser;
    @Schema(description = "创建者id")
    @TableField(value = CREATE_BY, fill = FieldFill.INSERT)
    private String createBy;
    @Schema(description = "修改者id")
    @TableField(value = UPDATE_BY, fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

}
