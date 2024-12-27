package com.example.commons.core.log.p6spy;

import com.example.commons.core.log.model.SQLLogger;
import com.example.commons.core.utils.StringUtils;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * <p>
 * p6spy日志格式重写
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 16:37
 */

public class P6spyLogFormat implements MessageFormattingStrategy {

    /**
     * 因在数据库连接池中，当一个连接空闲一段时间后，数据库服务器可能会主动关闭这个连接。为了确保在从连接池中获取连接时，连接依然是有效的，可以通过发送 ping 请求来保持连接的活跃状态
     * 它会发送一个 SELECT 1 的查询语句，不对查询结果进行任何处理，只是为了检测连接是否仍然有效。
     * 所以不打印此sql
     */
    private static final String PING_SQL = "/* ping */ SELECT 1";

    /**
     * 1. 定时任务sql不打印
     * 2. 由于预处理语句以 "SELECT COLUMN_NAME" 开头的查询通常是用于获取表的列名信息，而不是执行实际的数据库操作  所以不打印
     * 3. 由于预处理语句以 "SELECT A.ATTNAME AS COLUMN_NAME" 开头的查询通常是用于获取列别名信息，而不是执行实际的数据库操作  所以不打印
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (StringUtils.isBlank(sql) || PING_SQL.equals(sql)) {
            return null;
        }
        if (CharSequenceUtil.startWithIgnoreCase(Thread.currentThread().getName(), "quartz") || CharSequenceUtil.startWithIgnoreCase(prepared, "SELECT COLUMN_NAME") || CharSequenceUtil.startWithIgnoreCase(prepared, "SELECT A.ATTNAME AS COLUMN_NAME")) {
            return null;
        }
        SQLLogger sqlLogger = new SQLLogger();
        sqlLogger.setRunTime(elapsed + "ms");
        //多个连续空白字符（空格、制表符、换行符等）替换为单个空格
        sqlLogger.setSql(sql.replaceAll("[\\s]+", " "));
        return SQLLogger.LOG_PREFIX + sqlLogger;
    }

}
