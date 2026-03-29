package com.lth.wechat.ilink.entity.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseInfo {
    @JsonProperty("channel_version")
    private String channelVersion;

    public String getChannelVersion() {
        return channelVersion;
    }

    public void setChannelVersion(String channelVersion) {
        this.channelVersion = channelVersion;
    }
}