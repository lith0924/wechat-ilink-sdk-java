package com.lth.wechat.ilink;

/**
 * 登录凭证类
 * 封装登录成功后的所有凭证信息，用于无状态设计
 * @author lth
 * @version 1.0.0
 */
public class LoginCredentials {
    private final String botToken;
    private final String userId;
    private final String apiBaseUrl;
    private final String qrcode;

    public LoginCredentials(String botToken, String userId, String apiBaseUrl) {
        this(botToken, userId, apiBaseUrl, null);
    }

    public LoginCredentials(String botToken, String userId, String apiBaseUrl, String qrcode) {
        this.botToken = botToken;
        this.userId = userId;
        this.apiBaseUrl = apiBaseUrl;
        this.qrcode = qrcode;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public String getQrcode() {
        return qrcode;
    }

    public boolean isLoggedIn() {
        return botToken != null;
    }
}