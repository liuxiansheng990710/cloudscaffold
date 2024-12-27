package com.example.spring.boot.cache.support;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Redisson分布式发布-订阅工具类
 * <p>
 *
 * @author : 21
 * @since : 2023/10/17 15:27
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedissonTopicUtils {

    private static final SerializationCodec SERIALIZATION_CODEC = new SerializationCodec();

    /**
     * 获取RTopic
     *
     * @param redissonClient
     * @param topic
     * @return
     */
    public static RTopic getTopic(RedissonClient redissonClient, String topic) {
        return redissonClient.getTopic(topic, SERIALIZATION_CODEC);
    }

}
