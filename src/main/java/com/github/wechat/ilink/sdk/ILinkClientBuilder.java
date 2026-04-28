package com.github.wechat.ilink.sdk;

import com.github.wechat.ilink.sdk.core.config.ConfigLoader;
import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.context.ResumeContext;
import com.github.wechat.ilink.sdk.core.listener.*;
import com.github.wechat.ilink.sdk.core.login.LoginContext;

public class ILinkClientBuilder {
  private ILinkConfig config = ConfigLoader.loadDefault();
  private final ListenerRegistry listenerRegistry = new ListenerRegistry();
//  保证持久化，服务重启后，可以重新拉取客户端实例
  private ResumeContext resumeContext;

  public ILinkClientBuilder config(ILinkConfig config) {
    this.config = config;
    return this;
  }

  public ILinkClientBuilder resumeContext(ResumeContext resumeContext){
    this.resumeContext = resumeContext;
    return this;
  }

  public ILinkClientBuilder loginContext(LoginContext loginContext) {
    this.resumeContext = loginContext == null ? null : ResumeContext.of(loginContext);
    return this;
  }

  public ILinkClientBuilder onLogin(OnLoginListener l) {
    listenerRegistry.addOnLoginListener(l);
    return this;
  }

  public ILinkClientBuilder onDisconnect(OnDisconnectListener l) {
    listenerRegistry.addOnDisconnectListener(l);
    return this;
  }

  public ILinkClientBuilder onHeartbeat(OnHeartbeatListener l) {
    listenerRegistry.addOnHeartbeatListener(l);
    return this;
  }

  public ILinkClientBuilder onMessage(OnMessageListener l) {
    listenerRegistry.addOnMessageListener(l);
    return this;
  }

  public ILinkClient build() {
    return new ILinkClient(config, listenerRegistry,resumeContext);
  }
}
