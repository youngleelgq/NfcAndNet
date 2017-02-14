package com.umpay.nfcandnet;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.umpay.nfcandnet.common.Const;
import com.umpay.nfcandnet.data.ContentManager;
import com.umpay.nfcandnet.utils.L;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author younglee
 * @Description：
 * @DataTime 2017/2/14 14:10
 * @detailsDesc:
 */
public class NFCApplication extends Application {
    private static NFCApplication sInstance;
    private String mVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        L.d("GsNfc", this + "");
        sInstance = this;
        ContentManager.init(this);
        initOkHttp();
    }

    public String getVersion() {
        return mVersion;
    }

    private void initOkHttp() {

        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();

        // 放开日志
        if (!Const.Config.DEBUG)
            okHttpUtils.debug("okhttp");

        // 超时时间
        okHttpUtils.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpUtils.setWriteTimeout(10, TimeUnit.SECONDS);
        okHttpUtils.setReadTimeout(30, TimeUnit.SECONDS);

        // 使用https，但是默认信任全部证书
        okHttpUtils.setCertificates();
    }


    public static NFCApplication getInstance() {
        return sInstance;
    }
}
