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

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.commons.core.utils.JacksonUtils;

/**
 * <p>
 * JSONArray类型处理器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 10:48
 */

@MappedTypes({JSONArray.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.nonNull(parameter)) {
            ps.setString(i, JacksonUtils.toJson(parameter));
        } else {
            ps.setString(i, "[]");
        }
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonArryay = rs.getString(columnName);
        return parseArray(jsonArryay);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonArryay = rs.getString(columnIndex);
        return parseArray(jsonArryay);
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonArryay = cs.getString(columnIndex);
        return parseArray(jsonArryay);
    }

    private JSONArray parseArray(String jsonArryay) {
        if (StringUtils.isNotBlank(jsonArryay)) {
            return JacksonUtils.parseArray(jsonArryay);
        }
        return new JSONArray();
    }

}
