package com.lth.wechat.ilink.dto.message;

import java.util.List;

/**
 * 接收消息结果（用户友好版）
 */
public class ReceiveMessagesResult {
    private final List<WeixinMessageDto> messages;
    private final String nextCursor;
    private final long longpollingTimeoutMs;

    private String currentCursor;

    public ReceiveMessagesResult(List<WeixinMessageDto> messages, String nextCursor, long longpollingTimeoutMs) {
        this.messages = messages;
        this.nextCursor = nextCursor;
        this.longpollingTimeoutMs = longpollingTimeoutMs;
    }

    public List<WeixinMessageDto> getMessages() {
        return messages;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public long getLongpollingTimeoutMs() {
        return longpollingTimeoutMs;
    }

    public String getCurrentCursor() {
        return currentCursor;
    }

    public void setCurrentCursor(String cursor) {
        this.currentCursor = cursor;
    }

    public boolean hasMessages() {
        return messages != null && !messages.isEmpty();
    }

    public int getMessageCount() {
        return messages != null ? messages.size() : 0;
    }
}