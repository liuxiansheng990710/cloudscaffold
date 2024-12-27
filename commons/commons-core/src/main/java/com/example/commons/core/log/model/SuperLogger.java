package com.example.commons.core.log.model;

import java.io.Serializable;

import com.example.commons.core.model.ServerMetaData;
import com.example.commons.core.utils.ApplicationUtils;
import com.example.commons.core.utils.JacksonUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 日志父类
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 17:35
 */

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class SuperLogger implements Serializable {

    /**
     * 来源
     */
    private String origin;
    /**
     * 环境
     */
    private String environment;

    public SuperLogger() {
        ServerMetaData metaData = ApplicationUtils.getMetaData();
        this.origin = metaData.getApplicationName();
        this.environment = metaData.getServerEnvironment().getEnvironmentStr();
    }

    @Override
    public String toString() {
        return JacksonUtils.toJson(this);
    }

}
