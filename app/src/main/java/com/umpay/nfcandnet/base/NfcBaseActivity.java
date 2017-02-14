package com.umpay.nfcandnet.base;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;

import com.umpay.nfcandnet.activity.TestActivity;
import com.umpay.nfcandnet.apdu.ApduExecutorListener;
import com.umpay.nfcandnet.apdu.ApduResponse;
import com.umpay.nfcandnet.common.Const;
import com.umpay.nfcandnet.utils.L;
import com.umpay.nfcandnet.utils.LogUtils;


public abstract class NfcBaseActivity extends BaseActivity implements
        ApduExecutorListener {


    protected IsoDep mIsoDep;
    protected Context mContext;
    protected NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Class intentPage;
    protected int CardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentPage = setIntentPage();
        mContext = this;
        processNfc();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }


    @Override
    protected void preView() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    protected Class setIntentPage() {
        return TestActivity.class;
    }

    protected boolean isIsoDepConnect(Intent intent) {
        if (intent == null) {
            return false;
        }

        String action = intent.getAction();
        if (!NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            return false;
        }

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return false;
        }

        /*** 获取tech实例 ***/
        mIsoDep = IsoDep.get(tag);
        return mIsoDep != null;

    }

    protected void processNfc() {
        L.d("NfcBaseActivity", "进入processNfc方法");
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter == null) {
            LogUtils.e("不支持nfc");
            return;
        }

        // A. 创建一个PendingIntent对象，以便Android系统能够在扫描到NFC标签时，用它来封装NFC标签的详细信息。
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                        intentPage).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT);
        /*
         * if(isJump2InfoPage){
		 * 
		 * // A. 创建一个PendingIntent对象，以便Android系统能够在扫描到NFC标签时，用它来封装NFC标签的详细信息。
		 * mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
		 * NfcReadCardActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
		 * 0); }else{ // A.
		 * 创建一个PendingIntent对象，以便Android系统能够在扫描到NFC标签时，用它来封装NFC标签的详细信息。
		 * mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
		 * getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0); }
		 */

        // B. 声明你想要截获处理的Intent对象的Intent过滤器。
        // 前台调度系统会在设备扫描到NFC标签时，用声明的Intent过滤器来检查接收到的Intent对象。
        // 如果匹配就会让你的应用程序来处理这个Intent对象，如果不匹配，前台调度系统会回退到Intent调度系统。
        // 如果Intent过滤器和技术过滤器的数组指定了null，那么就说明你要过滤所有的退回到TAG_DISCOVERED类型的Intent对象的标签。
        // 以下代码会用于处理所有的NDEF_DISCOVERED的MIME类型。只有在需要的时候才做这种处理：
        IntentFilter filter = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter[]{filter};

        // C. 建立一个应用程序希望处理的NFC标签技术的数组。调用Object.class.getName()方法来获取你想要支持的技术的类：
        mTechLists = new String[][]{new String[]{IsoDep.class.getName()},};

        onNewIntent(getIntent());
    }


    @Override
    public void onStart(int reqId) {

    }

    @Override
    public void onConnected(int reqId) {

    }

    @Override
    public void onCompleted(ApduResponse rsp) {

    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onFailed(Exception e) {

    }


    /**
     * 卡片的类型
     *
     * @return int
     */
    protected int getCardType() {
        CardType = getIntent().getIntExtra(Const.IntentExtraKey.CHANEL_TYPE, Const.ChannelType.NFC);
        L.e("卡片类型", "chanelType：" + CardType);
        return CardType;
    }
}
