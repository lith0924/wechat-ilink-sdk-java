package com.lth.wechat.ilink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lth.wechat.ilink.converter.EntityConverter;
import com.lth.wechat.ilink.dto.login.LoginStatus;
import com.lth.wechat.ilink.dto.login.QrCodeInfo;
import com.lth.wechat.ilink.dto.message.ReceiveMessagesResult;
import com.lth.wechat.ilink.entity.login.LoginStatusResp;
import com.lth.wechat.ilink.entity.login.QrCodeResp;
import com.lth.wechat.ilink.entity.message.GetUpdatesResponse;
import com.lth.wechat.ilink.entity.send.SendMessageRequest;
import com.lth.wechat.ilink.entity.send.SendMessageResp;
import com.lth.wechat.ilink.entity.config.BaseInfo;
import com.lth.wechat.ilink.entity.config.GetConfigRequest;
import com.lth.wechat.ilink.entity.config.GetConfigResp;
import com.lth.wechat.ilink.entity.config.SendTypingRequest;
import com.lth.wechat.ilink.entity.config.SendTypingResp;
import com.lth.wechat.ilink.entity.media.GetUploadUrlReq;
import com.lth.wechat.ilink.entity.media.GetUploadUrlResp;
import com.lth.wechat.ilink.exception.ILinkException;
import com.lth.wechat.ilink.exception.ILinkSessionExpiredException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * 微信 iLink Bot SDK
 * @author lth
 * @version 1.0.0
 */
public class ILinkClient {
    public static final String VERSION = "1.0.0";
    public static final String DEFAULT_BASE_URL = "https://ilinkai.weixin.qq.com";
    public static final String CHANNEL_VERSION = "1.0.0";
    public static final String AUTHORIZATION_TYPE = "ilink_bot_token";
    public static final String CDN_BASE_URL = "https://novac2c.cdn.weixin.qq.com/c2c";

    private static final String GET_BOT_QRCODE = "/ilink/bot/get_bot_qrcode?bot_type=3";
    private static final String GET_QRCODE_STATUS = "/ilink/bot/get_qrcode_status";
    private static final String GET_UPDATES = "/ilink/bot/getupdates";
    private static final String SEND_MESSAGE = "/ilink/bot/sendmessage";
    private static final String GET_CONFIG = "/ilink/bot/getconfig";
    private static final String SEND_TYPING = "/ilink/bot/sendtyping";
    private static final String GET_UPLOAD_URL = "/ilink/bot/getuploadurl";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final SecureRandom random = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(ILinkClient.class);

