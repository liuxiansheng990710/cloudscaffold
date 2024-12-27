package com.example.checke.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.example.checke.bean.EntityDataSource;
import com.example.checke.parser.EntityDataSourceParser;
import com.example.commons.core.utils.OkHttpUtils;
import com.example.commons.core.utils.StringUtils;
import com.example.commons.core.utils.TypeUtils;
import com.example.exception.CheckDataBaseException;
import com.example.properties.CheckDataSourceProperties;
import com.example.properties.CheckEntitiesProperties;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.collection.CollUtil;

/**
 * <p>
 * mysql检查执行器
 * <p>
 *
 * @author : 21
 * @since : 2024/3/13 14:48
 */

public class MySQLCheckActuator implements CheckHandler<List<EntityDataSource>> {

    private Map<String, JdbcTemplate> jdbcTemplates = new HashMap<>();
    private EntityDataSourceParser entityDataSourceParser;
    private Map<String, CheckDataSourceProperties> dataSourceProperties;
    private static final String COLUMN_SQL = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?";

    public MySQLCheckActuator(CheckEntitiesProperties entitiesProperties, Map<String, CheckDataSourceProperties> dataSourceProperties) {
        this.entityDataSourceParser = new EntityDataSourceParser(entitiesProperties);
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public void init() {
        dataSourceProperties.forEach((sourceType, dataSource) -> {
            HikariDataSource hikari = DataSourceBuilder.create()
                    .url(dataSource.getJdbcUrl())
                    .username(dataSource.getUsername())
                    .password(dataSource.getPassword())
                    .driverClassName(dataSource.getDriverClassName())
                    .type(HikariDataSource.class)
                    .build();
            hikari.setMaximumPoolSize(dataSource.getMaxPoolSize());
            JdbcTemplate jdbcTemplate = new JdbcTemplate(hikari);
            jdbcTemplates.put(sourceType, jdbcTemplate);
        });
    }

    @Override
    public List<EntityDataSource> check() {
        List<EntityDataSource> dataSources = entityDataSourceParser.parserEntities();
        List<EntityDataSource> absentDataSource = dataSources.stream().map(source -> {
            List<String> dbColumnNames = jdbcTemplates.get(source.getDbType())
                    .queryForList(COLUMN_SQL, source.getTableName(), source.getServiceName())
                    .stream()
                    .map(dbList -> dbList.get("COLUMN_NAME"))
                    .map(TypeUtils::castToString)
                    .map(StringUtils::removeBackQuote)
                    .collect(Collectors.toList());
            List<String> absentColumns = CollUtil.subtractToList(source.getColumnNames(), dbColumnNames);
            return AbsentEntitiesStructConvert.INSTANCE.afterComparisonConvertToAbsentEntities(absentColumns, source);
        }).filter(absent -> !CollectionUtils.isEmpty(absent.getColumnNames())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(absentDataSource)) {
            sendWarn(absentDataSource);
            throw new CheckDataBaseException("缺少字段 ： " + absentDataSource);
        }
        System.exit(0);
        return dataSources;
    }

    /**
     * 填充jdbc链接
     *
     * @param dataSourceProperties
     */
    private void fillJdbcTemplates(Map<String, CheckDataSourceProperties> dataSourceProperties) {

    }

    private void sendWarn(List<EntityDataSource> absents) {
        absents.forEach(absent -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("#### 数据库结构 CI通知\n");
                    builder.append("\n");
                    builder.append(String.format("- 环境：%s%n", System.getProperty("check.env")));
                    builder.append(String.format("- 服务名：%s%n", absent.getServiceName()));
                    builder.append(String.format("- 表名：%s%n", absent.getTableName()));
                    builder.append(String.format("- 字段：%s%n", absent.getColumnNames()));
                    builder.append("- 状态：**表结构缺失**\n");
                    builder.append("\n");
                    builder.append("> **数据库结构检查失败，请相关人员及时处理。**\n");
                    JSONObject jsonObject = new JSONObject();
                    JSONObject markdown = new JSONObject();
                    markdown.put("title", "数据库结构 CI通知");
                    markdown.put("text", builder.toString());
                    jsonObject.put("msgtype", "markdown");
                    jsonObject.put("markdown", markdown);
                    OkHttpUtils.postBody("https://oapi.dingtalk.com/robot/send?access_token=62434696754f5f418bfbebbd3802b7a162e2c37054ab12b19ca16f5d2a1f7ed6", jsonObject);
                }
        );
    }
}
