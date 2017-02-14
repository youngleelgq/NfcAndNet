package com.umpay.nfcandnet.data;


import com.umpay.nfcandnet.model.IModel;

/**
 * description : 异步数据加载器,运行子线程中
 */
public class HttpLoader {

    /**
     * 数据类型
     */
    private int mDataType = 0;


    /**
     * 回调接口类
     */
    private OnLoadListener mListener = null;


    public HttpLoader(int dataType) {
        mDataType = dataType;

        // 初始化默认回调接口监听
        mListener = new OnLoadListener() {

            @Override
            public void doPost(int dataType) {
            }

            @Override
            public void onDataStart(int dataType) {
            }

            @Override
            public void onDataGet(IModel result, int dataType) {
            }

            @Override
            public void onDataFail(Exception e, int dataType) {
            }

            @Override
            public void onDataFinish(int dataType) {
            }
        };
    }

    /**
     * setOnLoadListener description : 设置异步回调接口
     */
    public void setOnLoadListener(OnLoadListener listener) {
        if (listener != null) {
            mListener = listener;
        }
    }

    public void execute() {
        mListener.doPost(mDataType);
    }

    /**
     * description : 异步数据回调接口类
     */
    public interface OnLoadListener {

        void doPost(int dataType);

        void onDataStart(int dataType);

        void onDataGet(IModel result, int dataType);

        void onDataFail(Exception e, int dataType);

        void onDataFinish(int dataType);
    }
}

