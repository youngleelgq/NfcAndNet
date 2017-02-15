package com.zhy.http.okhttp.callback;

import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public interface ICallback<T>
{
    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request);

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter();

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress);
    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response, Class<? extends Serializable> clazz) throws Exception;

    public abstract void onError(Call call, Exception e);

    public abstract void onResponse(Object response);


//    public static ICallback CALLBACK_DEFAULT = new ICallback()
//    {
//
//        @Override
//        public Object parseNetworkResponse(Response response) throws Exception
//        {
//            return null;
//        }
//
//        @Override
//        public void onError(Call call, Exception e)
//        {
//
//        }
//
//        @Override
//        public void onResponse(Object response)
//        {
//
//        }
//    };


}