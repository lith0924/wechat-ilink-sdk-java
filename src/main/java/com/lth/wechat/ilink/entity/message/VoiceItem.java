package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceItem {
    @JsonProperty("media")
    private CDNMediaEntity media;
    
    @JsonProperty("encode_type")
    private int encodeType;
    
    @JsonProperty("bits_per_sample")
    private int bitsPerSample;
    
    @JsonProperty("sample_rate")
    private int sampleRate;
    
    @JsonProperty("playtime")
    private long playtime;
    
    @JsonProperty("text")
    private String text;

    public CDNMediaEntity getMedia() {
        return media;
    }

    public void setMedia(CDNMediaEntity media) {
        this.media = media;
    }

    public int getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(int encodeType) {
        this.encodeType = encodeType;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}