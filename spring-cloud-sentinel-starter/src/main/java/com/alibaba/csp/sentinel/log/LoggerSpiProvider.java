package com.alibaba.csp.sentinel.log;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.alibaba.csp.sentinel.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 与LoggerSpiProvider无异
 * 1. 抑制sout日志打印
 * 2. 日志打印方式修改为log4j2
 * {@link com.alibaba.csp.sentinel.log.LoggerSpiProvider}
 * <p>
 *
 * @author : 21
 * @since : 2023/9/20 10:24
 */

@Slf4j
public final class LoggerSpiProvider {

    private static final Map<String, Logger> LOGGER_MAP = new HashMap<>();

    static {
        // NOTE: this class SHOULD NOT depend on any other Sentinel classes
        // except the util classes to avoid circular dependency.
        try {
            resolveLoggers();
        } catch (Throwable t) {
            log.error("Failed to resolve Sentinel Logger SPI");
            t.printStackTrace();
        }
    }

    public static Logger getLogger(String name) {
        if (name == null) {
            return null;
        }
        return LOGGER_MAP.get(name);
    }

    private static void resolveLoggers() {
        // NOTE: Here we cannot use {@code SpiLoader} directly because it depends on the RecordLog.
        ServiceLoader<Logger> loggerLoader = ServiceLoader.load(Logger.class);

        for (Logger logger : loggerLoader) {
            LogTarget annotation = logger.getClass().getAnnotation(LogTarget.class);
            if (annotation == null) {
                continue;
            }
            String name = annotation.value();
            // Load first encountered logger if multiple loggers are associated with the same name.
            if (StringUtil.isNotBlank(name) && !LOGGER_MAP.containsKey(name)) {
                LOGGER_MAP.put(name, logger);
            }
        }
    }

    private LoggerSpiProvider() {
    }
}
