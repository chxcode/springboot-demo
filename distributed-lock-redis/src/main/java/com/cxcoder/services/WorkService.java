package com.cxcoder.services;

import cn.hutool.core.util.StrUtil;
import com.cxcoder.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ChangXuan
 * @Decription:
 * @Date: 16:16 2021/1/14
 **/
@Service
@Slf4j
public class WorkService {

    public static int count = 0;

    private static final int TIME = 1000;

    private RedisUtil redisUtil;

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public void printLogInfo() {
        log.info("一般日志");
        log.warn("警告日志");
        log.error("错误日志");
    }

    /**
     * 示范错误加锁方式 <p>
     * 会导致其它线程释放掉正在持有锁的线程所持有的锁
     */
    public void lockWorkV1(){
        String key = "TEST_KEY_ONE";
        try {
            if (redisUtil.lock(key)){
                log.info("线程：{}，获取锁", Thread.currentThread().getName());
                increment();
                Thread.sleep(10000);
            }
        }catch (Exception e) {
            log.error("发生错误",e);
        }finally {
            redisUtil.unlock(key);
            log.info("线程：{}，释放锁", Thread.currentThread().getName());
        }
    }

    /**
     * 使用第一个版本锁
     */
    public void lockWorkV2() {
        String key = "TEST_KEY_TWO";
        if (redisUtil.lock(key)) {
            log.info("线程：{},已经获取锁", Thread.currentThread().getName());
            try {
                increment();
            }catch (Exception e) {
                log.error("发生错误",e);
            }finally {
                redisUtil.unlock(key);
                log.info("线程：{},已经释放锁", Thread.currentThread().getName());
            }
        } else {
            log.info("线程：{},未获取到锁", Thread.currentThread().getName());
        }
    }

    /**
     * 使用第二个版本锁（阻塞）
     */
    public void lockWorkV3() {
        String key = "TEST_KEY_THREE";
        String identity = StrUtil.uuid();
        int timeout = 20;
        if (redisUtil.lockb(key, identity, timeout)) {
            try {
                increment();
            }catch (Exception e) {
                log.error("发生错误",e);
            }finally {
                redisUtil.unlock(key, identity);
            }
        } else {
            log.info("线程：{},未获取到锁", Thread.currentThread().getName());
        }
    }



    private void increment() {
        for (int i = 0; i < TIME; i++) {
            WorkService.count++;
        }
    }

    /**
     * 不使用锁
     */
    public void notLockWork() {
        for (int i = 0; i < TIME; i++) {
            WorkService.count++;
        }
    }

}
