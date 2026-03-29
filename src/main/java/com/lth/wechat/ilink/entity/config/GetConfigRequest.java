package com.lth.wechat.ilink.entity.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetConfigRequest {
    @JsonProperty("ilink_user_id")
    private String ilinkUserId;
    
    @JsonProperty("context_token")
    private String contextToken;
    
    @JsonProperty("base_info")
    private BaseInfo baseInfo;

    public String getIlinkUserId() {
        return ilinkUserId;
    }

    public void setIlinkUserId(String ilinkUserId) {
        this.ilinkUserId = ilinkUserId;
    }

    public String getContextToken() {
        return contextToken;
    }

    public void setContextToken(String contextToken) {
        this.contextToken = contextToken;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}