package com.lth.wechat.ilink.entity.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessageResp {
    private int ret;
    public int getRet() { return ret; }
}