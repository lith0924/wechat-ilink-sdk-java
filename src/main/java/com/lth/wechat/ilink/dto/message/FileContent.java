package com.lth.wechat.ilink.dto.message;

/**
 * 文件消息内容
 */
public class FileContent extends MessageContent {
    private final String encryptQueryParam;
    private final String aesKey;
    private final int encryptType;
    private final String fileName;
    private final String md5;
    private final String len;

    public FileContent(String encryptQueryParam, String aesKey, int encryptType, 
                      String fileName, String md5, String len) {
        super(Type.FILE);
        this.encryptQueryParam = encryptQueryParam;
        this.aesKey = aesKey;
        this.encryptType = encryptType;
        this.fileName = fileName;
        this.md5 = md5;
        this.len = len;
    }

    public String getEncryptQueryParam() {
        return encryptQueryParam;
    }

    public String getAesKey() {
        return aesKey;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMd5() {
        return md5;
    }

    public String getLen() {
        return len;
    }
}