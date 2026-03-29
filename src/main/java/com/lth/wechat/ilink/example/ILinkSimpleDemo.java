package com.lth.wechat.ilink.example;

import com.lth.wechat.ilink.ILinkClient;
import com.lth.wechat.ilink.LoginCredentials;
import com.lth.wechat.ilink.entity.config.GetConfigRequest;
import com.lth.wechat.ilink.entity.message.RefMessageEntity;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 微信 iLink Bot SDK Java 简单示例
 */
public class ILinkSimpleDemo {
    private static ILinkClient client = new ILinkClient();
    private static LoginCredentials credentials;
    private static String userId, contextToken, typingTicket, cursor = "";

    public static void main(String[] args) throws Exception {
        login();                    // 1. 登录
        receiveMessage();           // 2. 接收消息
        getTypingTicket();          // 3. 获取票据
        sendTextWithTyping();       // 4. 发送文本
        sendImage();                // 5. 发送图片
        sendFile();                 // 6. 发送文件
        sendVideo();                // 7. 发送视频
        sendVoice();                // 8. 发送语音
        sendReply();                // 9. 发送引用消息
    }

    private static void login() throws Exception {
        var qrCode = client.getBotQrCode();
        System.out.println("扫码: " + qrCode.getQrcode_img_content());
        while (true) {
            var status = client.getQrCodeStatus(qrCode.getQrcode());
            if ("confirmed".equals(status.getStatus())) {
                credentials = client.createCredentials(qrCode.getQrcode(), status);
                System.out.println("登录成功");
                break;
            }
            Thread.sleep(3000);
        }
    }

    private static void getTypingTicket() throws Exception {
        GetConfigRequest req = new GetConfigRequest();
        if (userId != null) req.setIlinkUserId(userId);
        if (contextToken != null) req.setContextToken(contextToken);
        var config = client.getConfig(credentials, req);
        typingTicket = config.getTyping_ticket();
    }

    private static void receiveMessage() throws Exception {
        while (userId == null) {
            var result = client.receiveMessages(credentials, cursor);
            cursor = result.getNextCursor();
            if (!result.getMessages().isEmpty()) {
                var msg = result.getMessages().get(0);
                userId = msg.getFromUserId();
                contextToken = msg.getContextToken();
            }
            Thread.sleep(3000);
        }
    }

    private static void sendTextWithTyping() throws Exception {
        if (typingTicket != null) {
            client.sendTypingStatus(credentials, userId, typingTicket, true);
            Thread.sleep(2000);
            client.sendTypingStatus(credentials, userId, typingTicket, false);
        }
        client.sendTextMessage(credentials, userId, contextToken, "Java-SDK简单示例");
    }

    private static void sendImage() throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("D:\\Desktop\\face\\img.jpg"));
        var media = client.uploadMedia(credentials, 1, userId, data);
        client.sendImageMessage(credentials, userId, contextToken, media);
    }

    private static void sendVoice() throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("D:\\Desktop\\face\\voice.silk"));
        var media = client.uploadMedia(credentials, 4, userId, data);
        client.sendVoiceMessage(credentials, userId, contextToken, media, 3000, 6);
    }

    private static void sendFile() throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("D:\\Desktop\\face\\text.txt"));
        var media = client.uploadMedia(credentials, 3, userId, data);
        client.sendFileMessage(credentials, userId, contextToken, media, "test.txt", data.length);
    }

    private static void sendVideo() throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("D:\\Desktop\\face\\video.mp4"));
        var media = client.uploadMedia(credentials, 2, userId, data);
        client.sendVideoMessage(credentials, userId, contextToken, media, data.length, 5000);
    }

    private static void sendReply() throws Exception {
        RefMessageEntity ref = new RefMessageEntity();
        ref.setTitle("引用");
        client.sendReplyMessage(credentials, userId, contextToken, ref, "回复");
    }
}