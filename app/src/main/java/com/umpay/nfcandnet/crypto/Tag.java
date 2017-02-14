package com.umpay.nfcandnet.crypto;


/**
 * Created by younglee on 2016/11/17.
 */
public class Tag {

    private static String sTag;


//    public static void callback() {
//        sTag = ContentManager.getInstance().getSecretKey();
//    }

    public static native String getTag(boolean debug);

    static {
        System.loadLibrary("tag");

    }
}
