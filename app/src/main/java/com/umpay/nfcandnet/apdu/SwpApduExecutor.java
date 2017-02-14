package com.umpay.nfcandnet.apdu;
/**
 * ****************  JAVA头文件说明  ****************
 * file name  :  ApduExecutor.java
 * owner      :  yangningbo
 * copyright  :  UMPAY
 * description:  apdu执行器，可以在子线程中批量执行apdu指令
 * modified   :  2013-10-12
 *************************************************/

import android.content.Context;
import android.os.AsyncTask;

import com.umpay.nfcandnet.exception.ApduException;
import com.umpay.nfcandnet.exception.OpenChannelException;
import com.umpay.nfcandnet.utils.L;
import com.umpay.nfcandnet.utils.Utils;

import org.simalliance.openmobileapi.Channel;
import org.simalliance.openmobileapi.Reader;
import org.simalliance.openmobileapi.SEService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * ******************  类说明  *********************
 * class       :  ApduExecutor
 *
 * @author :  yangningbo
 * @version :  1.0
 *          description :  Apdu指令执行器，从ApduRequest对象中取出指令执行后封装到ApduResponse对象中
 * @see :
 * ***********************************************
 */
public class SwpApduExecutor implements SEService.CallBack, ApduExecutor {

    private static final String TAG = "ApduExecutor";
    /**
     * 创建一个单线程的线程池
     */
    private static final ExecutorService SINGLE_TASK_EXECUTOR = (ExecutorService) Executors
            .newSingleThreadExecutor();

    /**
     * android上下文对象
     */
    private Context mContext;
    /**
     * apdu执行器监听器
     */
    private ApduExecutorListener mListener;

    /**
     * 远程SEService服务对象
     */
    private SEService mSEService;
    /**
     * 读卡器对象
     */
    private Reader mReader;
    /**
     * 逻辑通道对象
     */
    private Channel mChannel;
    /**
     * 异步执行apdu任务对象
     */
    private RunApduTask mRunApduTask;
    /**
     * apdu请求id
     */
    private int mReqId;

    /**
     * @param context  android上下文环境
     * @param listener apdu执行器监听器
     */
    public SwpApduExecutor(Context context,
                           ApduExecutorListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("context can not null.");
        }

        if (listener == null) {
            throw new IllegalArgumentException("listener can not null.");
        }

