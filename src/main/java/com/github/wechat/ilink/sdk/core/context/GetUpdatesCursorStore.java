package com.github.wechat.ilink.sdk.core.context;

public final class GetUpdatesCursorStore {
  private volatile String cursor = "";

  public String get() {
    return cursor;
  }

  public void put(String cursor) {
    this.cursor = cursor == null ? "" : cursor;
  }

  public void clear() {
    this.cursor = "";
  }
}
