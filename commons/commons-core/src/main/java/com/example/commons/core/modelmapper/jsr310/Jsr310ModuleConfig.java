package com.example.commons.core.modelmapper.jsr310;

import java.time.ZoneId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 定义jsr-310日期时间等格式配置
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 15:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jsr310ModuleConfig {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    @Builder.Default
    //定义日期格式
    private String datePattern = DEFAULT_DATE_PATTERN;
    @Builder.Default
    //定义日期和时间的格式
    private String dateTimePattern = DEFAULT_DATE_TIME_PATTERN;
    @Builder.Default
    //定义时间的格式
    private String timePattern = DEFAULT_TIME_PATTERN;
    @Builder.Default
    //定义时区，默认为系统默认的时区
    private ZoneId zoneId = ZoneId.systemDefault();
}
