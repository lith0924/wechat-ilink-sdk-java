package com.lth.wechat.ilink.dto.message;

/**
 * 消息内容抽象类
 */
public abstract class MessageContent {
    
    public enum Type {
        TEXT, IMAGE, VOICE, FILE, VIDEO
    }
    
    private final Type type;
    
    protected MessageContent(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
}