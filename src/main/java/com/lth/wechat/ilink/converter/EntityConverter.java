package com.lth.wechat.ilink.converter;

import com.lth.wechat.ilink.dto.login.LoginInfo;
import com.lth.wechat.ilink.dto.login.LoginStatus;
import com.lth.wechat.ilink.dto.login.QrCodeInfo;
import com.lth.wechat.ilink.dto.message.*;
import com.lth.wechat.ilink.entity.login.LoginStatusResp;
import com.lth.wechat.ilink.entity.login.QrCodeResp;
import com.lth.wechat.ilink.entity.message.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 实体转换器：将原始实体转换为用户友好的DTO
 */
public class EntityConverter {

    /**
     * 转换为二维码信息
     */
    public static QrCodeInfo toQrCodeInfo(QrCodeResp resp) {
        if (resp == null) {
            return null;
        }
        return new QrCodeInfo(
            resp.getQrcode(),
            resp.getQrcode_img_content()
        );
    }

    /**
     * 转换为登录信息（从登录状态响应）
     */
    public static LoginInfo toLoginInfo(LoginStatusResp resp) {
        if (resp == null) {
            return null;
        }
        return new LoginInfo(
            resp.getBot_token(),
            resp.getIlink_bot_id(),
            resp.getIlink_user_id(),
            resp.getBaseurl()
        );
    }

    /**
     * 转换为登录状态
     */
    public static LoginStatus toLoginStatus(LoginStatusResp resp) {
        if (resp == null) {
            return new LoginStatus(LoginStatus.Status.WAIT, null, null, null, null);
        }
        LoginStatus.Status status;
        switch (resp.getStatus()) {
            case "wait":
                status = LoginStatus.Status.WAIT;
                break;
            case "scaned":
                status = LoginStatus.Status.SCANED;
                break;
            case "confirmed":
                status = LoginStatus.Status.CONFIRMED;
                break;
            case "expired":
                status = LoginStatus.Status.EXPIRED;
                break;
            default:
                status = LoginStatus.Status.WAIT;
        }
        return new LoginStatus(
            status,
            resp.getBot_token(),
            resp.getIlink_bot_id(),
            resp.getIlink_user_id(),
            resp.getBaseurl()
        );
    }

    /**
     * 转换消息列表结果
     */
    public static MessagesResult toMessagesResult(GetUpdatesResponse resp) {
        if (resp == null) {
            return new MessagesResult(null, null, 0);
        }
        List<Message> messages = new ArrayList<>();
        if (resp.getMsgs() != null) {
            for (WeixinMessage msg : resp.getMsgs()) {
                messages.add(toMessage(msg));
            }
        }
        return new MessagesResult(
            messages,
            resp.getGet_updates_buf(),
            resp.getLongpolling_timeout_ms()
        );
    }

    /**
     * 转换接收消息结果
     */
    public static ReceiveMessagesResult toReceiveMessagesResult(GetUpdatesResponse resp) {
        if (resp == null) {
            return new ReceiveMessagesResult(null, null, 0);
        }
        List<WeixinMessageDto> messages = new ArrayList<>();
        if (resp.getMsgs() != null) {
            for (WeixinMessage msg : resp.getMsgs()) {
                messages.add(toWeixinMessageDto(msg));
            }
        }
        return new ReceiveMessagesResult(
            messages,
            resp.getGet_updates_buf(),
            resp.getLongpolling_timeout_ms()
        );
    }

    /**
     * 转换单条消息
     */
    public static Message toMessage(WeixinMessage msg) {
        if (msg == null) {
            return null;
        }
        List<MessageContent> contents = new ArrayList<>();
        if (msg.getItem_list() != null) {
            for (MessageItem item : msg.getItem_list()) {
                MessageContent content = toMessageContent(item);
                if (content != null) {
                    contents.add(content);
                }
            }
        }
        return new Message(
            msg.getMessage_id(),
            msg.getFrom_user_id(),
            msg.getTo_user_id(),
            msg.getCreate_time_ms(),
            msg.getSession_id(),
            msg.getContext_token(),
            contents
        );
    }

    /**
     * 转换微信消息
     */
    public static WeixinMessageDto toWeixinMessageDto(WeixinMessage msg) {
        if (msg == null) {
            return null;
        }
        List<MessageItemDto> items = new ArrayList<>();
        if (msg.getItem_list() != null) {
            for (MessageItem item : msg.getItem_list()) {
                MessageItemDto itemDto = toMessageItemDto(item);
                if (itemDto != null) {
                    items.add(itemDto);
                }
            }
        }
        return new WeixinMessageDto(
            msg.getSeq(),
            msg.getMessage_id(),
            msg.getFrom_user_id(),
            msg.getTo_user_id(),
            msg.getClient_id(),
            new Date(msg.getCreate_time_ms()),
            new Date(msg.getUpdate_time_ms()),
            msg.getDelete_time_ms() > 0 ? new Date(msg.getDelete_time_ms()) : null,
            msg.getSession_id(),
            msg.getGroup_id(),
            msg.getMessage_type() == 1 ? WeixinMessageDto.Type.USER : WeixinMessageDto.Type.BOT,
            toMessageState(msg.getMessage_state()),
            items,
            msg.getContext_token()
        );
    }

