package com.example.spring.boot.cache.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import com.example.spring.boot.cache.config.RedissonCaffeineCacheConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * <p>
 * 解析配置文件
 * {@link org.redisson.spring.cache.CacheConfigSupport}
 * <p>
 *
 * @author : 21
 * @since : 2023/10/16 10:51
 */

public class RedissonCaffeineCacheConfigSupport {

    ObjectMapper jsonMapper = new ObjectMapper();
    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public Map<String, RedissonCaffeineCacheConfig> fromJSON(String content) throws IOException {
        return jsonMapper.readValue(content, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromJSON(File file) throws IOException {
        return jsonMapper.readValue(file, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromJSON(URL url) throws IOException {
        return jsonMapper.readValue(url, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromJSON(Reader reader) throws IOException {
        return jsonMapper.readValue(reader, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromJSON(InputStream inputStream) throws IOException {
        return jsonMapper.readValue(inputStream, new TypeReference<>() {
        });
    }

    public String toJSON(Map<String, ? extends RedissonCaffeineCacheConfig> configs) throws IOException {
        return jsonMapper.writeValueAsString(configs);
    }

    public Map<String, RedissonCaffeineCacheConfig> fromYAML(String content) throws IOException {
        return yamlMapper.readValue(content, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromYAML(File file) throws IOException {
        return yamlMapper.readValue(file, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromYAML(URL url) throws IOException {
        return yamlMapper.readValue(url, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromYAML(Reader reader) throws IOException {
        return yamlMapper.readValue(reader, new TypeReference<>() {
        });
    }

    public Map<String, RedissonCaffeineCacheConfig> fromYAML(InputStream inputStream) throws IOException {
        return yamlMapper.readValue(inputStream, new TypeReference<>() {
        });
    }

    public String toYAML(Map<String, ? extends RedissonCaffeineCacheConfig> configs) throws IOException {
        return yamlMapper.writeValueAsString(configs);
    }

}
