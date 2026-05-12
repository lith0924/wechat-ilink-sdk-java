package com.github.wechat.ilink.sdk.core.model;

import java.util.List;

public class WeixinMessage {
  private Long message_id;
  private Integer message_type;
  private String from_user_id;
  private String to_user_id;
  private Long create_time_ms;
  private String context_token;
  private List<MessageItem> item_list;

  public Long getMessage_id() {
    return message_id;
  }

  public void setMessage_id(Long v) {
    message_id = v;
  }

  public Integer getMessage_type() {
    return message_type;
  }

  public void setMessage_type(Integer v) {
    message_type = v;
  }

  public String getFrom_user_id() {
    return from_user_id;
  }

  public void setFrom_user_id(String v) {
    from_user_id = v;
  }

  public String getTo_user_id() {
    return to_user_id;
  }

  public void setTo_user_id(String v) {
    to_user_id = v;
  }

  public Long getCreate_time_ms() {
    return create_time_ms;
  }

  public void setCreate_time_ms(Long v) {
    create_time_ms = v;
  }

  public String getContext_token() {
    return context_token;
  }

  public void setContext_token(String v) {
    context_token = v;
  }

  public List<MessageItem> getItem_list() {
    return item_list;
  }

  public void setItem_list(List<MessageItem> v) {
    item_list = v;
  }
}
