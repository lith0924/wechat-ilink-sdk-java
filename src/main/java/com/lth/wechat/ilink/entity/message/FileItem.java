package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileItem {
    @JsonProperty("media")
    private CDNMediaEntity media;
    
    @JsonProperty("file_name")
    private String fileName;
    
    @JsonProperty("md5")
    private String md5;
    
    @JsonProperty("len")
    private String len;

    public CDNMediaEntity getMedia() {
        return media;
    }

    public void setMedia(CDNMediaEntity media) {
        this.media = media;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }
}