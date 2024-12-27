package com.example.spring.boot.redisson.handler;

import com.example.spring.boot.redisson.model.LockInfo;

/**
 * <p>
 * 解锁超时处理
 * <p>
 *
 * @author : 21
 * @since : 2023/10/26 9:21
 */

public interface ReleaseTimeoutHandler {

    void handle(LockInfo lockInfo);

}