        mContext = context;
        mListener = listener;
    }

    public SwpApduExecutor() {

    }

    @Override
    public boolean connect(int reqId) throws OpenChannelException {
        mReqId = reqId;

        mListener.onStart(reqId);
        L.i(TAG, "Connecting to SmartCardService...");
        try {
            mSEService = new SEService(mContext, this);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onFinished();
            mListener.onFailed(e);
            if (e instanceof SecurityException) {
                L.e(TAG,
                        "Binding not allowed, permission 'org.simalliance.openmobileapi.SMARTCARD' not sufficient?");
            } else {
                L.e(TAG, e.toString());
            }
            throw new OpenChannelException(OpenChannelException.OPEN_CHANNEL_FAILD);

        } catch (Error e) {
            e.printStackTrace();
            L.e(TAG, "Os not supported.");
            throw new OpenChannelException(OpenChannelException.OS_NOT_SUPPORTED, e);
        }
    }


    @Override
    public boolean isConnect() {
        if (mChannel != null)
            return mChannel.isClosed();
        return false;
    }

    /**
     * ********************************************
     * method name   : getReader
     * description   : 获取读卡器对象
     *
     * @param : @return
     *          modified      : yangningbo ,  2013-10-12  下午4:04:03
     * @return : Reader
     * @see :
     * *******************************************
     */
    private Reader getReader() {
        if (mReader == null || mReader.equals("")) {
            return null;
        }
        return mReader;
    }

    /**
     * ********************************************
     * method name   : setReader
     * description   : 设置读卡器对象
     *
     * @param : @param reader
     *          modified      : yangningbo ,  2013-10-12  下午4:05:17
     * @return : void
     * @see :
     * *******************************************
     */
    private void setReader(Reader reader) {
        mReader = reader;
    }


    /**
     * ********************************************
     * method name   : serviceConnected
     * modified      : yangningbo ,  2013-10-12
     *
     * @see : @see org.simalliance.openmobileapi.SEService.CallBack#serviceConnected(org.simalliance.openmobileapi.SEService)
     * *******************************************
     */
    @Override
    public void serviceConnected(SEService service) {
        L.i(TAG, "SmartCardService connected.");
        try {
            Reader[] readers = mSEService.getReaders();

            for (Reader reader : readers) {
                if (reader.getName().toUpperCase().startsWith("SIM")) {
                    setReader(reader);
                    break;
                }
            }

            mListener.onConnected(mReqId);
        } catch (Exception e) {
            mListener.onFinished();
            mListener.onFailed(e);
            L.i(TAG, e.toString());
        }
    }

    /**
     * ********************************************
     * method name   : shutdown
     * description   : 取消现在正在执行的任务，并关闭SEService远程服务
     *
     * @param : modified      : yangningbo ,  2013-10-12  下午4:06:51
     * @return : void
     * @see :
     * *******************************************
     */
    public void shutdown() {
        if (mRunApduTask != null && !mRunApduTask.isCancelled()) {
            mRunApduTask.cancel(true);
        }
        if (mSEService != null) {
            L.i(TAG, "Disconnecting from SmartCardService.");
            mSEService.shutdown();
        }
    }

    /**
     * ********************************************
     * method name   : runApdu
     * description   : 单线程执行apdu请求
     *
     * @param : @param req
     *          modified      : yangningbo ,  2013-10-12  下午4:08:12
     * @return : void
     * @see :
     * *******************************************
     */
    public void runApdu(ApduRequest req) {
        if (mRunApduTask != null && !mRunApduTask.isCancelled()) {
            mRunApduTask.cancel(true);
        }

        mRunApduTask = new RunApduTask();
        mRunApduTask.executeOnExecutor(SINGLE_TASK_EXECUTOR, req);
    }

    /**
     * ********************************************
     * method name   : execute
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
    public byte[] execute(String apdu) throws Exception {
        try {
            L.d(TAG, "-> " + apdu);
            byte[] rsp = mChannel.transmit(Utils.hexStringToBytes(apdu));
            L.d(TAG, "<- " + Utils.bytesToHexString(rsp));

            checkError(rsp);
            // return Utils.stripSW1SW2(rsp);
            return rsp;
        } catch (Exception e) {
            e.printStackTrace();
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
            L.d(TAG, "-> " + apdu);
            byte[] rsp = mChannel.transmit(Utils.hexStringToBytes(apdu));
            L.d(TAG, "<- " + Utils.bytesToHexString(rsp));
            // return Utils.stripSW1SW2(rsp);
            return rsp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApduException(e);
        }
    }

    private void checkError(byte[] rsp) throws Exception {
        if (!(Utils.getSW1SW2(rsp) == 0x9000)) {
            throw new Exception(Utils.bytesToHexString(rsp));
        }
    }

    /**
     * ********************************************
     * method name   : closeChannel
     * description   : 执行完apdu指令需要关闭逻辑通道
     *
     * @param : modified      : yangningbo ,  2013-10-12  下午4:16:32
     * @return : void
     * @see :
     * *******************************************
     */
    private void closeChannel() {
        try {
            if (mChannel != null && !mChannel.isClosed())
                mChannel.close();
        } catch (Exception e) {
            L.e(TAG, e.toString());
        }
        L.i(TAG, "SmartCardService disconnected.");
    }

    /**
     * ******************  类说明  *********************
     * class       :  RunApduTask
     *
     * @author :  yangningbo
     * @version :  1.0
     *          description :  异步执行Apdu请求任务
     * @see :
     * ***********************************************
     */
    private class RunApduTask extends
            AsyncTask<ApduRequest, Void, ApduResponse> {

        private Exception ex;

        /**
         * ********************************************
         * method name   : doInBackground 子线程获取逻辑通道对象后执行apdu请求
         * modified      : yangningbo ,  2013-10-12
         *
         * @see : @see android.os.AsyncTask#doInBackground(Params[])
         * *******************************************
         */
        @Override
        protected ApduResponse doInBackground(ApduRequest... params) {
            // Channel channel = null;
            try {
                if (getReader() == null) {
                    L.i(TAG, "Reader not available.");
                    throw new OpenChannelException(OpenChannelException.READER_ERROR);
                }

                if (!getReader().isSecureElementPresent()) {
                    L.i(TAG, "Card not available.");
                    throw new OpenChannelException(OpenChannelException.REDA_CARD_ERROR);
                }

                try {
                    mChannel = getReader().openSession().openLogicalChannel(Utils.hexStringToBytes(params[0].getAid()));
                    if (mChannel == null) {
                        L.i(TAG,
                                "Secure Element is unable to provide a new logical channel.");
                        throw new OpenChannelException(OpenChannelException.OPEN_CHANNEL_FAILD);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.i(TAG, "Open logical channel failed.");
                    throw new OpenChannelException(OpenChannelException.OPEN_CHANNEL_FAILD);
                }

                // setChannel(channel);
                return params[0].run(SwpApduExecutor.this);
            } catch (Exception e) {
                ex = e;
                L.e(TAG, e.toString());
            } finally {
                closeChannel();
            }

            return null;
        }

        /**
         * ********************************************
         * method name   : onPostExecute apdu请求结果主线程回调
         * modified      : yangningbo ,  2013-10-12
         *
         * @see : @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         * *******************************************
         */
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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mListener.onFinished();
        }
    }
}
