package com.lth.wechat.ilink.exception;

public class ILinkException extends RuntimeException {
    public ILinkException(String msg) { super(msg); }
    public ILinkException(String msg, Throwable t) { super(msg, t); }
}