    /**
     * 默认构造函数，使用默认的HttpClient配置
     */
    public ILinkClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build());
    }

    /**
     * 自定义HttpClient构造函数
     * @param httpClient 自定义的HttpClient实例
     */
    public ILinkClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    // X-WECHAT-UIN 官方算法
    private String generateXWechatUin() {
        long uint32 = random.nextLong() & 0xFFFFFFFFL;
        String decimalStr = String.valueOf(uint32);
        return Base64.getEncoder().encodeToString(decimalStr.getBytes(StandardCharsets.UTF_8));
    }

    private HttpRequest.Builder baseRequest() {
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/json");
    }

    // ==================== 登录相关 ====================

    /**
     * 获取二维码
     */
    public QrCodeResp getBotQrCode() {
        try {
            HttpRequest req = baseRequest()
                    .GET()
                    .uri(URI.create(DEFAULT_BASE_URL + GET_BOT_QRCODE))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(resp.body(), QrCodeResp.class);
        } catch (Exception e) {
            throw new ILinkException("获取二维码失败", e);
        }
    }

    /**
     * 获取二维码信息（DTO版）
     */
    public QrCodeInfo getQrCodeInfo() {
        return EntityConverter.toQrCodeInfo(getBotQrCode());
    }

    /**
     * 查询登录状态
     * @param qrcode 二维码ID
     */
    public LoginStatusResp getQrCodeStatus(String qrcode) {
        try {
            if (qrcode == null) {
                throw new ILinkException("qrcode不能为空");
            }
            HttpRequest req = baseRequest()
                    .GET()
                    .uri(URI.create(DEFAULT_BASE_URL + GET_QRCODE_STATUS + "?qrcode=" + qrcode))
                    .header("iLink-App-ClientVersion", "1")
                    .timeout(java.time.Duration.ofSeconds(35))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(resp.body(), LoginStatusResp.class);
        } catch (Exception e) {
            throw new ILinkException("查询登录状态失败", e);
        }
    }

    /**
     * 轮询登录状态（DTO版）
     * @param qrcode 二维码ID
     */
    public LoginStatus pollLoginStatus(String qrcode) {
        return EntityConverter.toLoginStatus(getQrCodeStatus(qrcode));
    }

    // ==================== 消息相关 ====================

    /**
     * 拉取消息（原始API）
     * @param credentials 登录凭证
     * @param cursor 游标，首次传空字符串
     */
    public GetUpdatesResponse getUpdates(LoginCredentials credentials, String cursor) {
        return executeWithRetry(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                if (cursor != null) {
                    body.put("get_updates_buf", cursor);
                }
                body.put("base_info", Map.of("channel_version", CHANNEL_VERSION));

                String baseUrl = credentials.getApiBaseUrl() != null ? credentials.getApiBaseUrl() : DEFAULT_BASE_URL;
                HttpRequest httpReq = baseRequest()
                        .header("AuthorizationType", AUTHORIZATION_TYPE)
                        .header("Authorization", "Bearer " + credentials.getBotToken())
                        .header("X-WECHAT-UIN", generateXWechatUin())
                        .uri(URI.create(baseUrl + GET_UPDATES))
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                        .build();

                HttpResponse<String> resp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
                GetUpdatesResponse response = objectMapper.readValue(resp.body(), GetUpdatesResponse.class);
                checkResponseCode(response.getRet(), "拉取消息");
                return response;
            } catch (Exception e) {
                throw new ILinkException("拉取消息失败", e);
            }
        }, 3, "拉取消息");
    }

    /**
     * 接收消息（DTO版）
     * @param credentials 登录凭证
     * @param cursor 游标
     */
    public ReceiveMessagesResult receiveMessages(LoginCredentials credentials, String cursor) {
        return EntityConverter.toReceiveMessagesResult(getUpdates(credentials, cursor));
    }

    /**
     * 发送消息（原始API）
     * @param credentials 登录凭证
     * @param request 消息请求体
     */
    public SendMessageResp sendMessage(LoginCredentials credentials, SendMessageRequest request) {
        return executeWithRetry(() -> {
            try {
                String baseUrl = credentials.getApiBaseUrl() != null ? credentials.getApiBaseUrl() : DEFAULT_BASE_URL;
                HttpRequest httpReq = baseRequest()
                        .header("AuthorizationType", AUTHORIZATION_TYPE)
                        .header("Authorization", "Bearer " + credentials.getBotToken())
                        .header("X-WECHAT-UIN", generateXWechatUin())
                        .uri(URI.create(baseUrl + SEND_MESSAGE))
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                        .build();

                HttpResponse<String> resp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
                SendMessageResp response = objectMapper.readValue(resp.body(), SendMessageResp.class);
                checkResponseCode(response.getRet(), "发送消息");
                return response;
            } catch (Exception e) {
                throw new ILinkException("发送消息失败", e);
            }
        }, 3, "发送消息");
    }

    /**
     * 发送文本消息
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param contextToken 会话令牌
     * @param text 文本内容
     */
    public void sendTextMessage(LoginCredentials credentials, String toUserId, String contextToken, String text) {
        SendMessageRequest request = new SendMessageRequest();

        SendMessageRequest.Msg msg = new SendMessageRequest.Msg();
        msg.setFromUserId("");
        msg.setToUserId(toUserId);
        msg.setClientId("text-" + UUID.randomUUID());
        msg.setMessageType(2);
        msg.setMessageState(2);
        msg.setContextToken(contextToken);

        List<com.lth.wechat.ilink.entity.message.MessageItem> itemList = new ArrayList<>();
        com.lth.wechat.ilink.entity.message.MessageItem textItem = new com.lth.wechat.ilink.entity.message.MessageItem();
        textItem.setType(1);
        textItem.setText_item(new com.lth.wechat.ilink.entity.message.TextItem());
        textItem.getText_item().setText(text);

        itemList.add(textItem);
        msg.setItemList(itemList);
        request.setMsg(msg);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendMessage(credentials, request);
    }

    /**
     * 发送图片消息
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param contextToken 会话令牌
     * @param mediaInfo 媒体信息（从 uploadMedia 获取）
     */
    public void sendImageMessage(LoginCredentials credentials, String toUserId, String contextToken, MediaInfo mediaInfo) {
        SendMessageRequest request = new SendMessageRequest();

        SendMessageRequest.Msg msg = new SendMessageRequest.Msg();
        msg.setFromUserId("");
        msg.setToUserId(toUserId);
        msg.setClientId("image-" + UUID.randomUUID());
        msg.setMessageType(2);
        msg.setMessageState(2);
        msg.setContextToken(contextToken);

        List<com.lth.wechat.ilink.entity.message.MessageItem> itemList = new ArrayList<>();
        com.lth.wechat.ilink.entity.message.MessageItem imageItem = new com.lth.wechat.ilink.entity.message.MessageItem();
        imageItem.setType(2);
        imageItem.setImage_item(new com.lth.wechat.ilink.entity.message.ImageItem());

        com.lth.wechat.ilink.entity.message.CDNMediaEntity media = new com.lth.wechat.ilink.entity.message.CDNMediaEntity();
        media.setEncryptQueryParam(mediaInfo.getEncryptQueryParam());
        media.setAesKey(mediaInfo.getAesKey());
        media.setEncryptType(1);
        imageItem.getImage_item().setMedia(media);
        imageItem.getImage_item().setMidSize(mediaInfo.getFileSize());

        itemList.add(imageItem);
        msg.setItemList(itemList);
        request.setMsg(msg);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendMessage(credentials, request);
    }

    /**
     * 发送语音消息
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param contextToken 会话令牌
     * @param mediaInfo 媒体信息
     * @param duration 语音时长（毫秒）
     * @param encodeType 编码类型：6=SILK
     */
    public void sendVoiceMessage(LoginCredentials credentials, String toUserId, String contextToken, MediaInfo mediaInfo, int duration, int encodeType) {
        SendMessageRequest request = new SendMessageRequest();

        SendMessageRequest.Msg msg = new SendMessageRequest.Msg();
        msg.setFromUserId("");
        msg.setToUserId(toUserId);
        msg.setClientId("voice-" + UUID.randomUUID());
        msg.setMessageType(2);
        msg.setMessageState(2);
        msg.setContextToken(contextToken);

        List<com.lth.wechat.ilink.entity.message.MessageItem> itemList = new ArrayList<>();
        com.lth.wechat.ilink.entity.message.MessageItem voiceItem = new com.lth.wechat.ilink.entity.message.MessageItem();
        voiceItem.setType(3);
        voiceItem.setVoice_item(new com.lth.wechat.ilink.entity.message.VoiceItem());

        com.lth.wechat.ilink.entity.message.CDNMediaEntity media = new com.lth.wechat.ilink.entity.message.CDNMediaEntity();
        media.setEncryptQueryParam(mediaInfo.getEncryptQueryParam());
        media.setAesKey(mediaInfo.getAesKey());
        media.setEncryptType(1);
        voiceItem.getVoice_item().setMedia(media);
        voiceItem.getVoice_item().setPlaytime(duration);
        voiceItem.getVoice_item().setEncodeType(encodeType);

        itemList.add(voiceItem);
        msg.setItemList(itemList);
        request.setMsg(msg);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendMessage(credentials, request);
    }

    /**
     * 发送文件消息
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param contextToken 会话令牌
     * @param mediaInfo 媒体信息
     * @param fileName 文件名
     * @param fileSize 文件大小
     */
    public void sendFileMessage(LoginCredentials credentials, String toUserId, String contextToken, MediaInfo mediaInfo, String fileName, long fileSize) {
        SendMessageRequest request = new SendMessageRequest();

        SendMessageRequest.Msg msg = new SendMessageRequest.Msg();
        msg.setFromUserId("");
        msg.setToUserId(toUserId);
        msg.setClientId("file-" + UUID.randomUUID());
        msg.setMessageType(2);
        msg.setMessageState(2);
        msg.setContextToken(contextToken);

        List<com.lth.wechat.ilink.entity.message.MessageItem> itemList = new ArrayList<>();
        com.lth.wechat.ilink.entity.message.MessageItem fileItem = new com.lth.wechat.ilink.entity.message.MessageItem();
        fileItem.setType(4);
        fileItem.setFile_item(new com.lth.wechat.ilink.entity.message.FileItem());

        com.lth.wechat.ilink.entity.message.CDNMediaEntity media = new com.lth.wechat.ilink.entity.message.CDNMediaEntity();
        media.setEncryptQueryParam(mediaInfo.getEncryptQueryParam());
        media.setAesKey(mediaInfo.getAesKey());
        media.setEncryptType(1);
        fileItem.getFile_item().setMedia(media);
        fileItem.getFile_item().setFileName(fileName);
        fileItem.getFile_item().setLen(String.valueOf(fileSize));

        itemList.add(fileItem);
        msg.setItemList(itemList);
        request.setMsg(msg);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendMessage(credentials, request);
    }

    /**
     * 发送视频消息
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param contextToken 会话令牌
     * @param mediaInfo 媒体信息
     * @param videoSize 视频大小
     * @param duration 视频时长（毫秒）
     */
    public void sendVideoMessage(LoginCredentials credentials, String toUserId, String contextToken, MediaInfo mediaInfo, long videoSize, int duration) {
        SendMessageRequest request = new SendMessageRequest();

        SendMessageRequest.Msg msg = new SendMessageRequest.Msg();
        msg.setFromUserId("");
        msg.setToUserId(toUserId);
        msg.setClientId("video-" + UUID.randomUUID());
        msg.setMessageType(2);
        msg.setMessageState(2);
        msg.setContextToken(contextToken);

        List<com.lth.wechat.ilink.entity.message.MessageItem> itemList = new ArrayList<>();
        com.lth.wechat.ilink.entity.message.MessageItem videoItem = new com.lth.wechat.ilink.entity.message.MessageItem();
        videoItem.setType(5);
        videoItem.setVideo_item(new com.lth.wechat.ilink.entity.message.VideoItem());

        com.lth.wechat.ilink.entity.message.CDNMediaEntity media = new com.lth.wechat.ilink.entity.message.CDNMediaEntity();
        media.setEncryptQueryParam(mediaInfo.getEncryptQueryParam());
        media.setAesKey(mediaInfo.getAesKey());
        media.setEncryptType(1);
        videoItem.getVideo_item().setMedia(media);
        videoItem.getVideo_item().setVideoSize(videoSize);
        videoItem.getVideo_item().setPlayLength(duration);

        itemList.add(videoItem);
        msg.setItemList(itemList);
        request.setMsg(msg);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendMessage(credentials, request);
    }

    /**
     * 发送引用消息
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param contextToken 会话令牌
     * @param refMessage 引用的消息体
     * @param replyText 回复文本
     */
    public void sendReplyMessage(LoginCredentials credentials, String toUserId, String contextToken, com.lth.wechat.ilink.entity.message.RefMessageEntity refMessage, String replyText) {
        SendMessageRequest request = new SendMessageRequest();

        SendMessageRequest.Msg msg = new SendMessageRequest.Msg();
        msg.setFromUserId("");
        msg.setToUserId(toUserId);
        msg.setClientId("reply-" + UUID.randomUUID());
        msg.setMessageType(2);
        msg.setMessageState(2);
        msg.setContextToken(contextToken);

        List<com.lth.wechat.ilink.entity.message.MessageItem> itemList = new ArrayList<>();

        if (replyText != null && !replyText.isEmpty()) {
            com.lth.wechat.ilink.entity.message.MessageItem textItem = new com.lth.wechat.ilink.entity.message.MessageItem();
            textItem.setType(1);
            textItem.setText_item(new com.lth.wechat.ilink.entity.message.TextItem());
            textItem.getText_item().setText(replyText);
            itemList.add(textItem);
        }

        com.lth.wechat.ilink.entity.message.MessageItem refItem = new com.lth.wechat.ilink.entity.message.MessageItem();
        refItem.setType(6);
        refItem.setRef_msg(refMessage);
        itemList.add(refItem);

        msg.setItemList(itemList);
        request.setMsg(msg);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendMessage(credentials, request);
    }

    // ==================== 输入状态 ====================

    /**
     * 发送输入状态
     * @param credentials 登录凭证
     * @param toUserId 目标用户ID
     * @param typingTicket 票据（从 getConfig 获取）
     * @param isTyping true=开始输入，false=结束输入
     */
    public void sendTypingStatus(LoginCredentials credentials, String toUserId, String typingTicket, boolean isTyping) {
        SendTypingRequest request = new SendTypingRequest();
        request.setIlinkUserId(toUserId);
        request.setTypingTicket(typingTicket);
        request.setStatus(isTyping ? 1 : 2);

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setChannelVersion(CHANNEL_VERSION);
        request.setBaseInfo(baseInfo);

        sendTyping(credentials, request);
    }

    /**
     * 获取配置
     * @param credentials 登录凭证
     * @param req 请求参数（可设置 ilinkUserId 和 contextToken 获取更精准的 typingTicket）
     */
    public GetConfigResp getConfig(LoginCredentials credentials, GetConfigRequest req) {
        return executeWithRetry(() -> {
            try {
                String baseUrl = credentials.getApiBaseUrl() != null ? credentials.getApiBaseUrl() : DEFAULT_BASE_URL;
                HttpRequest httpReq = baseRequest()
                        .header("AuthorizationType", AUTHORIZATION_TYPE)
                        .header("Authorization", "Bearer " + credentials.getBotToken())
                        .header("X-WECHAT-UIN", generateXWechatUin())
                        .uri(URI.create(baseUrl + GET_CONFIG))
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(req)))
                        .build();

                HttpResponse<String> resp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
                GetConfigResp response = objectMapper.readValue(resp.body(), GetConfigResp.class);
                checkResponseCode(response.getRet(), "获取配置");
                return response;
            } catch (Exception e) {
                throw new ILinkException("getConfig 失败", e);
            }
        }, 3, "获取配置");
    }

    /**
     * 发送输入状态（原始API）
     * @param credentials 登录凭证
     * @param req 请求体
     */
    public SendTypingResp sendTyping(LoginCredentials credentials, SendTypingRequest req) {
        return executeWithRetry(() -> {
            try {
                String baseUrl = credentials.getApiBaseUrl() != null ? credentials.getApiBaseUrl() : DEFAULT_BASE_URL;
                HttpRequest httpReq = baseRequest()
                        .header("AuthorizationType", AUTHORIZATION_TYPE)
                        .header("Authorization", "Bearer " + credentials.getBotToken())
                        .header("X-WECHAT-UIN", generateXWechatUin())
                        .uri(URI.create(baseUrl + SEND_TYPING))
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(req)))
                        .build();

                HttpResponse<String> resp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
                SendTypingResp response = objectMapper.readValue(resp.body(), SendTypingResp.class);
                checkResponseCode(response.getRet(), "发送输入状态");
                return response;
            } catch (Exception e) {
                throw new ILinkException("sendTyping 失败", e);
            }
        }, 3, "发送输入状态");
    }

    // ==================== 媒体相关 ====================

    /**
     * 获取上传链接（原始API）
     * @param credentials 登录凭证
     * @param req 请求体（包含文件信息）
     */
    public GetUploadUrlResp getUploadUrl(LoginCredentials credentials, GetUploadUrlReq req) {
        try {
            String baseUrl = credentials.getApiBaseUrl() != null ? credentials.getApiBaseUrl() : DEFAULT_BASE_URL;
            HttpRequest httpReq = baseRequest()
                    .header("AuthorizationType", AUTHORIZATION_TYPE)
                    .header("Authorization", "Bearer " + credentials.getBotToken())
                    .header("X-WECHAT-UIN", generateXWechatUin())
                    .uri(URI.create(baseUrl + GET_UPLOAD_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(req)))
                    .build();

            HttpResponse<String> resp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(resp.body(), GetUploadUrlResp.class);
        } catch (Exception e) {
            throw new ILinkException("getUploadUrl 失败", e);
        }
    }

    /**
     * 上传媒体文件
     * @param credentials 登录凭证
     * @param mediaType 1=图片,2=视频,3=文件,4=语音
     * @param toUserId 目标用户ID
     * @param fileData 文件数据
     * @return MediaInfo 包含 encryptQueryParam、aesKey、fileSize
     */
    public MediaInfo uploadMedia(LoginCredentials credentials, int mediaType, String toUserId, byte[] fileData) {
        try {
            String fileKey = com.lth.wechat.ilink.utils.MediaUtils.generateFileKey();
            byte[] aesKey = com.lth.wechat.ilink.utils.MediaUtils.generateAesKey();
            String rawFileMd5 = com.lth.wechat.ilink.utils.MediaUtils.calculateMd5(fileData);
            long rawSize = fileData.length;
            long fileSize = com.lth.wechat.ilink.utils.MediaUtils.calculateEncryptedSize(rawSize);

            byte[] encryptedData = com.lth.wechat.ilink.utils.MediaUtils.encryptAesEcb(fileData, aesKey);

            GetUploadUrlReq req = new GetUploadUrlReq();
            req.setFileKey(fileKey);
            req.setMediaType(mediaType);
            req.setToUserId(toUserId);
            req.setRawSize(rawSize);
            req.setRawFileMd5(rawFileMd5);
            req.setFileSize(fileSize);
            req.setNoNeedThumb(true);
            req.setAesKey(com.lth.wechat.ilink.utils.MediaUtils.aesKeyToHex(aesKey));

            BaseInfo baseInfo = new BaseInfo();
            baseInfo.setChannelVersion(CHANNEL_VERSION);
            req.setBaseInfo(baseInfo);

            GetUploadUrlResp resp = getUploadUrl(credentials, req);

            String encryptQueryParam = com.lth.wechat.ilink.utils.MediaUtils.uploadToCdn(encryptedData, resp.getUpload_param(), fileKey);

            String hexAesKey = com.lth.wechat.ilink.utils.MediaUtils.aesKeyToHex(aesKey);
            byte[] hexBytes = hexAesKey.getBytes();
            String base64AesKey = Base64.getEncoder().encodeToString(hexBytes);

            return new MediaInfo(encryptQueryParam, base64AesKey, fileSize);
        } catch (Exception e) {
            throw new ILinkException("上传媒体失败", e);
        }
    }

    /**
     * 下载媒体文件
     * @param encryptQueryParam CDN加密参数
     * @param aesKey AES密钥（base64格式）
     * @return 解密后的文件数据
     */
    public byte[] downloadMedia(String encryptQueryParam, String aesKey) {
        try {
            byte[] encryptedData = com.lth.wechat.ilink.utils.MediaUtils.downloadFromCdn(encryptQueryParam);
            byte[] key = com.lth.wechat.ilink.utils.MediaUtils.processAesKey(aesKey);
            return com.lth.wechat.ilink.utils.MediaUtils.decryptAesEcb(encryptedData, key);
        } catch (Exception e) {
            throw new ILinkException("下载媒体失败", e);
        }
    }

    // ==================== 内部类 ====================

    public static class MediaInfo {
        private final String encryptQueryParam;
        private final String aesKey;
        private final long fileSize;

        public MediaInfo(String encryptQueryParam, String aesKey, long fileSize) {
            this.encryptQueryParam = encryptQueryParam;
            this.aesKey = aesKey;
            this.fileSize = fileSize;
        }

        public String getEncryptQueryParam() { return encryptQueryParam; }
        public String getAesKey() { return aesKey; }
        public long getFileSize() { return fileSize; }
    }

    // ==================== 辅助方法 ====================

    /**
     * 执行带重试的操作
     * @param action 要执行的操作
     * @param maxRetries 最大重试次数
     * @param <T> 返回类型
     * @return 操作结果
     */
    private <T> T executeWithRetry(java.util.function.Supplier<T> action, int maxRetries, String operationName) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return action.get();
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    logger.error("{} 失败，已重试 {} 次: {}", operationName, maxRetries, e.getMessage());
                    throw new ILinkException(operationName + " 失败，已重试 " + maxRetries + " 次", e);
                }
                logger.warn("{} 失败，第 {} 次重试: {}", operationName, i + 1, e.getMessage());
                try {
                    Thread.sleep(1000 * (i + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ILinkException("操作被中断", ie);
                }
            }
        }
        throw new ILinkException("重试失败");
    }

    /**
     * 检查响应状态，处理会话过期等错误
     * @param ret 响应状态码
     * @param apiName API名称（用于错误消息）
     */
    private void checkResponseCode(int ret, String apiName) {
        if (ret == -14) {
            throw new ILinkSessionExpiredException("会话已过期，请重新登录");
        } else if (ret != 0) {
            throw new ILinkException(apiName + " 失败，错误码: " + ret);
        }
    }

    /**
     * 创建登录凭证
     * @param qrcode 二维码ID
     * @param loginStatus 登录状态响应（confirmed状态）
     */
    public static LoginCredentials createCredentials(String qrcode, LoginStatusResp loginStatus) {
        if (!"confirmed".equals(loginStatus.getStatus())) {
            throw new ILinkException("登录未确认，无法创建凭证");
        }
        String botToken = loginStatus.getBot_token();
        String userId = loginStatus.getIlink_user_id();
        String apiBaseUrl = loginStatus.getBaseurl() != null ? loginStatus.getBaseurl() : DEFAULT_BASE_URL;
        return new LoginCredentials(botToken, userId, apiBaseUrl, qrcode);
    }
}