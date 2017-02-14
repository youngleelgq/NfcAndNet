package com.umpay.nfcandnet.common;

/**
 * @author younglee
 * @Description：
 * @DataTime 2017/2/14 13:46
 * @detailsDesc:
 */
@SuppressWarnings("JavaDoc")
public class Const {
    public interface Config {
        boolean DEBUG = false;
        String TAG = "TAG";
    }

    /**
     * 常量，用来判断当前网络是WIFI，MOBILE还是无网络
     */
    public interface Network {
        int NONE = 0;
        int WIFI = 1;
        int MOBILE = 2;
    }

    public interface IntentExtraKey {

        String CHANEL_TYPE = "type";
    }

    public interface ChannelType {
        int NFC = 0;
        int SWP_SIM = 1;
    }
}
