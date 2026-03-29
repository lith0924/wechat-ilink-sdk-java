package com.lth.wechat.ilink.entity.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUploadUrlResp {
    private String upload_param;
    private String thumb_upload_param;
    public String getUpload_param() { return upload_param; }
    public String getThumb_upload_param() { return thumb_upload_param; }
}