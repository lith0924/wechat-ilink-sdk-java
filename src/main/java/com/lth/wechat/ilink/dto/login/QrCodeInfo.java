package com.lth.wechat.ilink.dto.login;

/**
 * 二维码信息（用户友好版）
 */
public class QrCodeInfo {
    private final String qrcodeId;
    private final String imageUrl;

    public QrCodeInfo(String qrcodeId, String imageUrl) {
        this.qrcodeId = qrcodeId;
        this.imageUrl = imageUrl;
    }

    public String getQrcodeId() {
        return qrcodeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}