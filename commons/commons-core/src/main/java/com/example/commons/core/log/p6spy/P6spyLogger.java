package com.example.commons.core.log.p6spy;

import com.example.commons.core.utils.StringUtils;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * P6spy日志重写
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 16:33
 */

@Slf4j
public class P6spyLogger extends FormattedLogger {

    @Override
    public void logException(Exception e) {
        log.info("", e);
    }

    @Override
    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        final String msg = strategy.formatMessage(connectionId, now, elapsed,
                category.toString(), prepared, sql, url);
        //空日志不打印 如果该类sl4j2日志等级为debug时 进行统一打印为debug
        if (StringUtils.isBlank(msg)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(msg);
        }
    }

    @Override
    public void logText(String text) {
        log.info(text);
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        if (Category.DEBUG.equals(category)) {
            return log.isDebugEnabled();
        } else if (Category.INFO.equals(category)) {
            return log.isInfoEnabled();
        } else if (Category.WARN.equals(category)) {
            return log.isWarnEnabled();
        } else {
            return log.isErrorEnabled();
        }
    }
}
