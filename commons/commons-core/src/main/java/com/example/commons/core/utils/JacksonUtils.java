package com.example.commons.core.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.exceptions.CommonUtilsException;
import com.example.commons.core.jackson.deserializer.DateStrDeserializer;
import com.example.commons.core.jackson.serializer.DateToNumberSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cn.hutool.core.date.DatePattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class JacksonUtils {

    private final static ObjectMapper OBJECT_MAPPER = initObjectMapper(new ObjectMapper());

    /**
     * 获取ObjectMapper
     *
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 初始化 ObjectMapper
     *
     * @param objectMapper
     * @return
     */
    public static ObjectMapper initObjectMapper(ObjectMapper objectMapper) {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
        }
        return doInitObjectMapper(objectMapper);
    }

    /**
     * 初始化 ObjectMapper 时间方法
     *
     * @return
     */
    private static ObjectMapper doInitObjectMapper(ObjectMapper objectMapper) {
        //因需传递http消息中的mapper 所以暂不处理过时方法
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.disable(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return registerModule(objectMapper);
    }

    /**
     * 注册模块
     *
     * @param objectMapper
     * @return
     */
    private static ObjectMapper registerModule(ObjectMapper objectMapper) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        objectMapper.registerModule(new JavaTimeModule());
        //Date 使用自定义序列化
        simpleModule.addSerializer(Date.class, DateToNumberSerializer.instance);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
        DateStrDeserializer dateDeserializer = new DateStrDeserializer(DateDeserializers.DateDeserializer.instance, dateFormat, DatePattern.NORM_DATETIME_PATTERN);
        simpleModule.addDeserializer(Date.class, dateDeserializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    /**
     * 包装 MappingJackson2HttpMessageConverter
     *
     * @return
     */
    public static Consumer<HttpMessageConverter<?>> wrapperObjectMapper() {
        return converter -> {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter httpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                setUpJacksonHttpMessageConverter(httpMessageConverter);
            }
        };
    }

    /**
     * 获取MappingJackson2HttpMessageConverter
     *
     * @return
     */
    public static MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        return setUpJacksonHttpMessageConverter(new MappingJackson2HttpMessageConverter());
    }

    /**
     * 设置JacksonHttpMessageConverter
     *
     * @param httpMessageConverter
     * @return
     */
    private static MappingJackson2HttpMessageConverter setUpJacksonHttpMessageConverter(MappingJackson2HttpMessageConverter httpMessageConverter) {
        ObjectMapper objectMapper = httpMessageConverter.getObjectMapper();
        initObjectMapper(objectMapper);
        httpMessageConverter.setSupportedMediaTypes(getMediaTypes());
        httpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        return httpMessageConverter;
    }

    public static List<MediaType> getMediaTypes() {
        return Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.valueOf("application/vnd.spring-boot.actuator.v2+json"),
                MediaType.APPLICATION_ATOM_XML,
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.APPLICATION_PDF,
                MediaType.APPLICATION_RSS_XML,
                MediaType.APPLICATION_XHTML_XML,
                MediaType.APPLICATION_XML,
                MediaType.IMAGE_GIF,
                MediaType.IMAGE_JPEG,
                MediaType.IMAGE_PNG,
                MediaType.TEXT_HTML,
                MediaType.TEXT_MARKDOWN,
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_XML);
    }

    /**
     * <p>
     * 是否为CharSequence类型
     * </p>
     *
     * @param object
     * @return
     */
    public static boolean isCharSequence(Object object) {
        return !Objects.isNull(object) && Objects.nonNull(object.getClass()) && CharSequence.class.isAssignableFrom(object.getClass());
    }

    /**
     * 转换Json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (isCharSequence(object)) {
            return (String) object;
        }
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new CommonUtilsException(e);
        }
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param json
     * @return
     */
    public static Object parse(String json) {
        Object object = null;
        try {
            object = getObjectMapper().readValue(json, Object.class);
        } catch (Exception ignored) {

        }
        return object;
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T readValue(String json, Class<T> clazz) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, clazz);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param input
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T readValue(byte[] input, Class<T> clazz) {
        T t = null;
        try {
            t = getObjectMapper().readValue(input, clazz);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param json
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    public static <T> T readValue(String json, TypeReference<T> valueTypeRef) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, valueTypeRef);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param input
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    public static <T> T readValue(byte[] input, TypeReference<T> valueTypeRef) {
        T t = null;
        try {
            t = getObjectMapper().readValue(input, valueTypeRef);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * 兼容使用fastjson解析json数据(JSONObject)
     *
     * @param json
     * @return
     */
    public static JSONObject parseObject(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(json);
        } catch (Exception ignored) {
        }
        return jsonObject;
    }

    /**
     * 兼容使用fastjson解析json数据(JSONObject)
     *
     * @param obj
     * @return
     */
    public static JSONObject parseObject(Object obj) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(toJson(obj));
        } catch (Exception ignored) {
        }
        return jsonObject;
    }

    /**
     * 兼容使用fastjson解析json数据(JSONArray)
     *
     * @param json
     * @return
     */
    public static JSONArray parseArray(String json) {
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(json);
        } catch (Exception ignored) {
        }
        return jsonArray;
    }

    /**
     * 对象转换为实体 转换失败返回null
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parse(Object obj, Class<T> clazz) {
        T t = null;
        try {
            t = OBJECT_MAPPER.convertValue(obj, clazz);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * 转换对象为byte
     *
     * @param obj
     * @return
     */
    public static byte[] toBytes(Object obj) {

        byte[] bytes = new byte[0];
        try {
            bytes = getObjectMapper().writeValueAsBytes(obj);
        } catch (Exception ignored) {
        }
        return bytes;
    }

}
