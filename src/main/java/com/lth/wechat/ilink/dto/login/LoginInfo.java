package com.lth.wechat.ilink.dto.login;

/**
 * 登录状态信息（用户友好版）
 * 存储登录成功后的凭证信息
 */
public class LoginInfo {
    private final String token;
    private final String botId;
    private final String userId;
    private final String apiBaseUrl;

    public LoginInfo(String token, String botId, String userId, String apiBaseUrl) {
        this.token = token;
        this.botId = botId;
        this.userId = userId;
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getToken() {
        return token;
    }

    public String getBotId() {
        return botId;
    }

    public String getUserId() {
        return userId;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }
}