package com.cxcoder.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;

/**
 * @Author: ChangXuan
 * @Decription: Redis 工具类
 * @Date: 16:02 2021/1/14
 **/
@Slf4j
@Component
public class RedisUtil {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean setString(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("设置key: " + key + ",value: " + value, e);
            return false;
        }
        return true;
    }

    // 第一版

    /**
     * 加锁
     * @param key 锁名称
     * @return boolean 是否加锁成功
     */
    public boolean lock(String key) {
        boolean lock;
        try {
            lock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "LOCK"));
        } catch (Exception e) {
            log.error("获取锁：{}，出现错误{}", key, e);
            return false;
        }
        return lock;
    }


    /**
     * 释放锁
     * @param key 锁名称
     */
    public void unlock(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
    }

    // 第二版

    /**
     * 加锁/获取锁
     * @param key 锁名称
     * @param identity 标识
     * @return boolean 是否加锁成功
     */
    public boolean lock(String key, String identity) {
        boolean lock;
        try {
            lock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, identity));
        } catch (Exception e) {
            log.error("获取锁：{}，出现错误{}", key, e);
            return false;
        }
        return lock;
    }

    /**
     * 释放锁
     * @param key 锁名称
     * @param identity 标识
     */
    public void unlock(String key, String identity) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            String value = obj != null ? obj.toString():null;
            if (value != null){
                if (value.equals(identity)) {
                    redisTemplate.delete(key);
                }
            }
        }catch (Exception e) {
            log.error("释放锁操作失败,key:" + key + "失败原因:",e);
        }
    }

    /**
     * 阻塞锁
     * @param key 锁名称
     * @param identity 标识
     * @param sec 超时时长（秒）
     * @return boolean 是否成功获取锁
     */
    public boolean lockb(String key, String identity, int sec) {
        try {
            int count = 0;
            while (!lock(key, identity)) {
                Thread.sleep(100);
                count++;
                if (count > sec * 1000L / 100) {
                    if (sec != 0) {
                        log.warn("线程：{}获取锁{}超时", Thread.currentThread().getName(), key);
                    }
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("获取锁：{}，出现错误{}", key, e);
            return false;
        }
    }

    // 第三版

    /**
     * 锁最大存活时长
     */
    private final static int LOCK_MAX_TIMEOUT_SEC = 600;

    /**
     * 加锁/获取锁 [超时时间]
     * @param key 锁名称
     * @param identity 标识
     * @param expireSec 过期时间
     * @return boolean 是否加锁成功
     */
    public boolean lock(String key, String identity, int expireSec) {
        boolean lock;
        try {
            lock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, identity, Duration.ofSeconds(expireSec)));
        } catch (Exception e) {
            log.error("获取锁：{}，出现错误{}", key, e);
            return false;
        }
        return lock;
    }

    /**
     * 加锁/获取锁，自定义实现[过期时间]
     * @param key 锁名称
     * @param identity 标识
     * @param expireSec 过期时间
     * @return boolean 是否加锁成功
     */
    public boolean lockV2(String key, String identity, int expireSec) {
        // 每个节点时间可能不一样
        Long currTime = redisTemplate.getRequiredConnectionFactory().getConnection().time();
        Assert.notNull(currTime, "Redis 服务异常");
        // 超时时间戳
        String expire = String.valueOf(currTime + expireSec);
        String separator = ",";
        // 获取锁
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, identity + separator + expire))) {
            return true;
        }
        // 未获取锁，检查锁状态
        Object obj = redisTemplate.opsForValue().get(key);
        String value = obj != null ? obj.toString() : null;
        if (value != null) {
            // 锁已过期
            if (Long.parseLong(value.split(separator)[1]) < currTime) {
                log.error("锁：{}，未显示释放，已过期", key);
                // 释放锁
                unlockV2(key, value.split(separator)[0]);
                // 再次尝试获取锁
                return lockV2(key, identity, expireSec);
            }
        }
        return false;
    }

    /**
     * 释放锁
     * @param key 锁名称
     * @param identity 标识
     */
    public void unlockV2(String key, String identity) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            String value = obj != null ? obj.toString():null;
            if (value != null){
                if (value.split(",")[0].equals(identity)) {
                    redisTemplate.delete(key);
                }
            }
        }catch (Exception e) {
            log.error("释放锁操作失败,key:" + key + "失败原因:",e);
        }
    }


    /**
     * 加锁/获取锁，自定义实现[过期时间]
     * @param key 锁名称
     * @param identity 标识
     * @return 是否获取锁
     */
    public boolean lockV2(String key, String identity) {
        return lockV2(key, identity, LOCK_MAX_TIMEOUT_SEC);
    }

    /**
     * 拥有排他时长的锁
     * @param key 锁名称
     * @param identity 标识
     * @param expireSec 过期时间
     * @param interval 排它时间
     * @return 是否获取锁
     */
    public boolean lock(String key, String identity, int expireSec, int interval) {
        if (lock(key, identity, expireSec)) {
            String preTimeStr = redisTemplate.opsForValue().get(key + "t") != null ? redisTemplate.opsForValue().get(key + "t").toString():null;
            String nowTimeStr = Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString().split(",")[1];
            if (preTimeStr != null) {
                long preTime = Long.parseLong(preTimeStr);
                long nowTime = Long.parseLong(nowTimeStr);
                if (nowTime - preTime < interval * 1000L) {
                    unlock(key, identity);
                    return false;
                }
            }
            setString(key + "t", nowTimeStr);
            return true;
        }
        return false;
    }


}
