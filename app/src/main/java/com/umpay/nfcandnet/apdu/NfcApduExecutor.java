package com.umpay.nfcandnet.apdu;

import android.nfc.tech.IsoDep;
import android.os.AsyncTask;

import com.umpay.nfcandnet.common.Const;
import com.umpay.nfcandnet.exception.ApduException;
import com.umpay.nfcandnet.exception.OpenChannelException;
import com.umpay.nfcandnet.utils.L;
import com.umpay.nfcandnet.utils.TransportUtils;
import com.umpay.nfcandnet.utils.Utils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NfcApduExecutor implements ApduExecutor {

    private static final String TAG = NfcApduExecutor.class.getSimpleName();

    private static final ExecutorService SINGLE_TASK_EXECUTOR = (ExecutorService) Executors
            .newSingleThreadExecutor();

    private IsoDep mIsoDep;
    private ApduExecutorListener mListener;

    private RunApduTask mRunApduTask;

    /**
     * apdu请求id
     */
    private int mReqId;

    public NfcApduExecutor(IsoDep tag, ApduExecutorListener listener) {
        mIsoDep = tag;
        if (listener != null) {
            mListener = listener;
        } else {
            mListener = new ExecutorListener();
        }
    }


    public boolean connect(int reqId) throws OpenChannelException {
        mReqId = reqId;
        try {
            if (!mIsoDep.isConnected()) {
                mIsoDep.connect();
            }
            mListener.onStart(mReqId);
            mListener.onConnected(mReqId);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            // mListener.onFailed(e);
            throw new OpenChannelException(OpenChannelException.OPEN_CHANNEL_FAILD);
        } catch (Exception e) {
            e.printStackTrace();
            // mListener.onFailed(e);
            throw new OpenChannelException(OpenChannelException.OS_NOT_SUPPORTED);
        } catch (Error e) {
            e.printStackTrace();
            throw new OpenChannelException(OpenChannelException.OS_NOT_SUPPORTED);
        }
    }


    @Override
    public boolean isConnect() {
        return mIsoDep.isConnected();
    }

    /**
     * ******************************************** method name : shutdown
     * description : 取消现在正在执行的任务，并关闭SEService远程服务
     *
     * @param : modified : yangningbo , 2013-10-12 下午4:06:51
     * @return : void
     * @see : *******************************************
     */
    public void shutdown() {
        if (mRunApduTask != null && !mRunApduTask.isCancelled()) {
            mRunApduTask.cancel(true);
        }

        if (mIsoDep != null && mIsoDep.isConnected()) {
            try {
                mIsoDep.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void runApdu(ApduRequest a) {
        if (mRunApduTask != null && !mRunApduTask.isCancelled()) {
            mRunApduTask.cancel(true);
        }

        mRunApduTask = new RunApduTask();
        mRunApduTask.executeOnExecutor(SINGLE_TASK_EXECUTOR, a);
    }

    public byte[] execute(String apdu) throws Exception {
        try {
            L.e(TAG, "-> " + apdu);
            byte[] rsp = mIsoDep.transceive(Utils.hexStringToBytes(apdu));
            L.e(TAG, "<- " + TransportUtils.bytesToHexString(rsp));
            checkError(rsp);
            return rsp;
        } catch (Exception e) {
            throw new ApduException(e);
        }
    }

    /**
     * ********************************************
     * 在进行互联互通卡片检测时使用，其他情况下使用excute方法
     * method name   : executeHtCard
     * description   : 执行apdu指令，并返回响应数据
     *
     * @param : @param channel 逻辑通道
     * @param : @param apdu apdu指令
     * @param : @return 卡片返回的响应字节
     * @param : @throws Exception
     *          modified      : yangningbo ,  2013-10-12  下午4:08:51
     * @return : byte[] 卡片返回的响应字节
     * @see :
     * *******************************************
     */
    @Override
    public byte[] executeNotCheck(String apdu) throws Exception {
        try {
            if (Const.Config.DEBUG) {
                L.d(TAG, "-> " + apdu);
            }
            byte[] rsp = mIsoDep.transceive(Utils.hexStringToBytes(apdu));
            if (Const.Config.DEBUG) {
                L.d(TAG, "<- " + TransportUtils.bytesToHexString(rsp));
            }
            return rsp;
        } catch (Exception e) {
            throw new ApduException(e);
        }
    }


    private class RunApduTask extends
            AsyncTask<ApduRequest, Void, ApduResponse> {

        private Exception ex;

        @Override
        protected ApduResponse doInBackground(ApduRequest... params) {
            try {
                return params[0].run(NfcApduExecutor.this);
            } catch (Exception e) {
                e.printStackTrace();
                ex = e;
            } finally {
                try {
                    if (mIsoDep != null && mIsoDep.isConnected())
                        mIsoDep.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApduResponse result) {
            super.onPostExecute(result);
            mListener.onFinished();
            if (ex != null) {
                mListener.onFailed(ex);
            } else {
                mListener.onCompleted(result);
            }
        }

    }

    /**
     * @author yangningbo
     * @ClassName: ExecutorListener
     * @Description: 一个空实现，谨防出差错
     * @date 2015-4-9 上午10:33:24
     */
    private class ExecutorListener implements ApduExecutorListener {

        @Override
        public void onStart(int reqId) {

        }

        @Override
        public void onConnected(int reqId) {
        }

        @Override
        public void onFailed(Exception e) {
        }

        @Override
        public void onCompleted(ApduResponse rsp) {
        }

        @Override
        public void onFinished() {

        }

    }

    private void checkError(byte[] rsp) throws Exception {
        if (!(TransportUtils.getSW1SW2(rsp) == 0x9000)) {
            throw new Exception(TransportUtils.bytesToHexString(rsp));
        }
    }

}
