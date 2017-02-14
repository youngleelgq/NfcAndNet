package com.umpay.nfcandnet.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;

import com.umpay.nfcandnet.apdu.ApduExecutor;
import com.umpay.nfcandnet.apdu.ApduExecutorListener;
import com.umpay.nfcandnet.apdu.NfcApduExecutor;
import com.umpay.nfcandnet.apdu.SwpApduExecutor;
import com.umpay.nfcandnet.common.Const;
import com.umpay.nfcandnet.listener.OnCardTouchListener;

public abstract class NfcBaseBusiActivity extends NfcBaseActivity implements
        ApduExecutorListener, OnCardTouchListener {

    protected PopupWindow mPopupWindow;
    protected ApduExecutor mApduExecutor;
    protected ApduExecutorListener mListener;
    protected View stickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener = this;
        switch (getCardType()) {
            case Const.ChannelType.NFC:
                mApduExecutor = new NfcApduExecutor(mIsoDep, this);
                break;
            case Const.ChannelType.SWP_SIM:
                try {
                    mApduExecutor = new SwpApduExecutor(mContext,
                            mListener);
                    onCardTouch();
                } catch (Throwable error) {
                    error.printStackTrace();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    protected void hideStickCardView() {
        if (stickView != null) {
            stickView.setVisibility(View.GONE);
        }
    }

    @Override
    protected Class setIntentPage() {
        return this.getClass();
    }

    /**
     * 2. 重写下列Activity生命周期的回调方法，并且添加逻辑在Activity挂起（onPause()）和获得焦点（onResume()）时，
     * 来启用和禁用前台调度。 enableForegroundDispatch()方法必须在主线程中被调用，并且只有在该Activity在前台的时候（
     * 要保证在onResume()方法中调用这个方法）。 你还需要实现onNewIntent回调方法来处理扫描到的NFC标签的数据：
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (isIsoDepConnect(intent)) {
            intent.putExtra(Const.IntentExtraKey.CHANEL_TYPE, Const.ChannelType.NFC);
            // 因为popwindow显示有200毫秒延迟，如果先检测到了卡片，那么读完卡后会把动画启动，所以加上这个变量，禁止读到卡以后跳动画
//            dismissPasteCardPopWindow();
            hideStickCardView();
//            int CardType = Const.ChanelType.NFC;//getIntent().getIntExtra(Const.IntentExtraKey.CHANEL_TYPE,Const.CardType.NFC);
            if (mApduExecutor != null) {
                mApduExecutor.shutdown();
            }
            mApduExecutor = new NfcApduExecutor(mIsoDep, this);
            this.onCardTouch();
            // 若apduexcutor为蓝牙方式或者手机卡方式正在连接
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    @Override
    public void onFinished() {
        if (mApduExecutor != null) {
            mApduExecutor.shutdown();
        }
        dismissProgressDialog();
    }
}
