package com.example.commons.core.enums;

import com.example.commons.core.cons.GlobalCons;
import com.example.commons.core.utils.StringUtils;

/**
 * <p>
 * 运行环境枚举类
 * <p>
 *
 * @author : 21
 * @since : 2023/12/13 14:34
 */

public enum ServerEnvironment {

    /**
     * 未初始化
     */
    UNKNOWN("unknown", false),
    /**
     * 开发环境
     */
    ALPHA("alpha", false),
    /**
     * 测试环境
     */
    BETA("beta", false),
    /**
     * 准生产环境
     */
    RC("rc", true),
    /**
     * 生产环境
     */
    RELEASE("release", true);

    private final String environment;

    /**
     * 是否是正式环境
     */
    private final boolean release;

    ServerEnvironment(final String environment, final boolean release) {
        this.environment = environment;
        this.release = release;
    }

    public String getEnvironmentStr() {
        return environment;
    }

    public boolean isRelease() {
        return this.release;
    }

    public boolean isUnknown() {
        return this.equals(UNKNOWN);
    }

    /**
     * 获取当前环境
     *
     * @param environmentStr
     * @return
     */
    public static ServerEnvironment getEnvironment(String environmentStr) {
        if (StringUtils.isNotBlank(environmentStr)) {
            ServerEnvironment[] enums = ServerEnvironment.values();
            for (ServerEnvironment serverEnvironment : enums) {
                if (serverEnvironment.getEnvironmentStr().equalsIgnoreCase(environmentStr)) {
                    return serverEnvironment;
                }
            }
        }
        return ServerEnvironment.UNKNOWN;
    }

    /**
     * 获取当前环境
     *
     * @return
     */
    public static ServerEnvironment getEnvironment() {
        String environmentStr = System.getProperty(GlobalCons.ENV_SERVER_ENVIRONMENT);
        return getEnvironment(environmentStr);
    }
}
