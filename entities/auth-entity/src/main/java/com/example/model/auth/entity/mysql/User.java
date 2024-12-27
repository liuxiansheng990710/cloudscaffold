package com.example.model.auth.entity.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.commons.core.model.BaseConvert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@TableName("user")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends BaseConvert {

    private static final long serialVersionUID = 1L;

    //--------------------------------------------------------------数据库字段常量--------------------------------------------------------------
    public static final String UID = "uid";
    public static final String NAME = "name";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";

//--------------------------------------------------------------字段--------------------------------------------------------------

    @TableId(value = UID)
    private Long uid;
    @Schema(description = "应用名称")
    @TableField(value = NAME, keepGlobalFormat = true)
    private String name;
    @Schema(description = "时间")
    @TableField(value = CREATE_TIME, fill = FieldFill.INSERT)
    private Date createTime;
    @Schema(description = "时间")
    @TableField(value = UPDATE_TIME, fill = FieldFill.INSERT)
    private Date updateTime;
}
