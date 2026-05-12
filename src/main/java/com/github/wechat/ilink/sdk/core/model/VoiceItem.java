package com.github.wechat.ilink.sdk.core.model;

public class VoiceItem {
  private CDNMedia media;
  private Integer encode_type;
  private Integer bits_per_sample;
  private Integer sample_rate;
  private Integer playtime;
  /** 语音转文字（入站由服务端填充；出站可选）。 */
  private String text;

  public CDNMedia getMedia() {
    return media;
  }

  public void setMedia(CDNMedia v) {
    media = v;
  }

  public Integer getEncode_type() {
    return encode_type;
  }

  public void setEncode_type(Integer v) {
    encode_type = v;
  }

  public Integer getBits_per_sample() {
    return bits_per_sample;
  }

  public void setBits_per_sample(Integer v) {
    bits_per_sample = v;
  }

  public Integer getSample_rate() {
    return sample_rate;
  }

  public void setSample_rate(Integer v) {
    sample_rate = v;
  }

  public Integer getPlaytime() {
    return playtime;
  }

  public void setPlaytime(Integer v) {
    playtime = v;
  }

  public String getText() {
    return text;
  }

  public void setText(String v) {
    text = v;
  }
}
