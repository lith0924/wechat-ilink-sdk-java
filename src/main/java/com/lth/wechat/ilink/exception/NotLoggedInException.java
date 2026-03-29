package com.lth.wechat.ilink.exception;

/**
 * 未登录异常
 */
public class NotLoggedInException extends ILinkException {
    public NotLoggedInException(String message) {
        super(message);
    }

    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}