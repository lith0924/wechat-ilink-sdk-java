package com.lth.wechat.ilink.exception;

public class ILinkSessionExpiredException extends ILinkException {
    public ILinkSessionExpiredException() {
        super("session 已过期(-14)，请重新登录");
    }
    
    public ILinkSessionExpiredException(String msg) {
        super(msg);
    }
}