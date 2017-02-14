package com.umpay.nfcandnet.utils;

import android.util.Log;

import com.umpay.nfcandnet.common.Const;


/**
 * @author yangningbo
 * @version V1.0
 * @Description：Log工具类 <p>
 * 创建日期：2013-9-10
 * </p>
 * @see
 */
public class L {

    private static final boolean DEBUG = Const.Config.DEBUG;

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void e(String tag, Throwable tr) {
        String msg = tr.getMessage();
        e(tag, msg != null ? msg : "unknown excetion.");
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void e(String tag) {
        if (DEBUG) {
            Log.e(Const.Config.TAG, tag);
        }
    }
}
