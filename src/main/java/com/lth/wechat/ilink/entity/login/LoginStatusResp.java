package com.lth.wechat.ilink.entity.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginStatusResp {
    private String status;
    private String bot_token;
    private String ilink_bot_id;
    private String ilink_user_id;
    private String baseurl;
    public String getStatus() { return status; }
    public String getBot_token() { return bot_token; }
    public String getIlink_bot_id() { return ilink_bot_id; }
    public String getIlink_user_id() { return ilink_user_id; }
    public String getBaseurl() { return baseurl; }
}