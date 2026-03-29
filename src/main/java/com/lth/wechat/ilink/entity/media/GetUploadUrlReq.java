package com.lth.wechat.ilink.entity.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lth.wechat.ilink.entity.config.BaseInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUploadUrlReq {
    @JsonProperty("filekey")
    private String fileKey;
    
    @JsonProperty("media_type")
    private int mediaType;
    
    @JsonProperty("to_user_id")
    private String toUserId;
    
    @JsonProperty("rawsize")
    private long rawSize;
    
    @JsonProperty("rawfilemd5")
    private String rawFileMd5;
    
    @JsonProperty("filesize")
    private long fileSize;
    
    @JsonProperty("no_need_thumb")
    private boolean noNeedThumb;
    
    @JsonProperty("aeskey")
    private String aesKey;
    
    @JsonProperty("base_info")
    private BaseInfo baseInfo;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public long getRawSize() {
        return rawSize;
    }

    public void setRawSize(long rawSize) {
        this.rawSize = rawSize;
    }

    public String getRawFileMd5() {
        return rawFileMd5;
    }

    public void setRawFileMd5(String rawFileMd5) {
        this.rawFileMd5 = rawFileMd5;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isNoNeedThumb() {
        return noNeedThumb;
    }

    public void setNoNeedThumb(boolean noNeedThumb) {
        this.noNeedThumb = noNeedThumb;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}