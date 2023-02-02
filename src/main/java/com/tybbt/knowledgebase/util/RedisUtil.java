package com.tybbt.knowledgebase.util;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private static final Logger LOG = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
    private RedisTemplate redisTemplate;

    public boolean validateRepeat(String key, long second) {
        if (redisTemplate.hasKey(key)) {
            LOG.info("key已存在：{}", key);
            return false;
        } else {
            LOG.info("key不存在：{}", key);
            redisTemplate.opsForValue().set(key, key, second, TimeUnit.SECONDS);
            return true;
        }
    }

}
