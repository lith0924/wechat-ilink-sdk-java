package com.github.wechat.ilink.sdk.core.model;

public class RefMessage {
    private MessageItem message_item;
    private String title;
    private String text;

    public RefMessage() {}

    public RefMessage(MessageItem messageItem, String title, String text) {
        this.message_item = messageItem;
        this.title = title;
        this.text = text;
    }

    public MessageItem getMessage_item() {
        return message_item;
    }

    public void setMessage_item(MessageItem v) {
        message_item = v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

    public String getText() {
        if (text != null && !text.isEmpty()) {
            return text;
        }
        if (message_item != null && message_item.getText_item() != null && message_item.getText_item().getText() != null) {
            return message_item.getText_item().getText();
        }
        return title;
    }

    public void setText(String v) {
        text = v;
    }
}
