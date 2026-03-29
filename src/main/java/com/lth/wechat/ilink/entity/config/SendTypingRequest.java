package com.lth.wechat.ilink.entity.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendTypingRequest {
    @JsonProperty("ilink_user_id")
    private String ilinkUserId;
    
    @JsonProperty("typing_ticket")
    private String typingTicket;
    
    @JsonProperty("status")
    private int status;
    
    @JsonProperty("base_info")
    private BaseInfo baseInfo;

    public String getIlinkUserId() {
        return ilinkUserId;
    }

    public void setIlinkUserId(String ilinkUserId) {
        this.ilinkUserId = ilinkUserId;
    }

    public String getTypingTicket() {
        return typingTicket;
    }

    public void setTypingTicket(String typingTicket) {
        this.typingTicket = typingTicket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}