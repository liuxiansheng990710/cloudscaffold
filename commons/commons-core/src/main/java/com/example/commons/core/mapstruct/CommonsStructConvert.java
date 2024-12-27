package com.example.commons.core.mapstruct;

import java.util.Objects;

import com.example.commons.core.jackson.enums.IEnum;

/**
 * <p>
 * MapStuct转换公共方法
 * <P>1. 实体转换时枚举类转换</P>
 * <P>示例：@Mapper(uses = {CommonsStructConvert.class}, typeConversionPolicy = ReportingPolicy.WARN, unmappedTargetPolicy = ReportingPolicy.IGNORE)</P>
 * <P>typeConversionPolicy = ReportingPolicy.WARN：类型转换时遇到问题，它将发出警告</P>
 * <P>unmappedTargetPolicy = ReportingPolicy.IGNORE)：如果有一些目标类的属性没有被映射</P>
 * <p>
 *
 * @author : 21
 * @since : 2023/11/2 18:03
 */

public class CommonsStructConvert {

    public Integer booleanToInteger(Boolean source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Objects.equals(Boolean.TRUE, source) ? 1 : 0;
    }

    public Boolean integerToBoolean(Integer source) {
        return Objects.equals(1, source) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Integer enumConvertToInteger(IEnum sourceEnum) {
        if (Objects.isNull(sourceEnum)) {
            return null;
        }
        return sourceEnum.getValue();
    }

}
