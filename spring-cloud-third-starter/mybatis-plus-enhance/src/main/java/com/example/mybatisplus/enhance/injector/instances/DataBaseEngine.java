package com.example.mybatisplus.enhance.injector.instances;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.jdbc.SqlRunner;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * 数据库引擎接口（目前支持mysql PGSql）若需扩展 实现该类即可
 * <p>
 *
 * @author : 21
 * @since : 2024/2/4 11:47
 */

public interface DataBaseEngine {

    String EMPTY_STR = "";

    /**
     * 获取数据库字段SQL(这里提取出来是便于后续可以方便修改)
     *
     * @return
     */
    String getDataBaseColumnSQL();

    /**
     * 数据库字段返回信息关键字(查询结果返回时 使用此关键字进行辨识)
     *
     * @return
     */
    String getSQLKeyWords();

    /**
     * 数据库字段返回列中标识(例：MySQL -> `Name`)
     *
     * @return
     */
    String getColumnMark();

    /**
     * 获取实体字段
     *
     * @param tableInfo
     * @return
     */
    default List<String> getModelColumns(TableInfo tableInfo) {
        //获取@TableField()字段
        List<String> cloumns = tableInfo.getFieldList().stream().map(field -> field.getColumn().replace(getColumnMark(), EMPTY_STR)).collect(Collectors.toList());
        //如果存在主键（@TableId）字段 将其也加进来
        if (StringUtils.isNotBlank(tableInfo.getKeyColumn())) {
            cloumns.add(tableInfo.getKeyColumn().replace(getColumnMark(), EMPTY_STR));
        }
        return cloumns;
    }

    /**
     * 获取数据库字段
     *
     * @param connection
     * @param tableName
     * @return
     */
    default List<String> getDataBaseColumns(Connection connection, String tableName) {
        List<Map<String, Object>> columnMaps = Collections.emptyList();
        try {
            //执行SQL
            columnMaps = new SqlRunner(connection).selectAll(getDataBaseColumnSQL(), tableName);
        } catch (Exception ignore) {
            // 一般来说 这里不会发生异常 除非数据库链接出现问题 所以忽略这个异常
        }
        //因为上面查询SQL返回的是COLUMN_NAME(可以修改) 所以根据此key获取列值
        return columnMaps.stream().map(e -> e.get(getSQLKeyWords()).toString()).collect(Collectors.toList());
    }

}