    /**
     * 转换消息状态
     */
    private static WeixinMessageDto.State toMessageState(int state) {
        switch (state) {
            case 0:
                return WeixinMessageDto.State.NEW;
            case 1:
                return WeixinMessageDto.State.GENERATING;
            case 2:
                return WeixinMessageDto.State.FINISH;
            default:
                return WeixinMessageDto.State.NEW;
        }
    }

    /**
     * 转换为用户方便的消息内容项
     */
    public static MessageItemDto toMessageItemDto(MessageItem item) {
        if (item == null) {
            return null;
        }
        Date createTime = item.getCreate_time_ms() > 0 ? new Date(item.getCreate_time_ms()) : null;
        Date updateTime = item.getUpdate_time_ms() > 0 ? new Date(item.getUpdate_time_ms()) : null;
        
        return new MessageItemDto(
            toMessageType(item.getType()),
            createTime,
            updateTime,
            item.isIs_completed(),
            item.getMsg_id(),
            toRefMessage(item.getRef_msg()),
            item.getText_item() != null ? item.getText_item().getText() : null,
            toImageContent(item.getImage_item()),
            toVoiceContent(item.getVoice_item()),
            toFileContent(item.getFile_item()),
            toVideoContent(item.getVideo_item())
        );
    }

    /**
     * 转换消息类型
     */
    private static MessageItemDto.Type toMessageType(int type) {
        switch (type) {
            case 1:
                return MessageItemDto.Type.TEXT;
            case 2:
                return MessageItemDto.Type.IMAGE;
            case 3:
                return MessageItemDto.Type.VOICE;
            case 4:
                return MessageItemDto.Type.FILE;
            case 5:
                return MessageItemDto.Type.VIDEO;
            default:
                return MessageItemDto.Type.TEXT;
        }
    }

    /**
     * 转换引用消息
     */
    private static com.lth.wechat.ilink.dto.message.RefMessage toRefMessage(com.lth.wechat.ilink.entity.message.RefMessageEntity refMsg) {
        if (refMsg == null) {
            return null;
        }
        return new com.lth.wechat.ilink.dto.message.RefMessage(
            refMsg.getTitle(),
            toMessageItemDto(refMsg.getMessage_item())
        );
    }

    /**
     * 转换消息内容
     */
    public static MessageContent toMessageContent(MessageItem item) {
        if (item == null) {
            return null;
        }
        switch (item.getType()) {
            case 1:
                return toTextContent(item.getText_item());
            case 2:
                return toImageContent(item.getImage_item());
            case 3:
                return toVoiceContent(item.getVoice_item());
            case 4:
                return toFileContent(item.getFile_item());
            case 5:
                return toVideoContent(item.getVideo_item());
            default:
                return null;
        }
    }

    /**
     * 转换文本内容
     */
    public static TextContent toTextContent(TextItem item) {
        if (item == null) {
            return null;
        }
        return new TextContent(item.getText());
    }

    /**
     * 转换图片内容
     */
    public static ImageContent toImageContent(ImageItem item) {
        if (item == null) {
            return null;
        }
        return new ImageContent(
            item.getMedia() != null ? item.getMedia().getEncryptQueryParam() : null,
            item.getMedia() != null ? item.getMedia().getAesKey() : null,
            item.getMedia() != null ? item.getMedia().getEncryptType() : 0,
            item.getUrl(),
            item.getMidSize(),
            item.getThumbSize(),
            item.getThumbWidth(),
            item.getThumbHeight(),
            item.getHdSize()
        );
    }

    /**
     * 转换语音内容
     */
    public static VoiceContent toVoiceContent(VoiceItem item) {
        if (item == null) {
            return null;
        }
        return new VoiceContent(
            item.getMedia() != null ? item.getMedia().getEncryptQueryParam() : null,
            item.getMedia() != null ? item.getMedia().getAesKey() : null,
            item.getMedia() != null ? item.getMedia().getEncryptType() : 0,
            item.getEncodeType(),
            item.getBitsPerSample(),
            item.getSampleRate(),
            item.getPlaytime(),
            item.getText()
        );
    }

    /**
     * 转换文件内容
     */
    public static FileContent toFileContent(FileItem item) {
        if (item == null) {
            return null;
        }
        return new FileContent(
            item.getMedia() != null ? item.getMedia().getEncryptQueryParam() : null,
            item.getMedia() != null ? item.getMedia().getAesKey() : null,
            item.getMedia() != null ? item.getMedia().getEncryptType() : 0,
            item.getFileName(),
            item.getMd5(),
            item.getLen()
        );
    }

    /**
     * 转换视频内容
     */
    public static VideoContent toVideoContent(VideoItem item) {
        if (item == null) {
            return null;
        }
        return new VideoContent(
            item.getMedia() != null ? item.getMedia().getEncryptQueryParam() : null,
            item.getMedia() != null ? item.getMedia().getAesKey() : null,
            item.getMedia() != null ? item.getMedia().getEncryptType() : 0,
            item.getVideoSize(),
            item.getPlayLength(),
            item.getVideoMd5(),
            item.getThumbMedia() != null ? item.getThumbMedia().getEncryptQueryParam() : null,
            item.getThumbMedia() != null ? item.getThumbMedia().getAesKey() : null,
            item.getThumbMedia() != null ? item.getThumbMedia().getEncryptType() : 0,
            item.getThumbSize(),
            item.getThumbWidth(),
            item.getThumbHeight()
        );
    }
}