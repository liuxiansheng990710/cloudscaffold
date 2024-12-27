package com.example.mybatisplus.enhance.metahandler;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * <p>
 * 自定义mybatis-plus  字段填充
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 10:23
 */

public class CustomMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建时间
     */
    private static final String CREATE_TIME = "createTime";
    /**
     * 修改时间
     */
    private static final String UPDATE_TIME = "updateTime";
    /**
     * 删除标识
     */
    private static final String DELETED = "deleted";
    //TODO 此处暂定，待登录方式确定后填充
    /**
     * 创建账号
     */
    private static final String CREATE_BY = "createBy";
    /**
     * 修改账号
     */
    private static final String UPDATE_BY = "updateBy";
    /**
     * 创建人
     */
    private static final String CREATER = "creater";
    /**
     * 修改人
     */
    private static final String REVISER = "reviser";

    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, CREATE_TIME, Date.class, new Date());
        strictInsertFill(metaObject, UPDATE_TIME, Date.class, new Date());
        strictInsertFill(metaObject, DELETED, Boolean.class, Boolean.FALSE);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, UPDATE_TIME, Date.class, new Date());

    }
}
