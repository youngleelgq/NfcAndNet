package com.umpay.nfcandnet.exception;

import android.text.TextUtils;


import com.umpay.nfcandnet.utils.L;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：联网请求异常类 <p>
 * 创建日期：2013-9-10
 * </p>
 * @see
 */
public class MyHttpException extends Exception {

    private static final long serialVersionUID = 1L;
    private static HashMap<Integer, String> sCodeMap;

    public static final int CODE_SOCKET_TIMEOUT_EXCEPTION = 18001;
    public static final int CODE_SOCKET_EXCEPTION = 18002;
    public static final int CODE_IO_EXCEPTION = 18003;
    public static final int CODE_PARSE_EXCEPTION = 18004;
    public static final int CODE_UNKNOWN_EXCEPTION = 18005;
    public static final int CODE_JSON_EXCEPTION = 18006;
    public static final int CODE_SYSTEM_EXCEPTION = 18007;
    public static final int CODE_ENC_EXCEPTION = 18008;
    public static final int CODE_DEC_EXCEPTION = 18009;

    //	public static final String MESSAGE_DEFAULT_EXCEPTION = "系统异常，请稍后再试。";
    public static final String MESSAGE_DEFAULT_EXCEPTION = "网络异常，请稍后再试。";
    public static final String MESSAGE_UNKNOWN_EXCEPTION = "未知异常，请稍后重试！";
    private static final String TAG = MyHttpException.class.getSimpleName();

    static {
        sCodeMap = new HashMap<Integer, String>();

        sCodeMap.put(400, "请求错误，请稍后重试！");
        sCodeMap.put(401, "请求未授权，请稍后重试！");
        sCodeMap.put(403, "请求被拒绝，请稍后重试！");
        sCodeMap.put(404, "请求地址不存在，请稍后重试！");
        sCodeMap.put(500, "服务器异常，请稍后重试！");
        sCodeMap.put(501, "服务器不支持，请稍后重试！");
        sCodeMap.put(502, "无法连接到网关，请稍后重试！");
        sCodeMap.put(503, "服务器正忙，请稍后重试！");
        sCodeMap.put(504, "服务器请求响应超时，请稍后重试！");

        sCodeMap.put(CODE_SOCKET_TIMEOUT_EXCEPTION, "服务器响应超时，请稍后重试！");
        sCodeMap.put(CODE_SOCKET_EXCEPTION, "服务器没有响应，请稍后重试！");
        sCodeMap.put(CODE_IO_EXCEPTION, "未知网络异常，请稍后重试！");
        sCodeMap.put(CODE_PARSE_EXCEPTION, "数据解析错误，请稍后重试！");
        sCodeMap.put(CODE_ENC_EXCEPTION, "数据加密错误，请稍后再试！");
        sCodeMap.put(CODE_DEC_EXCEPTION, "数据解密错误，请稍后再试！");
        sCodeMap.put(CODE_SYSTEM_EXCEPTION, "系统异常，请稍后再试！");
        sCodeMap.put(CODE_UNKNOWN_EXCEPTION, "未知异常，请稍后重试！");
    }

    private int mCode;

    public int getCode() {
        return mCode;
    }

    private String mMsg;

    public MyHttpException(int code) {
        this.mCode = code;
    }

    public MyHttpException(String msg) {
        mMsg = msg;
    }

    public MyHttpException(int code, String msg) {
        this.mCode = code;
        mMsg = msg;
    }

    public MyHttpException(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            L.i(TAG, "CODE_SOCKET_TIMEOUT_EXCEPTION");
            mCode = CODE_SOCKET_TIMEOUT_EXCEPTION;
        } else if (e instanceof SocketException) {
            L.i(TAG, "CODE_SOCKET_EXCEPTION");
            mCode = CODE_SOCKET_EXCEPTION;
        } else if (e instanceof IOException) {
            L.i(TAG, "CODE_IO_EXCEPTION");
            mCode = CODE_IO_EXCEPTION;
        } else if (e instanceof JSONException) {
            L.i(TAG, "CODE_JSON_EXCEPTION");
            mCode = CODE_JSON_EXCEPTION;
        } else {
            L.i(TAG, "OTHER_EXCEPTION");
            mCode = CODE_UNKNOWN_EXCEPTION;

        }
    }

    @Override
    public String getMessage() {
        if (!TextUtils.isEmpty(mMsg)) {
            L.i(TAG, mMsg);
            return mMsg;
        }

        return MESSAGE_DEFAULT_EXCEPTION;// + "(" + mCode + ")"
    }

}
