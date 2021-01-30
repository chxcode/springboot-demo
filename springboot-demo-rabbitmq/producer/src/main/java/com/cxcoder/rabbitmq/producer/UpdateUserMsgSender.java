package com.cxcoder.rabbitmq.producer;

import com.cxcoder.rabbitmq.constant.RabbitConstants;
import com.cxcoder.rabbitmq.entity.UpdateUserMsg;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ChangXuan
 * @Decription: 发送用户修改个人资料消息
 * @Date: 13:44 2020/10/31
 **/
@Component
public class UpdateUserMsgSender {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(UpdateUserMsg updateUserMsg) throws Exception {
        rabbitTemplate.convertAndSend(RabbitConstants.TOPIC_MODE_QUEUE, "3.queue", updateUserMsg);
    }

}
