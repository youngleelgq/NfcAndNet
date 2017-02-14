package com.umpay.nfcandnet.exception;

/**
 * Created by Administrator on 2016/7/22.
 */
public class RechargeException extends Exception {
    private String msg;

    public RechargeException() {
    }

    public RechargeException(String detailMessage) {
        super(detailMessage);
        this.msg = detailMessage;
    }

    public RechargeException(Throwable throwable) {
        super(throwable);
    }

    public RechargeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
