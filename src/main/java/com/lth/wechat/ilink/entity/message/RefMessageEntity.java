package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefMessageEntity {
    private String title;
    private MessageItem message_item;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MessageItem getMessage_item() {
        return message_item;
    }

    public void setMessage_item(MessageItem message_item) {
        this.message_item = message_item;
    }
}