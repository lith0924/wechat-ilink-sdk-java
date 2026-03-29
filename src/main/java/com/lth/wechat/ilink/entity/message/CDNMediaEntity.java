package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CDNMediaEntity {
    @JsonProperty("encrypt_query_param")
    private String encryptQueryParam;
    
    @JsonProperty("aes_key")
    private String aesKey;
    
    @JsonProperty("encrypt_type")
    private int encryptType;

    public String getEncryptQueryParam() {
        return encryptQueryParam;
    }

    public void setEncryptQueryParam(String encryptQueryParam) {
        this.encryptQueryParam = encryptQueryParam;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }
}