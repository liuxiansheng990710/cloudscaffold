package com.example.checke.bean;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EntityDataSource {

    private String tableName;
    private List<String> columnNames;
    private String dbType;
    private String serviceName;

}
