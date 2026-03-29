package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoItem {
    @JsonProperty("media")
    private CDNMediaEntity media;
    
    @JsonProperty("video_size")
    private long videoSize;
    
    @JsonProperty("play_length")
    private long playLength;
    
    @JsonProperty("video_md5")
    private String videoMd5;
    
    @JsonProperty("thumb_media")
    private CDNMediaEntity thumbMedia;
    
    @JsonProperty("thumb_size")
    private long thumbSize;
    
    @JsonProperty("thumb_height")
    private int thumbHeight;
    
    @JsonProperty("thumb_width")
    private int thumbWidth;

    public CDNMediaEntity getMedia() {
        return media;
    }

    public void setMedia(CDNMediaEntity media) {
        this.media = media;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public long getPlayLength() {
        return playLength;
    }

    public void setPlayLength(long playLength) {
        this.playLength = playLength;
    }

    public String getVideoMd5() {
        return videoMd5;
    }

    public void setVideoMd5(String videoMd5) {
        this.videoMd5 = videoMd5;
    }

    public CDNMediaEntity getThumbMedia() {
        return thumbMedia;
    }

    public void setThumbMedia(CDNMediaEntity thumbMedia) {
        this.thumbMedia = thumbMedia;
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
}