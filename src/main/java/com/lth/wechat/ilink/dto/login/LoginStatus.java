package com.lth.wechat.ilink.dto.login;

/**
 * 登录状态（用户友好版）
 */
public class LoginStatus {
    public enum Status {
        WAIT,     // 等待扫码
        SCANED,   // 已扫码
        CONFIRMED, // 已确认
        EXPIRED   // 已过期
    }

    private final Status status;
    private final String token;
    private final String botId;
    private final String userId;
    private final String apiBaseUrl;

    public LoginStatus(Status status, String token, String botId, String userId, String apiBaseUrl) {
        this.status = status;
        this.token = token;
        this.botId = botId;
        this.userId = userId;
        this.apiBaseUrl = apiBaseUrl;
    }

    public Status getStatus() {
        return status;
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

    public boolean isWaiting() {
        return status == Status.WAIT;
    }

    public boolean isScanned() {
        return status == Status.SCANED;
    }

    public boolean isConfirmed() {
        return status == Status.CONFIRMED;
    }

    public boolean isExpired() {
        return status == Status.EXPIRED;
    }

    public boolean isSuccess() {
        return status == Status.CONFIRMED;
    }

    public boolean hasCredentials() {
        return isConfirmed() && token != null;
    }
}