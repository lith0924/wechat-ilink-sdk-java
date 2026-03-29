package com.lth.wechat.ilink.dto.message;

/**
 * 视频消息内容
 */
public class VideoContent extends MessageContent {
    private final String encryptQueryParam;
    private final String aesKey;
    private final int encryptType;
    private final long videoSize;
    private final long playLength;
    private final String videoMd5;
    private final String thumbEncryptQueryParam;
    private final String thumbAesKey;
    private final int thumbEncryptType;
    private final long thumbSize;
    private final int thumbWidth;
    private final int thumbHeight;

    public VideoContent(String encryptQueryParam, String aesKey, int encryptType, long videoSize,
                       long playLength, String videoMd5, String thumbEncryptQueryParam,
                       String thumbAesKey, int thumbEncryptType, long thumbSize,
                       int thumbWidth, int thumbHeight) {
        super(Type.VIDEO);
        this.encryptQueryParam = encryptQueryParam;
        this.aesKey = aesKey;
        this.encryptType = encryptType;
        this.videoSize = videoSize;
        this.playLength = playLength;
        this.videoMd5 = videoMd5;
        this.thumbEncryptQueryParam = thumbEncryptQueryParam;
        this.thumbAesKey = thumbAesKey;
        this.thumbEncryptType = thumbEncryptType;
        this.thumbSize = thumbSize;
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
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

    public long getVideoSize() {
        return videoSize;
    }

    public long getPlayLength() {
        return playLength;
    }

    public String getVideoMd5() {
        return videoMd5;
    }

    public String getThumbEncryptQueryParam() {
        return thumbEncryptQueryParam;
    }

    public String getThumbAesKey() {
        return thumbAesKey;
    }

    public int getThumbEncryptType() {
        return thumbEncryptType;
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
}