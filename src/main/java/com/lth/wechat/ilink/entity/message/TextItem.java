package com.lth.wechat.ilink.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextItem {
    private String text;
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}