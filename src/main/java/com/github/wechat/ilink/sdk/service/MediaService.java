package com.github.wechat.ilink.sdk.service;

import com.github.wechat.ilink.sdk.core.config.ILinkConfig;
import com.github.wechat.ilink.sdk.core.crypto.AesEcbUtil;
import com.github.wechat.ilink.sdk.core.exception.MediaUploadException;
import com.github.wechat.ilink.sdk.core.http.BusinessApiClient;
import com.github.wechat.ilink.sdk.core.http.HttpClientFacade;
import com.github.wechat.ilink.sdk.core.login.LoginContext;
import com.github.wechat.ilink.sdk.core.model.BaseInfo;
import com.github.wechat.ilink.sdk.core.model.CDNMedia;
import com.github.wechat.ilink.sdk.core.model.GetUploadUrlRequest;
import com.github.wechat.ilink.sdk.core.model.GetUploadUrlResponse;
import com.github.wechat.ilink.sdk.core.model.UploadedMedia;
import com.github.wechat.ilink.sdk.core.utils.HashUtils;
import com.github.wechat.ilink.sdk.core.utils.HexUtils;
import com.github.wechat.ilink.sdk.core.utils.RandomUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MediaService {

  private static final String CDN_BASE = "https://novac2c.cdn.weixin.qq.com/c2c";

  private final ILinkConfig config;
  private final BusinessApiClient apiClient;
  private final HttpClientFacade httpClientFacade;

  public MediaService(
      ILinkConfig config, BusinessApiClient apiClient, HttpClientFacade httpClientFacade) {
    this.config = config;
    this.apiClient = apiClient;
    this.httpClientFacade = httpClientFacade;
  }

  public UploadedMedia uploadImage(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 1);
  }

  public UploadedMedia uploadVideo(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 2);
  }

  public UploadedMedia uploadFile(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 3);
  }

  public UploadedMedia uploadVoice(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 4);
  }

  private UploadedMedia upload(
      LoginContext c, String toUserId, byte[] plain, String fileName, int mediaType)
      throws IOException {

    if (plain == null || plain.length == 0) {
      throw new MediaUploadException("empty media bytes", null);
    }
    if (toUserId == null || toUserId.trim().isEmpty()) {
      throw new MediaUploadException("empty toUserId", null);
    }

    // 正确生成 16 字节 AES key => 32 位 hex
    String aesKeyHex = RandomUtils.randomHex(16);
    byte[] aesKeyBytes = HexUtils.decodeHex(aesKeyHex);

    byte[] encrypted;
    try {
      encrypted = AesEcbUtil.encryptPkcs7(plain, aesKeyBytes);
    } catch (Exception e) {
      throw new MediaUploadException("encrypt media failed", e);
    }

    // filekey 也用稳定随机值，不用 clientId 裁剪
    String filekey = RandomUtils.randomHex(16);

    GetUploadUrlRequest req =
        new GetUploadUrlRequest(
            filekey,
            mediaType,
            toUserId,
            (long) plain.length,
            HashUtils.md5Hex(plain),
            (long) encrypted.length,
            Boolean.TRUE,
            aesKeyHex,
            new BaseInfo(config.getChannelVersion()));

    GetUploadUrlResponse resp =
        apiClient.post(c, "/ilink/bot/getuploadurl", req, GetUploadUrlResponse.class);

    if (resp.getUpload_param() == null || resp.getUpload_param().trim().isEmpty()) {
      throw new MediaUploadException("empty upload_param", null);
    }

    String uploadUrl =
        CDN_BASE
            + "/upload?encrypted_query_param="
            + URLEncoder.encode(resp.getUpload_param(), StandardCharsets.UTF_8.name())
            + "&filekey="
            + URLEncoder.encode(filekey, StandardCharsets.UTF_8.name());

    String finalEncryptedParam;
    try {
      finalEncryptedParam = httpClientFacade.uploadBytes(uploadUrl, encrypted);
    } catch (Exception e) {
      throw new MediaUploadException("cdn upload failed", e);
    }

    if (finalEncryptedParam == null || finalEncryptedParam.trim().isEmpty()) {
      throw new MediaUploadException("empty x-encrypted-param", null);
    }

    CDNMedia media = new CDNMedia();
    media.setEncrypt_query_param(finalEncryptedParam);

    // 协议兼容：base64(hex string)
    media.setAes_key(
        Base64.getEncoder().encodeToString(aesKeyHex.getBytes(StandardCharsets.UTF_8)));
    media.setEncrypt_type(1);

    UploadedMedia out = new UploadedMedia();
    out.setFilekey(filekey);
    out.setMedia(media);
    out.setAesKeyHex(aesKeyHex);
    out.setRawSize(plain.length);
    out.setEncryptedSize(encrypted.length);
    out.setMd5(HashUtils.md5Hex(plain));
    out.setFileName(fileName);
    return out;
  }

  public byte[] downloadMedia(CDNMedia media) throws IOException {
    if (media == null) {
      throw new MediaUploadException("media is null", null);
    }
    if (media.getEncrypt_query_param() == null || media.getEncrypt_query_param().trim().isEmpty()) {
      throw new MediaUploadException("media.encrypt_query_param is empty", null);
    }
    if (media.getAes_key() == null || media.getAes_key().trim().isEmpty()) {
      throw new MediaUploadException("media.aes_key is empty", null);
    }

    String url =
        CDN_BASE
            + "/download?encrypted_query_param="
            + URLEncoder.encode(media.getEncrypt_query_param(), StandardCharsets.UTF_8.name());

    byte[] encrypted = httpClientFacade.getBytes(url);
    byte[] decoded = Base64.getDecoder().decode(media.getAes_key());

    byte[] key;
    if (decoded.length == 16) {
      key = decoded;
    } else {
      key = HexUtils.decodeHex(new String(decoded, StandardCharsets.UTF_8));
    }

    try {
      return AesEcbUtil.decryptPkcs7(encrypted, key);
    } catch (Exception e) {
      throw new MediaUploadException("decrypt media failed", e);
    }
  }
}
