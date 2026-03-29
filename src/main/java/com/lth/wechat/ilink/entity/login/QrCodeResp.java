package com.lth.wechat.ilink.entity.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCodeResp {
    private String qrcode;
    private String qrcode_img_content;
    public String getQrcode() { return qrcode; }
    public String getQrcode_img_content() { return qrcode_img_content; }
}