package com.github.wechat.ilink.sdk.core.model;

public class VideoItem {
  private CDNMedia media;
  private Long video_size;
  private Integer play_length;
  private String video_md5;
  private CDNMedia thumb_media;
  private Long thumb_size;
  private Integer thumb_height;
  private Integer thumb_width;

  public CDNMedia getMedia() {
    return media;
  }

  public void setMedia(CDNMedia v) {
    media = v;
  }

  public Long getVideo_size() {
    return video_size;
  }

  public void setVideo_size(Long v) {
    video_size = v;
  }

  public Integer getPlay_length() {
    return play_length;
  }

  public void setPlay_length(Integer v) {
    play_length = v;
  }

  public String getVideo_md5() {
    return video_md5;
  }

  public void setVideo_md5(String v) {
    video_md5 = v;
  }

  public CDNMedia getThumb_media() {
    return thumb_media;
  }

  public void setThumb_media(CDNMedia v) {
    thumb_media = v;
  }

  public Long getThumb_size() {
    return thumb_size;
  }

  public void setThumb_size(Long v) {
    thumb_size = v;
  }

  public Integer getThumb_height() {
    return thumb_height;
  }

  public void setThumb_height(Integer v) {
    thumb_height = v;
  }

  public Integer getThumb_width() {
    return thumb_width;
  }

  public void setThumb_width(Integer v) {
    thumb_width = v;
  }
}
