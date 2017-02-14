package com.umpay.nfcandnet.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.nfcandnet.NFCApplication;
import com.umpay.nfcandnet.R;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：Toast工具类 <p>
 * 创建日期：2013-9-10
 * </p>
 * @see
 */
public class T {

    private static final boolean DEBUG = true;
    private static Toast toast;

    public static void l(Context context, String text) {
        if (DEBUG)
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void l(Context context, Throwable tr) {
        String text = tr.getMessage();
        l(context, text != null ? text : "未知异常，请稍后重试！");
    }

    public static void s(Context context, String text) {
        if (DEBUG)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void s(Context context, Throwable tr) {
        String text = tr.getMessage();
        s(context, text != null ? text : "未知异常，请稍后重试！");
    }

    public static void showCustomToast(String textStr) {
        View layout = LayoutInflater.from(NFCApplication.getInstance()).inflate(R.layout.toast, null);
        TextView text = (TextView) layout.findViewById(R.id.toast_text_tv);
        text.setText(textStr);
        toast = getInstance();
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private static Toast getInstance() {
        if (toast != null) {
            return toast;
        }
        return new Toast(NFCApplication.getInstance());
    }


    public static void toastCancel() {
        if (toast != null) {
            toast.cancel();
        }
    }

    public static void showSuccessToast(String msg) {
        Toast toast = new Toast(NFCApplication.getInstance());
        View view = View.inflate(NFCApplication.getInstance(), R.layout.view_toast, null);
        ((TextView) view.findViewById(R.id.tv_content)).setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.show();
    }
}
