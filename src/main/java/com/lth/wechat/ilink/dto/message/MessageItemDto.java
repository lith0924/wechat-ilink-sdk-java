package com.lth.wechat.ilink.dto.message;

import java.util.Date;

/**
 * 消息内容项（用户友好版）
 */
public class MessageItemDto {
    public enum Type {
        TEXT,   // 文本
        IMAGE,  // 图片
        VOICE,  // 语音
        FILE,   // 文件
        VIDEO   // 视频
    }

    private final Type type;
    private final Date createTime;
    private final Date updateTime;
    private final boolean completed;
    private final String msgId;
    private final RefMessage refMessage;
    private final String text;
    private final ImageContent image;
    private final VoiceContent voice;
    private final FileContent file;
    private final VideoContent video;

    public MessageItemDto(Type type, Date createTime, Date updateTime, boolean completed, 
                       String msgId, RefMessage refMessage, String text, 
                       ImageContent image, VoiceContent voice, FileContent file, VideoContent video) {
        this.type = type;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.completed = completed;
        this.msgId = msgId;
        this.refMessage = refMessage;
        this.text = text;
        this.image = image;
        this.voice = voice;
        this.file = file;
        this.video = video;
    }

    public Type getType() {
        return type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getMsgId() {
        return msgId;
    }

    public RefMessage getRefMessage() {
        return refMessage;
    }

    public String getText() {
        return text;
    }

    public ImageContent getImage() {
        return image;
    }

    public VoiceContent getVoice() {
        return voice;
    }

    public FileContent getFile() {
        return file;
    }

    public VideoContent getVideo() {
        return video;
    }

    public boolean isText() {
        return type == Type.TEXT;
    }

    public boolean isImage() {
        return type == Type.IMAGE;
    }

    public boolean isVoice() {
        return type == Type.VOICE;
    }

    public boolean isFile() {
        return type == Type.FILE;
    }

    public boolean isVideo() {
        return type == Type.VIDEO;
    }
}