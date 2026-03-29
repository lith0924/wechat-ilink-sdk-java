package com.lth.wechat.ilink.dto.message;

/**
 * CDN媒体引用
 */
public class CDNMedia {
    private final String encryptQueryParam;
    private final String aesKey;
    private final int encryptType;

    public CDNMedia(String encryptQueryParam, String aesKey, int encryptType) {
        this.encryptQueryParam = encryptQueryParam;
        this.aesKey = aesKey;
        this.encryptType = encryptType;
    }

    public String getEncryptQueryParam() {
        return encryptQueryParam;
    }

    public String getAesKey() {
        return aesKey;
    }

    public int getEncryptType() {
        return encryptType;
    }
}