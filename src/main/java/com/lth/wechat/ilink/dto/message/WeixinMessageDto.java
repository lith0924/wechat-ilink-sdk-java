package com.lth.wechat.ilink.dto.message;

import java.util.Date;
import java.util.List;

/**
 * 微信消息（用户友好版）
 */
public class WeixinMessageDto {
    public enum Type {
        USER,  // 用户消息
        BOT    // 机器人消息
    }

    public enum State {
        NEW,        // 新消息
        GENERATING, // 生成中
        FINISH      // 完成
    }

    private final long seq;
    private final long messageId;
    private final String fromUserId;
    private final String toUserId;
    private final String clientId;
    private final Date createTime;
    private final Date updateTime;
    private final Date deleteTime;
    private final String sessionId;
    private final String groupId;
    private final Type type;
    private final State state;
    private final List<MessageItemDto> item_list;
    private final String contextToken;

    public WeixinMessageDto(long seq, long messageId, String fromUserId, String toUserId, 
                           String clientId, Date createTime, Date updateTime, Date deleteTime,
                           String sessionId, String groupId, Type type, State state,
                           List<MessageItemDto> item_list, String contextToken) {
        this.seq = seq;
        this.messageId = messageId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.clientId = clientId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleteTime = deleteTime;
        this.sessionId = sessionId;
        this.groupId = groupId;
        this.type = type;
        this.state = state;
        this.item_list = item_list;
        this.contextToken = contextToken;
    }

    public long getSeq() {
        return seq;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getClientId() {
        return clientId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public List<MessageItemDto> getItemList() {
        return item_list;
    }

    public String getContextToken() {
        return contextToken;
    }

    public boolean isUserMessage() {
        return type == Type.USER;
    }

    public boolean isBotMessage() {
        return type == Type.BOT;
    }

    public boolean isNew() {
        return state == State.NEW;
    }

    public boolean isGenerating() {
        return state == State.GENERATING;
    }

    public boolean isFinish() {
        return state == State.FINISH;
    }

    public boolean hasItems() {
        return item_list != null && !item_list.isEmpty();
    }

    public MessageItemDto getFirstItem() {
        if (hasItems()) {
            return item_list.get(0);
        }
        return null;
    }
}