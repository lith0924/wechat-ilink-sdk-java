package com.lth.wechat.ilink.dto.message;

/**
 * 引用消息
 */
public class RefMessage {
    private final String title;
    private final MessageItemDto messageItem;

    public RefMessage(String title, MessageItemDto messageItem) {
        this.title = title;
        this.messageItem = messageItem;
    }

    public String getTitle() {
        return title;
    }

    public MessageItemDto getMessageItem() {
        return messageItem;
    }
}