package com.github.wechat.ilink.sdk.core.context;

import com.github.wechat.ilink.sdk.core.login.LoginContext;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客户端恢复上下文，用于服务重启后恢复连接状态
 */
public class ResumeContext {
    private final LoginContext loginContext;
    private final String updatesCursor;
    private final Map<String, ConversationContext> conversationContexts;

    private ResumeContext(LoginContext loginContext) {
        this.loginContext = loginContext;
        this.updatesCursor = null;
        this.conversationContexts = Collections.emptyMap();
    }

    private ResumeContext(
        LoginContext loginContext,
        String updatesCursor,
        Map<String, ConversationContext> conversationContexts) {
        this.loginContext = loginContext;
        this.updatesCursor = updatesCursor;
        this.conversationContexts = snapshotContexts(conversationContexts);
    }

    public static ResumeContext of(LoginContext loginContext) {
        return new ResumeContext(loginContext);
    }

    public static Builder builder(LoginContext loginContext) {
        return new Builder(loginContext);
    }

    public LoginContext getLoginContext() {
        return loginContext;
    }

    public String getUpdatesCursor() {
        return updatesCursor;
    }

    public Collection<ConversationContext> getConversationContexts() {
        return conversationContexts.values();
    }

    public Map<String, ConversationContext> getConversationContextMap() {
        return conversationContexts;
    }

    private static Map<String, ConversationContext> snapshotContexts(
        Map<String, ConversationContext> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, ConversationContext> snapshots = new LinkedHashMap<String, ConversationContext>();
        for (Map.Entry<String, ConversationContext> entry : contexts.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            snapshots.put(entry.getKey(), entry.getValue().snapshot());
        }
        return Collections.unmodifiableMap(snapshots);
    }

    public static final class Builder {
        private final LoginContext loginContext;
        private String updatesCursor;
        private Map<String, ConversationContext> conversationContexts;

        private Builder(LoginContext loginContext) {
            this.loginContext = loginContext;
        }

        public Builder updatesCursor(String updatesCursor) {
            this.updatesCursor = updatesCursor;
            return this;
        }

        public Builder conversationContexts(Map<String, ConversationContext> conversationContexts) {
            this.conversationContexts = conversationContexts;
            return this;
        }

        public ResumeContext build() {
            return new ResumeContext(loginContext, updatesCursor, conversationContexts);
        }
    }
}