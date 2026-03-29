package com.lth.wechat.ilink.dto.message;

/**
 * 语音消息内容
 */
public class VoiceContent extends MessageContent {
    private final String encryptQueryParam;
    private final String aesKey;
    private final int encryptType;
    private final int encodeType;
    private final int bitsPerSample;
    private final int sampleRate;
    private final long playtime;
    private final String text;

    public VoiceContent(String encryptQueryParam, String aesKey, int encryptType, int encodeType,
                       int bitsPerSample, int sampleRate, long playtime, String text) {
        super(Type.VOICE);
        this.encryptQueryParam = encryptQueryParam;
        this.aesKey = aesKey;
        this.encryptType = encryptType;
        this.encodeType = encodeType;
        this.bitsPerSample = bitsPerSample;
        this.sampleRate = sampleRate;
        this.playtime = playtime;
        this.text = text;
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

    public int getEncodeType() {
        return encodeType;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public long getPlaytime() {
        return playtime;
    }

    public String getText() {
        return text;
    }
}