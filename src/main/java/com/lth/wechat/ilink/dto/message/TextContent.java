package com.lth.wechat.ilink.dto.message;

/**
 * 文本消息内容
 */
public class TextContent extends MessageContent {
    private final String text;

    public TextContent(String text) {
        super(Type.TEXT);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}