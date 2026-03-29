package com.lth.wechat.ilink.entity.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetConfigResp {
    private int ret;
    private String typing_ticket;
    public int getRet() { return ret; }
    public String getTyping_ticket() { return typing_ticket; }
}