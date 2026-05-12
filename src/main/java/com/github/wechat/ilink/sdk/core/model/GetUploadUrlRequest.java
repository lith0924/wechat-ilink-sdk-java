package com.github.wechat.ilink.sdk.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUploadUrlRequest {
  private String filekey;
  private Integer media_type;
  private String to_user_id;
  private Long rawsize;
  private String rawfilemd5;
  private Long filesize;
  private Boolean no_need_thumb;
  private String aeskey;
  private BaseInfo base_info;
  private Long thumb_rawsize;
  private String thumb_rawfilemd5;
  private Long thumb_filesize;

  public GetUploadUrlRequest(
      String filekey,
      Integer mediaType,
      String toUserId,
      Long rawsize,
      String rawfilemd5,
      Long filesize,
      Boolean noNeedThumb,
      String aeskey,
      BaseInfo info) {
    this(
        filekey,
        mediaType,
        toUserId,
        rawsize,
        rawfilemd5,
        filesize,
        noNeedThumb,
        aeskey,
        info,
        null,
        null,
        null);
  }

  public GetUploadUrlRequest(
      String filekey,
      Integer mediaType,
      String toUserId,
      Long rawsize,
      String rawfilemd5,
      Long filesize,
      Boolean noNeedThumb,
      String aeskey,
      BaseInfo info,
      Long thumbRawsize,
      String thumbRawfilemd5,
      Long thumbFilesize) {
    this.filekey = filekey;
    this.media_type = mediaType;
    this.to_user_id = toUserId;
    this.rawsize = rawsize;
    this.rawfilemd5 = rawfilemd5;
    this.filesize = filesize;
    this.no_need_thumb = noNeedThumb;
    this.aeskey = aeskey;
    this.base_info = info;
    this.thumb_rawsize = thumbRawsize;
    this.thumb_rawfilemd5 = thumbRawfilemd5;
    this.thumb_filesize = thumbFilesize;
  }

  public String getFilekey() {
    return filekey;
  }

  public Integer getMedia_type() {
    return media_type;
  }

  public String getTo_user_id() {
    return to_user_id;
  }

  public Long getRawsize() {
    return rawsize;
  }

  public String getRawfilemd5() {
    return rawfilemd5;
  }

  public Long getFilesize() {
    return filesize;
  }

  public Boolean getNo_need_thumb() {
    return no_need_thumb;
  }

  public String getAeskey() {
    return aeskey;
  }

  public BaseInfo getBase_info() {
    return base_info;
  }

  public Long getThumb_rawsize() {
    return thumb_rawsize;
  }

  public String getThumb_rawfilemd5() {
    return thumb_rawfilemd5;
  }

  public Long getThumb_filesize() {
    return thumb_filesize;
  }
}
