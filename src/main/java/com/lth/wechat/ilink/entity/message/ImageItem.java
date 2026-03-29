package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageItem {
    @JsonProperty("media")
    private CDNMediaEntity media;
    
    @JsonProperty("thumb_media")
    private CDNMediaEntity thumbMedia;
    
    @JsonProperty("aeskey")
    private String aeskey;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("mid_size")
    private long midSize;
    
    @JsonProperty("thumb_size")
    private long thumbSize;
    
    @JsonProperty("thumb_height")
    private int thumbHeight;
    
    @JsonProperty("thumb_width")
    private int thumbWidth;
    
    @JsonProperty("hd_size")
    private long hdSize;

    public CDNMediaEntity getMedia() {
        return media;
    }

    public void setMedia(CDNMediaEntity media) {
        this.media = media;
    }

    public CDNMediaEntity getThumbMedia() {
        return thumbMedia;
    }

    public void setThumbMedia(CDNMediaEntity thumbMedia) {
        this.thumbMedia = thumbMedia;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getMidSize() {
        return midSize;
    }

    public void setMidSize(long midSize) {
        this.midSize = midSize;
    }

    public long getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(long thumbSize) {
        this.thumbSize = thumbSize;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public long getHdSize() {
        return hdSize;
    }

    public void setHdSize(long hdSize) {
        this.hdSize = hdSize;
    }
}