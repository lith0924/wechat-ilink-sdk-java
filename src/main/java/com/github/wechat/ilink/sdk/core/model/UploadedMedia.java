package com.github.wechat.ilink.sdk.core.model;

public class UploadedMedia {

  private String filekey;
  private CDNMedia media;
  private CDNMedia thumbMedia;
  private String aesKeyHex;
  private long rawSize;
  private long encryptedSize;
  private Long thumbEncryptedSize;
  private String md5;
  private String fileName;

  public String getFilekey() {
    return filekey;
  }

  public void setFilekey(String v) {
    filekey = v;
  }

  public CDNMedia getMedia() {
    return media;
  }

  public void setMedia(CDNMedia v) {
    media = v;
  }

  public CDNMedia getThumbMedia() {
    return thumbMedia;
  }

  public void setThumbMedia(CDNMedia v) {
    thumbMedia = v;
  }

  public String getAesKeyHex() {
    return aesKeyHex;
  }

  public void setAesKeyHex(String v) {
    aesKeyHex = v;
  }

  public long getRawSize() {
    return rawSize;
  }

  public void setRawSize(long v) {
    rawSize = v;
  }

  public long getEncryptedSize() {
    return encryptedSize;
  }

  public void setEncryptedSize(long v) {
    encryptedSize = v;
  }

  public Long getThumbEncryptedSize() {
    return thumbEncryptedSize;
  }

  public void setThumbEncryptedSize(Long v) {
    thumbEncryptedSize = v;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String v) {
    md5 = v;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String v) {
    fileName = v;
  }
}
