package com.cxcoder.rabbitmq.consumer;

import com.cxcoder.rabbitmq.constant.RabbitConstants;
import com.cxcoder.rabbitmq.entity.UpdateUserMsg;
import com.cxcoder.rabbitmq.utils.JsonMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: ChangXuan
 * @Decription: 消费者
 * @Date: 14:21 2020/10/31
 **/
@RabbitListener(queues = RabbitConstants.QUEUE_THREE)
@Component
public class UpdateUserMsgReceiver {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserMsgReceiver.class);

    @RabbitHandler
    public void onUpdateUserMsg(
            UpdateUserMsg updateUserMsg
            , Message message
            , Channel channel) throws Exception {
        //  如果手动ACK,消息会被监听消费,但是消息在队列中依旧存在,如果 未配置 acknowledge-mode 默认是会在消费完毕后自动ACK掉
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("队列3，手动ACK，接收消息：{}", JsonMapper.objectToJson(updateUserMsg));
            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                // 处理失败,重新压入MQ
                channel.basicRecover();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

}
