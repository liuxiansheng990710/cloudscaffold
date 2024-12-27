package com.example.properties;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class CheckEntitiesProperties {

    //需要检测的实体路径
    private List<String> entityPathPatterns = new ArrayList<>();
    //需要跳过检测的实体路径  为以后兼容考虑,目前只检测了mysql 之后可以跳过某些数据库类型的检测（也可以使用分包来解决）
    private List<String> skipPatterns = new ArrayList<>();
    //需要排除检测的实体（全路径）
    private List<String> excludeClasses = new ArrayList<>();
    //数据库类型集合
    private List<String> dbTypes = new ArrayList<>();
    private String defaultDbType = "mysql";

}
