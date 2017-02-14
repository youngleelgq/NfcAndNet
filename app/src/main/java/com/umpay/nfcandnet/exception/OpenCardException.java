package com.umpay.nfcandnet.exception;

/**
 * Created by Administrator on 2016/7/22.
 */
public class OpenCardException extends Exception {
    private String msg;

    public OpenCardException() {
    }

    public OpenCardException(String detailMessage) {
        super(detailMessage);
        this.msg = detailMessage;
    }

    public OpenCardException(Throwable throwable) {
        super(throwable);
    }

    public OpenCardException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
