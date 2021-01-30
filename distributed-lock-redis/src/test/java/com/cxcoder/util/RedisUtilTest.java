package com.cxcoder.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: ChangXuan
 * @Decription: 工具类测试
 * @Date: 16:43 2021/1/14
 **/
@SpringBootTest
class RedisUtilTest {

    private RedisUtil redisUtil;

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Test
    void setString() {
        Assert.isTrue(redisUtil.lock("ChangXuan", "good", 1000), "设置失败");
    }
}