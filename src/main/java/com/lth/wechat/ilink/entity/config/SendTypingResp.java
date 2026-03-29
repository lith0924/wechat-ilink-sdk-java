package com.lth.wechat.ilink.entity.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendTypingResp {
    private int ret;
    public int getRet() { return ret; }
}