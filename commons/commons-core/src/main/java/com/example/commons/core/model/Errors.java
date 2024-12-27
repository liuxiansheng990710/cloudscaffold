package com.example.commons.core.model;


/**
 * <p>
 * 接口异常
 * <p>
 *
 * @author : 21
 * @since : 2023/12/18 10:55
 */

public interface Errors {

    /**
     * 获取error标识（自定义错误状态码）
     *
     * @return
     */
    String getError();

    /**
     * 获取http状态码
     *
     * @return
     */
    int getStatus();

    /**
     * 异常等级
     *
     * @return
     */
    String getRanking();

    /**
     * 获取提示信息
     *
     * @return
     */
    String getMsg();

    /**
     * 是否展示
     *
     * @return
     */
    boolean isShow();

    /**
     * 重写自定义返回消息
     *
     * @param msg
     * @return
     */
    default Errors errMsg(String msg) {
        return errBuild().msg(msg).build();
    }

    /**
     * 使用String.format转换ErrorCode返回消息
     *
     * @param format
     * @return
     */
    default Errors fMsg(Object... format) {
        return errBuild().msg(String.format(getMsg(), format)).build();
    }

    /**
     * 转换为ErrorCode
     *
     * @return
     */
    default Errors toErr() {
        return errBuild().build();
    }

    /**
     * 获取errBuild
     *
     * @return
     */
    default ErrorCode.ErrorCodeBuilder errBuild() {
        return ErrorCode.builder().status(getStatus()).error(getError()).ranking(getRanking()).msg(getMsg()).show(isShow());
    }

}
