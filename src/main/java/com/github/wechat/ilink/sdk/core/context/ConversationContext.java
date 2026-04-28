package com.github.wechat.ilink.sdk.core.context;

public class ConversationContext {
  private final ContextKey key;
  private volatile String latestContextToken;
  private volatile String typingTicket;
  private volatile long lastUpdatedAt;
  private volatile Long sourceMessageId;
  private volatile Long sourceMessageTime;

  public ConversationContext(ContextKey key) {
    this.key = key;
    this.lastUpdatedAt = System.currentTimeMillis();
  }

  public synchronized void updateContextToken(String token, Long messageId, Long messageTime) {
    if (token == null || token.trim().isEmpty()) return;
    this.latestContextToken = token;
    this.sourceMessageId = messageId;
    this.sourceMessageTime = messageTime;
    this.lastUpdatedAt = System.currentTimeMillis();
  }

  public synchronized ConversationContext snapshot() {
    ConversationContext copy = new ConversationContext(key);
    copy.latestContextToken = latestContextToken;
    copy.typingTicket = typingTicket;
    copy.lastUpdatedAt = lastUpdatedAt;
    copy.sourceMessageId = sourceMessageId;
    copy.sourceMessageTime = sourceMessageTime;
    return copy;
  }

  public synchronized void clearEphemeral() {
    latestContextToken = null;
    typingTicket = null;
    sourceMessageId = null;
    sourceMessageTime = null;
    lastUpdatedAt = System.currentTimeMillis();
  }

  public ContextKey getKey() {
    return key;
  }

  public String getLatestContextToken() {
    return latestContextToken;
  }

  public void setLatestContextToken(String v) {
    this.latestContextToken = v;
    this.lastUpdatedAt = System.currentTimeMillis();
  }

  public String getTypingTicket() {
    return typingTicket;
  }

  public void setTypingTicket(String v) {
    this.typingTicket = v;
    this.lastUpdatedAt = System.currentTimeMillis();
  }

  public long getLastUpdatedAt() {
    return lastUpdatedAt;
  }

  public Long getSourceMessageId() {
    return sourceMessageId;
  }

  public Long getSourceMessageTime() {
    return sourceMessageTime;
  }

  public boolean hasContextToken() {
    return latestContextToken != null && !latestContextToken.trim().isEmpty();
  }
}
