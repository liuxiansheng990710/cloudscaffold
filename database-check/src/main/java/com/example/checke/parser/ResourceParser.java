package com.example.checke.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.example.commons.core.exceptions.ServerException;
import com.example.properties.CheckEntitiesProperties;

/**
 * <p>
 * 资源解析器(主要根据路径获取路径下所有的资源)
 * <p>
 *
 * @author : 21
 * @since : 2024/3/13 16:12
 */

public class ResourceParser {

    //可以使用路径模式找到并加载所有匹配的资源文件
    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    private final CheckEntitiesProperties checkEntitiesProperties;

    public ResourceParser(CheckEntitiesProperties checkEntitiesProperties) {
        this.checkEntitiesProperties = checkEntitiesProperties;
    }

    /**
     * 获取路径下所有的资源（也就是类文件）
     *
     * @return
     */
    public List<Resource> getResources() {
        return checkEntitiesProperties.getEntityPathPatterns()
                .stream()
                .map(pattern -> {
                    try {
                        return RESOURCE_PATTERN_RESOLVER.getResources(pattern);
                    } catch (IOException e) {
                        throw new ServerException(e);
                    }
                }).flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

}
