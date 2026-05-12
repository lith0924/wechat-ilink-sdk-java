package com.github.wechat.ilink.sdk.core.model;

public class ImageItem {
  private CDNMedia media;
  private CDNMedia thumb_media;
  private String aeskey;
  private String url;
  private Long mid_size;
  private Long thumb_size;
  private Integer thumb_height;
  private Integer thumb_width;
  private Long hd_size;

  public CDNMedia getMedia() {
    return media;
  }

  public void setMedia(CDNMedia v) {
    media = v;
  }

  public CDNMedia getThumb_media() {
    return thumb_media;
  }

  public void setThumb_media(CDNMedia v) {
    thumb_media = v;
  }

  public String getAeskey() {
    return aeskey;
  }

  public void setAeskey(String v) {
    aeskey = v;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String v) {
    url = v;
  }

  public Long getMid_size() {
    return mid_size;
  }

  public void setMid_size(Long v) {
    mid_size = v;
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

  public Long getHd_size() {
    return hd_size;
  }

  public void setHd_size(Long v) {
    hd_size = v;
  }
}
