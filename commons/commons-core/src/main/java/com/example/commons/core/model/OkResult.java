package com.example.commons.core.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.utils.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * okthhp返回封装
 * <p>
 *
 * @author : 21
 * @since : 2023/12/5 18:04
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OkResult implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final OkResult DEFAULT = new OkResult(-1, "");

    /**
     * http 状态码
     */
    private Integer status;
    /**
     * 结果集返回
     */
    private String result;

    /**
     * 返回是否存在
     *
     * @return
     */
    public boolean isPresent() {
        return StringUtils.isNotBlank(result);
    }

    /**
     * 请求是否成功
     *
     * @return
     */
    public boolean isSuccessful() {
        return status >= 200 && status < 300;
    }

    /**
     * 转换成JSONArray
     *
     * @return
     */
    public JSONArray toJSONArray() {
        return JacksonUtils.parseArray(result);
    }

    /**
     * 转换成JSONObject
     *
     * @return
     */
    public JSONObject toJSONObject() {
        return JacksonUtils.parseObject(result);
    }

    /**
     * 转换成对象
     *
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    public <T> T readValue(TypeReference<T> valueTypeRef) {
        return JacksonUtils.readValue(result, valueTypeRef);
    }

    /**
     * 转换成对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T readValue(Class<T> clazz) {
        return JacksonUtils.readValue(result, clazz);
    }
}
