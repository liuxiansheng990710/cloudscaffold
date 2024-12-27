package com.example.spring.boot.cache.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 缓存变更消息通知消息体
 * <p>
 *
 * @author : 21
 * @since : 2023/10/17 14:53
 */

@Data
@NoArgsConstructor
public class CacheMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 缓存KEY
     */
    private Object key;

    public CacheMessage(String cacheName, Object key) {
        this.cacheName = cacheName;
        this.key = key;
    }

}
