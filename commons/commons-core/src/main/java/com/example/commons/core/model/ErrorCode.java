package com.example.commons.core.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>
 * 统一异常
 * <p>
 *
 * @author : 21
 * @since : 2023/12/18 10:58
 */

@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCode implements Errors, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自定义错误状态码
     */
    private String error;
    /**
     * http状态码
     */
    private int status;
    /**
     * 等级
     */
    private String ranking;
    /**
     * 错误消息
     */
    private String msg;
    /**
     * 错误消息
     */
    private boolean show;

    @Override
    public String getError() {
        return error;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getRanking() {
        return ranking;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public boolean isShow() {
        return show;
    }
}
