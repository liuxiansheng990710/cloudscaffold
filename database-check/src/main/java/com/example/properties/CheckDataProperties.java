package com.example.properties;

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class CheckDataProperties {

    /**
     * 需要检测的 实体属性类
     */
    private CheckEntitiesProperties checkedEntities;

    /**
     * 需要检测的 实体数据库连接类
     */
    private Map<String, CheckDataSourceProperties> datasource;

}
