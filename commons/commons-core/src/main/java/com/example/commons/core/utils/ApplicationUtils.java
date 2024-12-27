package com.example.commons.core.utils;

import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.commons.core.enums.ServerEnvironment;
import com.example.commons.core.enums.Servers;
import com.example.commons.core.exceptions.CommonUtilsException;
import com.example.commons.core.model.ServerMetaData;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 全局辅助工具类
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 16:23
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationUtils {

    /**
     * 服务器元数据
     */
    private static final ServerMetaData SERVER_META_DATA = ServerMetaData.getMetaData();

    /**
     * 获取spring Environment
     *
     * @return
     */
    public static Environment getSpringEnvironment() {
        return ApplicationContextRegister.getApplicationContext().getEnvironment();
    }

    /**
     * 初始化实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new CommonUtilsException("Error: NewInstance Exception:" + clazz.getName(), e);
        }
    }

    /**
     * 根据名称获取对象，不存在则报错
     *
     * @param className
     * @return
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new CommonUtilsException("Error: forName Exception: String class [" + className + "]", e);
        }
    }

    /**
     * 获取springbean
     *
     * @param beanName
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> requiredType) {
        if (containsBean(beanName)) {
            return ApplicationContextRegister.getApplicationContext().getBean(beanName, requiredType);
        }
        return null;
    }

    /**
     * 根据类型获取springbean
     *
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        return ApplicationContextRegister.getApplicationContext().getBean(requiredType);
    }

    /**
     * 获取多个springbean
     *
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return ApplicationContextRegister.getApplicationContext().getBeansOfType(requiredType);
    }

    /**
     * 获取springbean
     *
     * @param beanName
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName) {
        if (containsBean(beanName)) {
            Class<T> type = getType(beanName);
            return ApplicationContextRegister.getApplicationContext().getBean(beanName, type);
        }
        return null;
    }

    /**
     * 依赖spring框架获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception ignored) {
        }
        return request;
    }

    /**
     * 依赖spring框架获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static Optional<HttpServletRequest> getOptionalRequest() {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception ignored) {
        }
        return Optional.ofNullable(request);
    }

    /**
     * 依赖spring框架获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = null;
        try {
            response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        } catch (Exception ignored) {
        }
        return response;
    }

    /**
     * ApplicationContext是否包含该Bean
     *
     * @param name
     * @return
     */
    public static boolean containsBean(String name) {
        return ApplicationContextRegister.getApplicationContext().containsBean(name);
    }

    /**
     * ApplicationContext该Bean是否为单例
     *
     * @param name
     * @return
     */
    public static boolean isSingleton(String name) {
        return ApplicationContextRegister.getApplicationContext().isSingleton(name);
    }

    /**
     * 获取该Bean的Class
     *
     * @param name
     * @return
     */
    public static <T> Class<T> getType(String name) {
        return (Class<T>) ApplicationContextRegister.getApplicationContext().getType(name);
    }

    /**
     * 获取ServletContext
     *
     * @return
     */
    public static ServletContext getServletContext() {
        return ((AnnotationConfigServletWebServerApplicationContext) ApplicationContextRegister.getApplicationContext()).getServletContext();
    }

    /**
     * 获取ServerMetaData
     *
     * @return
     */
    public static ServerMetaData getMetaData() {
        return SERVER_META_DATA;
    }

    /**
     * 获取web_server_path
     *
     * @return
     */
    public static String getWebServerPath() {
        return SERVER_META_DATA.getServers().path();
    }

    /**
     * 获取web_servers
     *
     * @return
     */
    public static Servers getWebServers() {
        return SERVER_META_DATA.getServers();
    }

    /**
     * 获取运行环境
     *
     * @return
     */
    public static ServerEnvironment getEnvironment() {
        return SERVER_META_DATA.getServerEnvironment();
    }

}
