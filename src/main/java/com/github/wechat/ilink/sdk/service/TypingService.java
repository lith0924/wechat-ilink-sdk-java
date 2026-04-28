package com.github.wechat.ilink.sdk.service;

import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.context.ContextPoolManager;
import com.github.wechat.ilink.sdk.core.context.ConversationContext;
import com.github.wechat.ilink.sdk.core.http.BusinessApiClient;
import com.github.wechat.ilink.sdk.core.login.LoginContext;
import com.github.wechat.ilink.sdk.core.model.*;
import java.io.IOException;

public class TypingService {
  private final ILinkConfig config;
  private final BusinessApiClient apiClient;
  private final ContextPoolManager contextPoolManager;

  public TypingService(
      ILinkConfig config, BusinessApiClient apiClient, ContextPoolManager contextPoolManager) {
    this.config = config;
    this.apiClient = apiClient;
    this.contextPoolManager = contextPoolManager;
  }

  public String ensureTypingTicket(LoginContext loginContext, String userId) throws IOException {
    ConversationContext ctx = contextPoolManager.getOrCreate(loginContext.getBotId(), userId);
    if (ctx.getTypingTicket() != null) return ctx.getTypingTicket();
    GetConfigResponse resp =
        apiClient.post(
            loginContext,
            "/ilink/bot/getconfig",
            new GetConfigRequest(
                userId, ctx.getLatestContextToken(), new BaseInfo(config.getChannelVersion())),
            GetConfigResponse.class);
    ctx.setTypingTicket(resp.getTyping_ticket());
    return resp.getTyping_ticket();
  }

  public void startTyping(LoginContext loginContext, String userId) throws IOException {
    apiClient.post(
        loginContext,
        "/ilink/bot/sendtyping",
        new SendTypingRequest(
            userId,
            ensureTypingTicket(loginContext, userId),
            1,
            new BaseInfo(config.getChannelVersion())),
        ApiResponse.class);
  }

  public void stopTyping(LoginContext loginContext, String userId) throws IOException {
    ConversationContext ctx = contextPoolManager.getOrCreate(loginContext.getBotId(), userId);
    if (ctx.getTypingTicket() == null) return;
    apiClient.post(
        loginContext,
        "/ilink/bot/sendtyping",
        new SendTypingRequest(
            userId, ctx.getTypingTicket(), 2, new BaseInfo(config.getChannelVersion())),
        ApiResponse.class);
  }
}
