package com.example.cloud.openfeign.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * <p>
 * 时间-字符串转换器  手动装配
 * <p>
 *
 * @author : 21
 * @since : 2023/7/18 15:44
 */

public class Date2StringConverter implements Converter<Date, String> {

    @Override
    public String convert(Date source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(source);
    }
}
