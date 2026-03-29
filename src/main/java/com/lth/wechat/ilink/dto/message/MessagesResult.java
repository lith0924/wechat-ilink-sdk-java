package com.lth.wechat.ilink.dto.message;

import java.util.List;

/**
 * 消息列表结果（用户友好版）
 */
public class MessagesResult {
    private final List<Message> messages;
    private final String nextBuffer;
    private final long timeout;

    public MessagesResult(List<Message> messages, String nextBuffer, long timeout) {
        this.messages = messages;
        this.nextBuffer = nextBuffer;
        this.timeout = timeout;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getNextBuffer() {
        return nextBuffer;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean hasMessages() {
        return messages != null && !messages.isEmpty();
    }
}