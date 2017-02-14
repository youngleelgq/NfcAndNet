package com.umpay.nfcandnet.exception;

/**
 * 
 * @Description：Apdu执行异常类 <p>
 *                        创建日期：2013-9-4
 *                        </p>
 * @version V1.0
 * @author yangningbo
 * @see
 */
public class ApduException extends Exception {

	private static final long serialVersionUID = 1L;

	public ApduException() {
	}

	public ApduException(String detailMessage) {
		super(detailMessage);
	}

	public ApduException(Throwable throwable) {
		super(throwable);
	}

	public ApduException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	@Override
	public String getMessage() {
		return "读卡错误，请重试！";
	}

}
