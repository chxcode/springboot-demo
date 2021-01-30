package com.cxcoder.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: ChangXuan
 * @Decription: 测试类
 * @Date: 16:18 2021/1/14
 **/
@SpringBootTest
class WorkServiceTest {

    private WorkService workService;

    @Autowired
    public void setWorkService(WorkService workService) {
        this.workService = workService;
    }

    @Test
    void printLogInfo() {
        workService.printLogInfo();
    }

    @Test
    void lockWork() throws Exception {
        CountDownLatch downLatch = new CountDownLatch(10);
        LinkedList<Thread> threads = new LinkedList<>();
        for (int i = 0; i < 10; ++i) {
            Thread thread = new Thread(() -> {
//                    workService.lockWorkV1();
//                    workService.lockWorkV2();
                    workService.lockWorkV3();
                    downLatch.countDown();

            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        downLatch.await();
        System.out.println("count = " + WorkService.count);
    }

    @Test
    void notLockWork() {
        LinkedList<Thread> threads = new LinkedList<>();
        for (int i = 0; i < 10; ++i) {
            Thread thread = new Thread(() -> workService.notLockWork());
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        System.out.println(WorkService.count);
    }


}