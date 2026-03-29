package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageItem {
    private int type;
    private long create_time_ms;
    private long update_time_ms;
    private boolean is_completed;
    private String msg_id;
    private RefMessageEntity ref_msg;
    private TextItem text_item;
    private ImageItem image_item;
    private VoiceItem voice_item;
    private FileItem file_item;
    private VideoItem video_item;

    public int getType() { return type; }
    public long getCreate_time_ms() { return create_time_ms; }
    public long getUpdate_time_ms() { return update_time_ms; }
    public boolean isIs_completed() { return is_completed; }
    public String getMsg_id() { return msg_id; }
    public RefMessageEntity getRef_msg() { return ref_msg; }
    public TextItem getText_item() { return text_item; }
    public ImageItem getImage_item() { return image_item; }
    public VoiceItem getVoice_item() { return voice_item; }
    public FileItem getFile_item() { return file_item; }
    public VideoItem getVideo_item() { return video_item; }
    public void setType(int type) { this.type = type; }
    public void setText_item(TextItem text_item) { this.text_item = text_item; }
    public void setImage_item(ImageItem image_item) { this.image_item = image_item; }
    public void setVoice_item(VoiceItem voice_item) { this.voice_item = voice_item; }
    public void setFile_item(FileItem file_item) { this.file_item = file_item; }
    public void setVideo_item(VideoItem video_item) { this.video_item = video_item; }
    public void setRef_msg(RefMessageEntity ref_msg) { this.ref_msg = ref_msg; }
    public void setMsg_id(String msg_id) { this.msg_id = msg_id; }
    public void setCreate_time_ms(long create_time_ms) { this.create_time_ms = create_time_ms; }
    public void setUpdate_time_ms(long update_time_ms) { this.update_time_ms = update_time_ms; }
    public void setIs_completed(boolean is_completed) { this.is_completed = is_completed; }
}