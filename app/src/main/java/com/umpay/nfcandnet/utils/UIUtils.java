package com.umpay.nfcandnet.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.nfcandnet.R;


/**
 * 提供UI上的一些公共操作方法
 */
public class UIUtils {

    /**
     * 在状态栏取消一条通知
     *
     * @param context 上下文
     */
    public void cancelNotify(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.app_name);
    }

    /**
     * 拨打电话
     *
     * @param context 上下文
     * @param number  呼叫的号码
     */
    public void dial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * 安装APK到手机
     *
     * @param context  上下文
     * @param filePath apk的本地路径
     */
    public void installApk(Context context, String filePath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 收起软键盘并设置提示文字
     */
    public void collapseSoftInputMethod(Context context, EditText text) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(),
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 标准化EditText光标的位置，解决光标在输入文字前面的问题
     *
     * @param s 传入 editText.getText()
     */
    public void formatCursorPosition(Editable s) {
        Selection.setSelection((Spannable) s, s.toString().length());
    }

    /**
     * dip转像素
     */
    public int dipToPixels(Context context, int dip) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        float valueDips = dip;
        int valuePixels = (int) (valueDips * SCALE + 0.5f);
        return valuePixels;
    }

    /**
     * @Description：显示自定义Toast <p>
     * 创建人：zhangmuhao , 2013-12-18 上午10:43:01
     * </p>
     * <p>
     * 修改人：zhangmuhao , 2013-12-18 上午10:43:01
     * </p>
     * void
     */
    public void showToastLong(Context context, String msg, boolean isShort) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(context);
        // 设置Toast的位置
//        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, xOffset,
//                yOffset);
        if (isShort)
            toast.setDuration(Toast.LENGTH_SHORT);
        else
            toast.setDuration(Toast.LENGTH_LONG);
        // 将自定义的界面设置到Toast里
        toast.setView(layout);
        toast.show();
    }

}
