package com.github.wechat.ilink.sdk.core.config;

public final class ILinkConfig {
  private final long connectTimeoutMs;
  private final long readTimeoutMs;
  private final long writeTimeoutMs;
  private final int httpMaxRetries;
  private final long retryBaseDelayMs;
  private final long retryMaxDelayMs;
  private final boolean retryJitterEnabled;
  private final long loginTimeoutMs;
  private final boolean heartbeatEnabled;
  private final long heartbeatIntervalMs;

  private final int ioCoreThreads;
  private final int ioMaxThreads;
  private final int schedulerThreads;
  private final int queueCapacity;
  private final String channelVersion;
//  好像没用
  private final String routeTag;
//  好像不太适用
  private final boolean autoReconnectEnabled;
  private final int reconnectMaxAttempts;
  private final long reconnectBaseDelayMs;
  private final long reconnectMaxDelayMs;

  private ILinkConfig(Builder b) {
    this.connectTimeoutMs = b.connectTimeoutMs;
    this.readTimeoutMs = b.readTimeoutMs;
    this.writeTimeoutMs = b.writeTimeoutMs;
    this.httpMaxRetries = b.httpMaxRetries;
    this.retryBaseDelayMs = b.retryBaseDelayMs;
    this.retryMaxDelayMs = b.retryMaxDelayMs;
    this.retryJitterEnabled = b.retryJitterEnabled;
    this.loginTimeoutMs = b.loginTimeoutMs;
    this.heartbeatEnabled = b.heartbeatEnabled;
    this.heartbeatIntervalMs = b.heartbeatIntervalMs;
    this.reconnectMaxAttempts = b.reconnectMaxAttempts;
    this.reconnectBaseDelayMs = b.reconnectBaseDelayMs;
    this.reconnectMaxDelayMs = b.reconnectMaxDelayMs;
    this.ioCoreThreads = b.ioCoreThreads;
    this.ioMaxThreads = b.ioMaxThreads;
    this.schedulerThreads = b.schedulerThreads;
    this.queueCapacity = b.queueCapacity;
    this.channelVersion = b.channelVersion;
    this.routeTag = b.routeTag;
    this.autoReconnectEnabled = b.autoReconnectEnabled;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private long connectTimeoutMs = 35000L;
    private long readTimeoutMs = 35000L;
    private long writeTimeoutMs = 35000L;
    private int httpMaxRetries = 3;
    private long retryBaseDelayMs = 1000L;
    private long retryMaxDelayMs = 10000L;
    private boolean retryJitterEnabled = true;
    private long loginTimeoutMs = 180000L;
    private boolean heartbeatEnabled = true;
    private long heartbeatIntervalMs = 30000L;
    private int reconnectMaxAttempts = 5;
    private long reconnectBaseDelayMs = 1000L;
    private long reconnectMaxDelayMs = 30000L;
    private int ioCoreThreads = 4;
    private int ioMaxThreads = 8;
    private int schedulerThreads = 2;
    private int queueCapacity = 1000;
    private String channelVersion = "1.0.0";
    private String routeTag;
    private boolean autoReconnectEnabled = true;

    public Builder connectTimeoutMs(long v) {
      this.connectTimeoutMs = v;
      return this;
    }

    public Builder readTimeoutMs(long v) {
      this.readTimeoutMs = v;
      return this;
    }

    public Builder writeTimeoutMs(long v) {
      this.writeTimeoutMs = v;
      return this;
    }

    public Builder httpMaxRetries(int v) {
      this.httpMaxRetries = v;
      return this;
    }

    public Builder retryBaseDelayMs(long v) {
      this.retryBaseDelayMs = v;
      return this;
    }

    public Builder retryMaxDelayMs(long v) {
      this.retryMaxDelayMs = v;
      return this;
    }

    public Builder retryJitterEnabled(boolean v) {
      this.retryJitterEnabled = v;
      return this;
    }

    public Builder loginTimeoutMs(long v) {
      this.loginTimeoutMs = v;
      return this;
    }

    public Builder heartbeatEnabled(boolean v) {
      this.heartbeatEnabled = v;
      return this;
    }

    public Builder heartbeatIntervalMs(long v) {
      this.heartbeatIntervalMs = v;
      return this;
    }

    public Builder reconnectMaxAttempts(int v) {
      this.reconnectMaxAttempts = v;
      return this;
    }

    public Builder reconnectBaseDelayMs(long v) {
      this.reconnectBaseDelayMs = v;
      return this;
    }

    public Builder reconnectMaxDelayMs(long v) {
      this.reconnectMaxDelayMs = v;
      return this;
    }

    public Builder ioCoreThreads(int v) {
      this.ioCoreThreads = v;
      return this;
    }

    public Builder ioMaxThreads(int v) {
      this.ioMaxThreads = v;
      return this;
    }

    public Builder schedulerThreads(int v) {
      this.schedulerThreads = v;
      return this;
    }

    public Builder queueCapacity(int v) {
      this.queueCapacity = v;
      return this;
    }

    public Builder channelVersion(String v) {
      this.channelVersion = v;
      return this;
    }

    public Builder routeTag(String v) {
      this.routeTag = v;
      return this;
    }

    public Builder autoReconnectEnabled(boolean v) {
      this.autoReconnectEnabled = v;
      return this;
    }

    public ILinkConfig build() {
      return new ILinkConfig(this);
    }
  }

  public long getConnectTimeoutMs() {
    return connectTimeoutMs;
  }

  public long getReadTimeoutMs() {
    return readTimeoutMs;
  }

  public long getWriteTimeoutMs() {
    return writeTimeoutMs;
  }

  public int getHttpMaxRetries() {
    return httpMaxRetries;
  }

  public long getRetryBaseDelayMs() {
    return retryBaseDelayMs;
  }

  public long getRetryMaxDelayMs() {
    return retryMaxDelayMs;
  }

  public boolean isRetryJitterEnabled() {
    return retryJitterEnabled;
  }

  public long getLoginTimeoutMs() {
    return loginTimeoutMs;
  }

  public boolean isHeartbeatEnabled() {
    return heartbeatEnabled;
  }

  public long getHeartbeatIntervalMs() {
    return heartbeatIntervalMs;
  }

  public int getReconnectMaxAttempts() {
    return reconnectMaxAttempts;
  }

  public long getReconnectBaseDelayMs() {
    return reconnectBaseDelayMs;
  }

  public long getReconnectMaxDelayMs() {
    return reconnectMaxDelayMs;
  }

  public int getIoCoreThreads() {
    return ioCoreThreads;
  }

  public int getIoMaxThreads() {
    return ioMaxThreads;
  }

  public int getSchedulerThreads() {
    return schedulerThreads;
  }

  public int getQueueCapacity() {
    return queueCapacity;
  }

  public String getChannelVersion() {
    return channelVersion;
  }

  public String getRouteTag() {
    return routeTag;
  }

  public boolean isAutoReconnectEnabled() {
    return autoReconnectEnabled;
  }
}
