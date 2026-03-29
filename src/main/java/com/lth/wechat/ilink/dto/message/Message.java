package com.lth.wechat.ilink.dto.message;

import java.util.List;

/**
 * 消息信息（用户友好版）
 */
public class Message {
    private final long id;
    private final String fromUserId;
    private final String toUserId;
    private final long createTime;
    private final String sessionId;
    private final String contextToken;
    private final List<MessageContent> contents;

    public Message(long id, String fromUserId, String toUserId, long createTime, 
                   String sessionId, String contextToken, List<MessageContent> contents) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.createTime = createTime;
        this.sessionId = sessionId;
        this.contextToken = contextToken;
        this.contents = contents;
    }

    public long getId() {
        return id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getContextToken() {
        return contextToken;
    }

    public List<MessageContent> getContents() {
        return contents;
    }

    /**
     * 获取文本内容（如果是文本消息）
     */
    public String getText() {
        if (contents != null && !contents.isEmpty()) {
            MessageContent content = contents.get(0);
            if (content instanceof TextContent) {
                return ((TextContent) content).getText();
            }
        }
        return null;
    }
}