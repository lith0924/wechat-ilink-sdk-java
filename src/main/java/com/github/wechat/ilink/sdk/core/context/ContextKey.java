package com.github.wechat.ilink.sdk.core.context;

import java.util.Objects;

public final class ContextKey {
  private final String botId;
  private final String userId;

  public ContextKey(String botId, String userId) {
    this.botId = Objects.requireNonNull(botId);
    this.userId = Objects.requireNonNull(userId);
  }

  public String getBotId() {
    return botId;
  }

  public String getUserId() {
    return userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ContextKey)) return false;
    ContextKey k = (ContextKey) o;
    return botId.equals(k.botId) && userId.equals(k.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(botId, userId);
  }
}
