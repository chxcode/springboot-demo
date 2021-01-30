package com.cxcoder.rabbitmq.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.cxcoder.rabbitmq.entity.UpdateUserMsg;
import com.cxcoder.rabbitmq.entity.User;
import com.cxcoder.rabbitmq.producer.UpdateUserMsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: ChangXuan
 * @Decription: 发送消息接口
 * @Date: 13:49 2020/10/31
 **/
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    private UpdateUserMsgSender updateUserMsgSender;

    @Autowired
    public void setUpdateUserMsgSender(UpdateUserMsgSender updateUserMsgSender) {
        this.updateUserMsgSender = updateUserMsgSender;
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Object> update(@RequestBody User user) throws Exception{
        // 更新用户信息
        // ... ...
        // 构造通知消息体
        UpdateUserMsg updateUserMsg = new UpdateUserMsg(RandomUtil.randomInt(), user.getId().toString(), IdUtil.simpleUUID());
        updateUserMsgSender.send(updateUserMsg);
        return new ResponseEntity<>("{\"status\":\"ok\"}",HttpStatus.OK);
    }
}
