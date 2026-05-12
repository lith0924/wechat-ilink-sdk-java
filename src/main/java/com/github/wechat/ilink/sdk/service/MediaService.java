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
    return upload(c, toUserId, bytes, fileName, 1, null);
  }

  public UploadedMedia uploadImageWithThumb(
      LoginContext c, String toUserId, byte[] imageBytes, byte[] thumbBytes, String fileName)
      throws IOException {
    if (thumbBytes == null || thumbBytes.length == 0) {
      throw new MediaUploadException("thumb bytes required", null);
    }
    return upload(c, toUserId, imageBytes, fileName, 1, thumbBytes);
  }

  public UploadedMedia uploadVideo(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 2, null);
  }

  public UploadedMedia uploadVideoWithThumb(
      LoginContext c, String toUserId, byte[] videoBytes, byte[] thumbBytes, String fileName)
      throws IOException {
    if (thumbBytes == null || thumbBytes.length == 0) {
      throw new MediaUploadException("thumb bytes required", null);
    }
    return upload(c, toUserId, videoBytes, fileName, 2, thumbBytes);
  }

  public UploadedMedia uploadFile(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 3, null);
  }

  public UploadedMedia uploadVoice(LoginContext c, String toUserId, byte[] bytes, String fileName)
      throws IOException {
    return upload(c, toUserId, bytes, fileName, 4, null);
  }

  private UploadedMedia upload(
      LoginContext c,
      String toUserId,
      byte[] plain,
      String fileName,
      int mediaType,
      byte[] thumbPlainOrNull)
      throws IOException {

    if (plain == null || plain.length == 0) {
      throw new MediaUploadException("empty media bytes", null);
    }
    if (toUserId == null || toUserId.trim().isEmpty()) {
      throw new MediaUploadException("empty toUserId", null);
    }

    String aesKeyHex = RandomUtils.randomHex(16);
    byte[] aesKeyBytes = HexUtils.decodeHex(aesKeyHex);

    byte[] encrypted;
    try {
      encrypted = AesEcbUtil.encryptPkcs7(plain, aesKeyBytes);
    } catch (Exception e) {
      throw new MediaUploadException("encrypt media failed", e);
    }

    boolean withThumb = thumbPlainOrNull != null && thumbPlainOrNull.length > 0;
    byte[] thumbEncrypted = null;
    Long thumbRawLen = null;
    String thumbMd5 = null;
    Long thumbEncLen = null;
    if (withThumb) {
      try {
        thumbEncrypted = AesEcbUtil.encryptPkcs7(thumbPlainOrNull, aesKeyBytes);
      } catch (Exception e) {
        throw new MediaUploadException("encrypt thumb failed", e);
      }
      thumbRawLen = (long) thumbPlainOrNull.length;
      thumbMd5 = HashUtils.md5Hex(thumbPlainOrNull);
      thumbEncLen = (long) thumbEncrypted.length;
    }

    String filekey = RandomUtils.randomHex(16);

    GetUploadUrlRequest req =
        new GetUploadUrlRequest(
            filekey,
            mediaType,
            toUserId,
            (long) plain.length,
            HashUtils.md5Hex(plain),
            (long) encrypted.length,
            withThumb ? Boolean.FALSE : Boolean.TRUE,
            aesKeyHex,
            new BaseInfo(config.getChannelVersion()),
            thumbRawLen,
            thumbMd5,
            thumbEncLen);

    GetUploadUrlResponse resp =
        apiClient.post(c, "/ilink/bot/getuploadurl", req, GetUploadUrlResponse.class);

    if (resp.getUpload_param() == null || resp.getUpload_param().trim().isEmpty()) {
      throw new MediaUploadException("empty upload_param", null);
    }

    String mainUploadUrl = cdnUploadUrl(resp.getUpload_param(), filekey);
    String finalEncryptedParam;
    try {
      finalEncryptedParam = httpClientFacade.uploadBytes(mainUploadUrl, encrypted);
    } catch (Exception e) {
      throw new MediaUploadException("cdn upload failed", e);
    }

    if (finalEncryptedParam == null || finalEncryptedParam.trim().isEmpty()) {
      throw new MediaUploadException("empty x-encrypted-param", null);
    }

    String aesKeyB64 =
        Base64.getEncoder().encodeToString(aesKeyHex.getBytes(StandardCharsets.UTF_8));

    CDNMedia media = new CDNMedia();
    media.setEncrypt_query_param(finalEncryptedParam);
    media.setAes_key(aesKeyB64);
    media.setEncrypt_type(1);

    UploadedMedia out = new UploadedMedia();
    out.setFilekey(filekey);
    out.setMedia(media);
    out.setAesKeyHex(aesKeyHex);
    out.setRawSize(plain.length);
    out.setEncryptedSize(encrypted.length);
    out.setMd5(HashUtils.md5Hex(plain));
    out.setFileName(fileName);

    if (withThumb) {
      String thumbParam = resp.getThumb_upload_param();
      if (thumbParam == null || thumbParam.trim().isEmpty()) {
        throw new MediaUploadException("empty thumb_upload_param", null);
      }
      String thumbUploadUrl = cdnUploadUrl(thumbParam, filekey);
      String thumbEncParam;
      try {
        thumbEncParam = httpClientFacade.uploadBytes(thumbUploadUrl, thumbEncrypted);
      } catch (Exception e) {
        throw new MediaUploadException("cdn thumb upload failed", e);
      }
      if (thumbEncParam == null || thumbEncParam.trim().isEmpty()) {
        throw new MediaUploadException("empty thumb x-encrypted-param", null);
      }
      CDNMedia thumbMedia = new CDNMedia();
      thumbMedia.setEncrypt_query_param(thumbEncParam);
      thumbMedia.setAes_key(aesKeyB64);
      thumbMedia.setEncrypt_type(1);
      out.setThumbMedia(thumbMedia);
      out.setThumbEncryptedSize(thumbEncLen);
    }

    return out;
  }

  private static String cdnUploadUrl(String encryptedQueryParam, String filekey)
      throws IOException {
    return CDN_BASE
        + "/upload?encrypted_query_param="
        + URLEncoder.encode(encryptedQueryParam, StandardCharsets.UTF_8.name())
        + "&filekey="
        + URLEncoder.encode(filekey, StandardCharsets.UTF_8.name());
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
