package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUpdatesResponse {
    private int ret;
    private List<WeixinMessage> msgs;
    private String sync_buf;
    private String get_updates_buf;
    private long longpolling_timeout_ms;

    public int getRet() { return ret; }
    public List<WeixinMessage> getMsgs() { return msgs; }
    public String getSync_buf() { return sync_buf; }
    public String getGet_updates_buf() { return get_updates_buf; }
    public long getLongpolling_timeout_ms() { return longpolling_timeout_ms; }
}