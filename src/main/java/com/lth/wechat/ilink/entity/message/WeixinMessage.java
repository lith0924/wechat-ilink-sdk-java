package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeixinMessage {
    private long seq;
    private long message_id;
    private String from_user_id;
    private String to_user_id;
    private String client_id;
    private long create_time_ms;
    private long update_time_ms;
    private long delete_time_ms;
    private String session_id;
    private String group_id;
    private int message_type;
    private int message_state;
    private List<MessageItem> item_list;
    private String context_token;

    public long getSeq() { return seq; }
    public long getMessage_id() { return message_id; }
    public String getFrom_user_id() { return from_user_id; }
    public String getTo_user_id() { return to_user_id; }
    public String getClient_id() { return client_id; }
    public long getCreate_time_ms() { return create_time_ms; }
    public long getUpdate_time_ms() { return update_time_ms; }
    public long getDelete_time_ms() { return delete_time_ms; }
    public String getSession_id() { return session_id; }
    public String getGroup_id() { return group_id; }
    public int getMessage_type() { return message_type; }
    public int getMessage_state() { return message_state; }
    public List<MessageItem> getItem_list() { return item_list; }
    public String getContext_token() { return context_token; }
}