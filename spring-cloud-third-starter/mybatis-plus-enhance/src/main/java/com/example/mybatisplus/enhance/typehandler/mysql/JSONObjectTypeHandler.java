package com.example.mybatisplus.enhance.typehandler.mysql;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.commons.core.utils.JacksonUtils;

/**
 * <p>
 * mybatis-JSONObject 类型处理器
 *
 * @author : 21
 * @MappedTypes({JSONObject.class})：指定类型处理器处理的java类型（JSONObject）
 * @MappedJdbcTypes({JdbcType.VARCHAR})：指定类型处理器处理的数据库字段类型，表示将JSONObject类型映射到数据库时为varchar类型 <p>
 * @since : 2023/10/7 15:34
 */

@MappedTypes({JSONObject.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JSONObjectTypeHandler extends BaseTypeHandler<JSONObject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.nonNull(parameter)) {
            ps.setString(i, JacksonUtils.toJson(parameter));
        } else {
            ps.setString(i, "{}");
        }
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return parseObject(columnValue);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return parseObject(columnValue);
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return parseObject(columnValue);
    }

    private JSONObject parseObject(String jsonObj) {
        if (StringUtils.isNotBlank(jsonObj)) {
            return JacksonUtils.parseObject(jsonObj);
        }
        return new JSONObject();
    }
}
