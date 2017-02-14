package com.umpay.nfcandnet.data;

import android.content.Context;

import com.umpay.nfcandnet.exception.MyHttpException;
import com.umpay.nfcandnet.model.IModel;
import com.umpay.nfcandnet.model.Request;


/**
 * @author yangningbo
 * @version V1.0
 * @Description：数据访问接口类 <p>
 * 创建日期：2013-9-9
 * </p>
 * @see
 */
public class ContentManager {

    private static ContentManager sInstance;
    private Context mContext;

    private ContentManager(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        sInstance = new ContentManager(context);
    }

    public static ContentManager getInstance() {
        return sInstance;
    }


    public void post(Request request, int mDataType, Class clazz, HttpLoader.OnLoadListener listener) {
        RemoteData.getInstance().post(request, mDataType, clazz, listener);
    }

    public <T> T syncPost(Request request, Class<? extends IModel> clazz) throws MyHttpException {
        return RemoteData.getInstance().syncPost(request, clazz);
    }


    /**
     * 取消网络请求
     *
     * @param funcode
     */
    public void cancleRemoteReq(String funcode) {
        RemoteData.getInstance().cancle(funcode);
    }

}
