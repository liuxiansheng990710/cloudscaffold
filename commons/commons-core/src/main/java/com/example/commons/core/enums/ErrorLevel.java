package com.example.commons.core.enums;

/**
 * <p>
 * 错误等级
 * <p>
 *
 * @author : 21
 * @since : 2023/12/19 9:52
 */

public enum ErrorLevel {

    /**
     * 正常提醒
     */
    REMIND,
    /**
     * 崩溃的，代码或系统方面的错误，能够马上确定处理的
     */
    CRASH,
    /**
     * 能确定不符合预期的，理论上不会走到的报错
     */
    UNEXPECTED,
    /**
     * 模糊区域的，不能确定要不要优化处理 可分解成remind、crash、unexpected、third
     */
    UNKNOWN,
    /**
     * 第三方异常(比如:请求其他系统)
     */
    THIRD
}
