package com.example.model.base.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.commons.core.model.BaseModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName("app")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class App extends BaseModel {

    private static final long serialVersionUID = 1L;

    //--------------------------------------------------------------数据库字段常量--------------------------------------------------------------
    public static final String ID = "id";
    public static final String NAME = "name";

//--------------------------------------------------------------字段--------------------------------------------------------------

    @Schema(description = "应用名称")
    @TableField(value = NAME, keepGlobalFormat = true)
    private String name;

}
