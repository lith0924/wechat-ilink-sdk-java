package com.github.wechat.ilink.sdk.service;

import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.context.*;
import com.github.wechat.ilink.sdk.core.http.BusinessApiClient;
import com.github.wechat.ilink.sdk.core.login.LoginContext;
import com.github.wechat.ilink.sdk.core.model.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UpdateService {
  private final ILinkConfig config;
  private final BusinessApiClient apiClient;
  private final GetUpdatesCursorStore cursorStore;
  private final ContextPoolManager contextPoolManager;

  public UpdateService(
      ILinkConfig config,
      BusinessApiClient apiClient,
      GetUpdatesCursorStore cursorStore,
      ContextPoolManager contextPoolManager) {
    this.config = config;
    this.apiClient = apiClient;
    this.cursorStore = cursorStore;
    this.contextPoolManager = contextPoolManager;
  }

  public List<WeixinMessage> poll(LoginContext loginContext) throws IOException {
    String cursor = cursorStore.get();
    if (cursor == null) cursor = "";
    GetUpdatesResponse resp =
        apiClient.post(
            loginContext,
            "/ilink/bot/getupdates",
            new GetUpdatesRequest(cursor, new BaseInfo(config.getChannelVersion())),
            GetUpdatesResponse.class);
    if (resp.getGet_updates_buf() != null)
      cursorStore.put(resp.getGet_updates_buf());
    List<WeixinMessage> msgs = resp.getMsgs();
    if (msgs == null) return Collections.<WeixinMessage>emptyList();
    for (WeixinMessage msg : msgs) {
      if (msg.getFrom_user_id() != null
          && msg.getContext_token() != null
          && !msg.getContext_token().trim().isEmpty()) {
        contextPoolManager
            .getOrCreate(loginContext.getBotId(), msg.getFrom_user_id())
            .updateContextToken(
                msg.getContext_token(), msg.getMessage_id(), msg.getCreate_time_ms());
      }
    }
    return msgs;
  }
}
