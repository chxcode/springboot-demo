package com.cxcoder.rabbitmq.entity;

import java.io.Serializable;

/**
 * @Author: ChangXuan
 * @Decription: 用户信息更新消息
 * @Date: 13:41 2020/10/31
 **/
public class UpdateUserMsg implements Serializable {

    private static final long serialVersionUID = 392365881428311040L;

    private Integer id;

    private String accountId;

    private String messageId;

    public UpdateUserMsg() {
    }

    public UpdateUserMsg(Integer id, String accountId, String messageId) {
        this.id = id;
        this.accountId = accountId;
        this.messageId = messageId;
    }

    public Integer getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
