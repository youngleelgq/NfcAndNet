package com.umpay.nfcandnet.exception;

/**
 * Created by Administrator on 2016/7/12.
 */
public class CardException extends Exception {
    private String msg;

    public CardException() {
    }

    public CardException(String detailMessage) {
        super(detailMessage);
        this.msg = detailMessage;
    }

    public CardException(Throwable throwable) {
        super(throwable);
    }

    public CardException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
