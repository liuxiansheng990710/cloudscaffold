package com.example.utils;

import java.util.stream.Collectors;

import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;

import com.example.commons.core.utils.JacksonUtils;
import com.example.properties.CheckDataProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
/**
 * <p>
 *  解析yaml工具类
 * <p>
 *
 * @author : 21
 * @since : 2024/3/14 16:18
 */


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalysisYamlUtils {

    /**
     * 解析yml参数工厂实体
     */
    private static final YamlMapFactoryBean yaml = new YamlMapFactoryBean();

    /**
     * 设置替换yml参数规则  ${name:*} 如果name为空则替换为* 否则替换为name
     */
    private static final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
            PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
            PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
            PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, false);

    /**
     * 设置需要解析的yml文件
     *
     * @param resources
     */
    public static void setResources(Resource... resources) {
        yaml.setResources(resources);
    }

    /**
     * 解析yaml文件
     *
     * @return
     * @throws JsonProcessingException
     */
    public static CheckDataProperties analysisCheckDataProperties() throws JsonProcessingException {
        ObjectMapper objectMapper = JacksonUtils.getObjectMapper();
        //设置解析规则 例：checked-entities 解析为 checkedEntities
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        CheckDataProperties dataProperties = objectMapper.readValue(JacksonUtils.toJson(yaml.getObject()), CheckDataProperties.class);
        //将${}参数 替换为实际值
        dataProperties.getCheckedEntities().setEntityPathPatterns(dataProperties.getCheckedEntities().getEntityPathPatterns().stream()
                .map(pattern -> helper.replacePlaceholders(pattern, System.getProperties())).collect(Collectors.toList()));
        dataProperties.getCheckedEntities().setSkipPatterns(dataProperties.getCheckedEntities().getSkipPatterns().stream()
                .map(pattern -> helper.replacePlaceholders(pattern, System.getProperties())).collect(Collectors.toList()));
        return dataProperties;
    }

}
