package com.alibaba.csp.sentinel.log;

import static com.alibaba.csp.sentinel.util.ConfigUtil.addSeparator;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 主体与LogBase无异
 * 1. 抑制sout日志打印
 * 2. 日志打印方式修改为consonle
 * 3. 修改日志方式为log4j2
 * {@link com.alibaba.csp.sentinel.log.LogBase}
 * <p>
 *
 * @author : 21
 * @since : 2023/9/20 9:36
 */
@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogBase {

    public static final String LOG_DIR = "csp.sentinel.log.dir";
    public static final String LOG_NAME_USE_PID = "csp.sentinel.log.use.pid";
    public static final String LOG_OUTPUT_TYPE = "csp.sentinel.log.output.type";
    public static final String LOG_CHARSET = "csp.sentinel.log.charset";
    public static final String LOG_LEVEL = "csp.sentinel.log.level";

    /**
     * Output biz log (e.g. RecordLog and CommandCenterLog) to file.
     */
    public static final String LOG_OUTPUT_TYPE_FILE = "file";
    /**
     * Output biz log (e.g. RecordLog and CommandCenterLog) to console.
     */
    public static final String LOG_OUTPUT_TYPE_CONSOLE = "console";
    public static final String LOG_CHARSET_UTF8 = "utf-8";

    private static final String DIR_NAME = "logs" + File.separator + "csp";
    private static final String USER_HOME = "user.home";
    private static final Level LOG_DEFAULT_LEVEL = Level.INFO;

    private static boolean logNameUsePid;
    private static String logOutputType;
    private static String logBaseDir;
    private static String logCharSet;
    private static Level logLevel;

    static {
        try {
            initializeDefault();
            loadProperties();
        } catch (Throwable t) {
            log.error("[LogBase] FATAL ERROR when initializing logging config");
            t.printStackTrace();
        }
    }

    private static void initializeDefault() {
        logNameUsePid = false;
        //这里修改为控制台打印
        logOutputType = LOG_OUTPUT_TYPE_CONSOLE;
        logBaseDir = addSeparator(System.getProperty(USER_HOME)) + DIR_NAME + File.separator;
        logCharSet = LOG_CHARSET_UTF8;
        logLevel = LOG_DEFAULT_LEVEL;
    }

    private static void loadProperties() {
        Properties properties = LogConfigLoader.getProperties();

        logOutputType = properties.get(LOG_OUTPUT_TYPE) == null ? logOutputType : properties.getProperty(LOG_OUTPUT_TYPE);
        if (!LOG_OUTPUT_TYPE_FILE.equalsIgnoreCase(logOutputType) && !LOG_OUTPUT_TYPE_CONSOLE.equalsIgnoreCase(logOutputType)) {
            logOutputType = LOG_OUTPUT_TYPE_FILE;
        }

        logCharSet = properties.getProperty(LOG_CHARSET) == null ? logCharSet : properties.getProperty(LOG_CHARSET);

        logBaseDir = properties.getProperty(LOG_DIR) == null ? logBaseDir : properties.getProperty(LOG_DIR);
        logBaseDir = addSeparator(logBaseDir);
        File dir = new File(logBaseDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("ERROR: create Sentinel log base directory error: " + logBaseDir);
            }
        }

        String usePid = properties.getProperty(LOG_NAME_USE_PID);
        logNameUsePid = "true".equalsIgnoreCase(usePid);

        // load log level
        String logLevelString = properties.getProperty(LOG_LEVEL);
        if (logLevelString != null && (logLevelString = logLevelString.trim()).length() > 0) {
            try {
                logLevel = Level.parse(logLevelString);
            } catch (IllegalArgumentException e) {
                log.error("Log level : " + logLevel + " is invalid. Use default : " + LOG_DEFAULT_LEVEL);
            }
        }
    }

    /**
     * Whether log file name should contain pid. This switch is configured by {@link #LOG_NAME_USE_PID} system property.
     *
     * @return true if log file name should contain pid, return true, otherwise false
     */
    public static boolean isLogNameUsePid() {
        return logNameUsePid;
    }

    /**
     * Get the log file base directory path, which is guaranteed ended with {@link File#separator}.
     *
     * @return log file base directory path
     */
    public static String getLogBaseDir() {
        return logBaseDir;
    }

    /**
     * Get the log file output type.
     *
     * @return log output type, "file" by default
     */
    public static String getLogOutputType() {
        return logOutputType;
    }

    /**
     * Get the log file charset.
     *
     * @return the log file charset, "utf-8" by default
     */
    public static String getLogCharset() {
        return logCharSet;
    }

    public static Level getLogLevel() {
        return logLevel;
    }
}

