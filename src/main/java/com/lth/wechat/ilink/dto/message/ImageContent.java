package com.lth.wechat.ilink.dto.message;

/**
 * 图片消息内容
 */
public class ImageContent extends MessageContent {
    private final String encryptQueryParam;
    private final String aesKey;
    private final int encryptType;
    private final String url;
    private final long midSize;
    private final long thumbSize;
    private final int thumbWidth;
    private final int thumbHeight;
    private final long hdSize;

    public ImageContent(String encryptQueryParam, String aesKey, int encryptType, String url, 
                       long midSize, long thumbSize, int thumbWidth, int thumbHeight, long hdSize) {
        super(Type.IMAGE);
        this.encryptQueryParam = encryptQueryParam;
        this.aesKey = aesKey;
        this.encryptType = encryptType;
        this.url = url;
        this.midSize = midSize;
        this.thumbSize = thumbSize;
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
        this.hdSize = hdSize;
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

    public String getUrl() {
        return url;
    }

    public long getMidSize() {
        return midSize;
    }

    public long getThumbSize() {
        return thumbSize;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public long getHdSize() {
        return hdSize;
    }
}