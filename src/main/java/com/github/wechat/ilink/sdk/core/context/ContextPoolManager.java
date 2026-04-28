package com.github.wechat.ilink.sdk.core.context;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContextPoolManager {
  private final Map<ContextKey, ConversationContext> pool =
      new ConcurrentHashMap<ContextKey, ConversationContext>();

  public ConversationContext getOrCreate(String botId, String userId) {
    ContextKey key = new ContextKey(botId, userId);
    ConversationContext ctx = pool.get(key);
    if (ctx != null) return ctx;
    ConversationContext created = new ConversationContext(key);
    ConversationContext prev = pool.putIfAbsent(key, created);
    return prev == null ? created : prev;
  }

  public ConversationContext get(String botId, String userId) {
    return pool.get(new ContextKey(botId, userId));
  }

  public void remove(String botId, String userId) {
    ConversationContext ctx = pool.remove(new ContextKey(botId, userId));
    if (ctx != null) ctx.clearEphemeral();
  }

  public void clearAll() {
    for (ConversationContext ctx : pool.values()) ctx.clearEphemeral();
    pool.clear();
  }

  public void clearByBotId(String botId) {
    for (Map.Entry<ContextKey, ConversationContext> e : pool.entrySet())
      if (e.getKey().getBotId().equals(botId)) {
        e.getValue().clearEphemeral();
        pool.remove(e.getKey());
      }
  }

  public void restore(Collection<ConversationContext> contexts) {
    if (contexts == null) return;
    for (ConversationContext ctx : contexts) {
      if (ctx == null || ctx.getKey() == null) continue;
      pool.put(ctx.getKey(), ctx.snapshot());
    }
  }

  public Map<String, ConversationContext> snapshotByUserId() {
    Map<String, ConversationContext> snapshots = new LinkedHashMap<String, ConversationContext>();
    for (ConversationContext ctx : pool.values()) {
      if (ctx != null && ctx.getKey() != null && ctx.getKey().getUserId() != null) {
        snapshots.put(ctx.getKey().getUserId(), ctx.snapshot());
      }
    }
    return Collections.unmodifiableMap(snapshots);
  }

  public Collection<ConversationContext> values() {
    return pool.values();
  }
}
