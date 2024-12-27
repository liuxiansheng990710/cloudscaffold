package com.example.commons.core.log.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * sql日志
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 17:38
 */

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SQLLogger extends SuperLogger {

    public static final String LOG_PREFIX = "<sql> - ";

    /**
     * SQL
     */
    private String sql;
    /**
     * 运行时间 单位:ms
     */
    private String runTime;


}
