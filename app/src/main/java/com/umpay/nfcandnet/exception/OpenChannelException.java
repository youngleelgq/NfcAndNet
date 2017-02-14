package com.umpay.nfcandnet.exception;

/**
 * 
 * @Description：打开逻辑通道异常类
 * <p>创建日期：2013-9-4 </p>
 * @version V1.0  
 * @author yangningbo
 * @see
 */
public class OpenChannelException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final String OPEN_CHANNEL_FAILD = "打开通道失败";
	public static final String OS_NOT_SUPPORTED = "不支持类型";
	public static final String READER_ERROR = "读取失败";
	public static final String REDA_CARD_ERROR = "读卡失败";

	public OpenChannelException() {
	}

	public OpenChannelException(String detailMessage) {
		super(detailMessage);
	}

	public OpenChannelException(Throwable throwable) {
		super(throwable);
	}

	public OpenChannelException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	@Override
	public String getMessage() {
		return "读卡错误，请重试！";
	}

}
