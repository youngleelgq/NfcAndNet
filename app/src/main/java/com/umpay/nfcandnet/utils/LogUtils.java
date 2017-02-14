package com.umpay.nfcandnet.utils;


import com.umpay.nfcandnet.common.Const;

/**
 * Created by Administrator on 2016/10/25.
 */
public class LogUtils {
    public static void e(String msg){
        if(Const.Config.DEBUG){
            L.e(Const.Config.TAG,msg);
        }
    }
}
