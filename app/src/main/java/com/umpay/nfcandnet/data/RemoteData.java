package com.umpay.nfcandnet.data;

import com.google.gson.Gson;
import com.umpay.nfcandnet.NFCApplication;
import com.umpay.nfcandnet.exception.MyHttpException;
import com.umpay.nfcandnet.model.IModel;
import com.umpay.nfcandnet.model.Request;
import com.umpay.nfcandnet.model.Response;
import com.umpay.nfcandnet.utils.L;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：远程数据接口类 <p>
 * 创建日期：2013-9-9
 * </p>
 * @see
 */
public class RemoteData {

    private static final String DOMAIN = "ip";

    public static final String URL_API = "/";

    private final String mBaseUrl;
    private static RemoteData sInstance;

    private RemoteData(String domain, String clientVersion) {
        mBaseUrl = "http://" + domain;
    }

    public static void init() {

        sInstance = new RemoteData(DOMAIN, NFCApplication
                .getInstance().getVersion());
    }

    public static RemoteData getInstance() {
        return sInstance;
    }

    public static String getDomain() {
        return DOMAIN;
    }

    private String fullUrl(String url) {
        return mBaseUrl + url;
    }


    /**
     * @param @param  req
     * @param @return 设定文件
     * @return String 返回类型
     * @throws MyHttpException toEncData
     * @throws
     * @Title: toEncData
     * @Description: 对json串序列化成字符串，做des加密，再对对称秘钥做非对称加密，秘钥放在报文头中
     */
    private String toEncData_(Object object) throws MyHttpException {
        String jsonStr = new Gson().toJson(object);
        L.d("HttpApi", "============== url: ==============" + fullUrl(URL_API));
        L.d("HttpApi", "请求参数：" + jsonStr);
//        return SafeUtil.getEncData(jsonStr);
        return jsonStr;
    }


    /**
     * 异步网络请求
     *
     * @param request
     * @param dataType
     * @param listener
     * @param clazz
     */
    public void post(Request request, final int dataType, final Class clazz, final HttpLoader.OnLoadListener listener) {

        StringCallback callback = new StringCallback() {

            @Override
            public void onBefore(okhttp3.Request request) {
                super.onBefore(request);
                listener.onDataStart(dataType);
            }

            /**
             * onAfter 在onError或者onResponse之后才调用，用来关闭连接，释放资源；
             * 不能在这里调用listener.onDataFinish(dataType);因为我们很多时候在这个方法里去结束转圈，
             * onResponse结束的时候有可能会销毁窗口，如果在这里才调用onDataFinish方法会导致dialog在activity
             * 销毁后才销毁，会导致Activity has leaked window的错误
             */
            @Override
            public void onAfter() {
                super.onAfter();

            }

            @Override
            public void onError(Call call, Exception e) {

                if (call.isCanceled())
                    return;

                e.printStackTrace();
                listener.onDataFinish(dataType);
                if (e instanceof SocketTimeoutException) {
                    listener.onDataFail(e, dataType);
                } else {
                    listener.onDataFail(new MyHttpException(MyHttpException.CODE_SYSTEM_EXCEPTION), dataType);
                }
            }

            @Override
            public void onResponse(String response) {
                listener.onDataFinish(dataType);
                String content = "";
                try {
                    content = response;
//                    content = SafeUtil.getDecData(response);
                    L.d("HttpApi", "响应参数：" + content);
                } catch (Exception e) {
                    listener.onDataFail(new MyHttpException(MyHttpException.CODE_DEC_EXCEPTION), dataType);
                }
                IModel model = (IModel) new Gson().fromJson(content, clazz);

                if (model instanceof Response) {
                    Response rsp = (Response) model;
                    listener.onDataGet(model, dataType); // 回调接口的数据执行方法
                }
            }

            @Override
            public void inProgress(float progress) {

            }
        };

        //解决乱码增加回调
        Callback callbackGBK = new Callback() {

            @Override
            public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                return new String(response.body().bytes(), "gbk");
            }

            @Override
            public void onBefore(okhttp3.Request request) {
                super.onBefore(request);
                listener.onDataStart(dataType);
            }

            @Override
            public void onAfter() {
                super.onAfter();
            }

            @Override
            public void onError(Call call, Exception e) {
                if (call.isCanceled())
                    return;

                e.printStackTrace();
                listener.onDataFinish(dataType);
                if (e instanceof SocketTimeoutException) {
                    listener.onDataFail(e, dataType);
                } else {
                    listener.onDataFail(new MyHttpException(MyHttpException.CODE_SYSTEM_EXCEPTION), dataType);
                }
            }

            @Override
            public void onResponse(Object response) {
                listener.onDataFinish(dataType);
                String content = "";
                try {
                    content = String.valueOf(response);
                    L.d("HttpApi", "响应参数：" + content);
                } catch (Exception e) {
                    listener.onDataFail(new MyHttpException(MyHttpException.CODE_DEC_EXCEPTION), dataType);
                }
                IModel model = (IModel) new Gson().fromJson(content, clazz);

                if (model instanceof Response) {
                    Response rsp = (Response) model;
                    listener.onDataGet(model, dataType); // 回调接口的数据执行方法
                }
            }
        };

        try {
            OkHttpUtils
                    .postString()
                    .url(fullUrl(URL_API))
                    .mediaType(MediaType.parse("application/json; charset=gb2312"))
                    .content(toEncData_(request))
                    .tag(request.getFunCode())
                    .build()
                    .execute(callbackGBK);
        } catch (Exception e) {
            listener.onDataFinish(dataType);
            if (e instanceof MyHttpException)
                listener.onDataFail(e, dataType);
            else
                listener.onDataFail(new MyHttpException(MyHttpException.CODE_SYSTEM_EXCEPTION), dataType);
        }

    }


    /**
     * 取消请求
     *
     * @param funcode
     */
    public void cancle(String funcode) {

        L.d("HttpApi", "请求已经取消,funcode(" + funcode + ")");
        OkHttpUtils.getInstance().cancelTag(funcode);
    }

    /**
     * 同步网络请求
     *
     * @param request
     * @param clazz
     * @param <T>
     * @return
     * @throws MyHttpException
     */
    public <T> T syncPost(Request request, Class<? extends IModel> clazz) throws MyHttpException {
        String content = null;
        try {
            okhttp3.Response response = OkHttpUtils
                    .postString()
                    .url(fullUrl(URL_API))
                    .mediaType(MediaType.parse("application/json; charset=gb2312"))
                    .content(toEncData_(request))
                    .tag(request.getFunCode())
                    .build()
                    .execute();

//            content = response.body().string();
            content = new String(response.body().bytes(), "gbk");
            L.d("HttpApi", "响应参数：" + content);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyHttpException(MyHttpException.CODE_SYSTEM_EXCEPTION, "网络异常，请稍后再试！");
        }

        return (T) new Gson().fromJson(content, clazz);
    }
}
