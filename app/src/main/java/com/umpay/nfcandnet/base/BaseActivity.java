package com.umpay.nfcandnet.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umpay.nfcandnet.R;
import com.umpay.nfcandnet.data.ContentManager;
import com.umpay.nfcandnet.data.HttpLoader;
import com.umpay.nfcandnet.listener.PermissionResultListener;
import com.umpay.nfcandnet.model.IModel;
import com.umpay.nfcandnet.model.Request;
import com.umpay.nfcandnet.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity implements HttpLoader.OnLoadListener {

    protected boolean mIsShowProgress;
    protected ProgressDialog progressDialog;
    protected Dialog dialog;
    protected Context context;
    protected InputMethodManager imm;
    protected LayoutInflater inflater;
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inflater = LayoutInflater.from(this);
        initStateBar(R.color.bg_blue);
        preView();
        initView();
        initData();
    }

    protected abstract void preView();

    protected abstract void initView();

    protected abstract void initData();


    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 隐藏软键盘
     */
    @SuppressWarnings("unused")
    protected void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    @SuppressWarnings("unused")
    protected void showKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataStart(int dataType) {
        if (mIsShowProgress) {
            showProgressDialog();
        }
    }


    @Override
    public void onDataFail(Exception e, int dataType) {
        e.printStackTrace();
      /*  if (e instanceof Exception4Ui) {
            dialog(e.getMessage());
        } else*/
        if (e instanceof RuntimeException) {
            dialog(e.getMessage());
        } else {
            dialog("msg");
        }
        dismissProgressDialog();
    }

    @Override
    public void onDataFinish(int dataType) {
        dismissProgressDialog();
    }

    @Override
    public void doPost(int dataType) {
        MyIModel iModel = getIModel(dataType);
        if (iModel == null) {
            return;
        }
        ContentManager.getInstance().post(iModel.request, dataType, iModel.clazz, this);
    }

    /**
     * 网络请求
     *
     * @param dataType       0
     * @param isShowProgress 是否显示progressDialog
     */
    @SuppressWarnings("unused")
    protected void request(int dataType, boolean isShowProgress) {
        mIsShowProgress = isShowProgress;
        // 网络条件判断
        if (!NetworkUtil.isNetWorkAvailable(this)) {
            onDataFinish(dataType);
            onDataFail(new RuntimeException(), dataType);
            return;
        }
        HttpLoader loader = new HttpLoader(dataType);
        loader.setOnLoadListener(this);
        loader.execute();
    }

    @Override
    public void onDataGet(IModel result, int dataType) {
        if (result == null) {
            onDataFail(null, 0);
        } else {
            try {
                onSuccess(result, dataType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void onSuccess(IModel result, int dataType);

    protected abstract MyIModel getIModel(int dataType);

    public class MyIModel {
        public Request request;
        public Class<? extends IModel> clazz;

        public MyIModel(Request request, Class<? extends IModel> clazz) {
            this.request = request;
            this.clazz = clazz;
        }
    }

    protected void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, null);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * 弹出点击外部不可取消的提示框
     *
     * @param msg 提示框内容
     */
    protected void dialog(String msg) {
        dialog(msg, false);
    }

    /**
     * 弹出点击外部不可取消的提示框
     *
     * @param msg             提示框内容
     * @param isCloseActivity 点击确认该Activity是否finish
     */
    protected void dialog(String msg, boolean isCloseActivity) {
        if (dialog == null) {
            dialog = new Dialog(this, "提示", msg);
        } else {
            dialog.setMessage(msg);
        }
        if (isCloseActivity) {
            dialog.setOnAcceptButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        dialog.setCancelable(false);
        if (!this.isFinishing()) {
            dialog.show();
        }
    }


    /**
     * 退出应用弹框
     *
     * @param msg 弹框提示
     */
    @SuppressWarnings("unused")
    protected void exit(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                }).setCancelable(false).create().show();
    }


    private PermissionResultListener pl;

    @SuppressWarnings("unused")
    public void requestImeiPermission(PermissionResultListener pl) {
        this.pl = pl;
        requestPermission(1, Manifest.permission.READ_PHONE_STATE, new Runnable() {
            @Override
            public void run() {
                BaseActivity.this.pl.response(true);
            }
        }, new Runnable() {
            @Override
            public void run() {
                BaseActivity.this.pl.response(false);
            }
        });
    }

    @SuppressWarnings("unused")
    public void requestMsgPermission(PermissionResultListener pl) {
        this.pl = pl;
        requestPermission(1, Manifest.permission.SEND_SMS, new Runnable() {
            @Override
            public void run() {
                BaseActivity.this.pl.response(true);
            }
        }, new Runnable() {
            @Override
            public void run() {
                BaseActivity.this.pl.response(false);
            }
        });
    }

    @SuppressWarnings("unused")
    public void requestStoragePermission(PermissionResultListener pl) {
        this.pl = pl;
        requestPermission(1, Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                BaseActivity.this.pl.response(true);
            }
        }, new Runnable() {
            @Override
            public void run() {
                BaseActivity.this.pl.response(false);
            }
        });
    }

    /**
     * 请求权限
     *
     * @param id                   请求授权的id 唯一标识即可
     * @param permission           请求的权限
     * @param allowableRunnable    同意授权后的操作
     * @param disallowableRunnable 禁止权限后的操作
     */
    @SuppressWarnings("unused")
    protected void requestPermission(final int id, final String permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }

        allowablePermissionRunnables.put(id, allowableRunnable);
        if (disallowableRunnable != null) {
            disallowablePermissionRunnables.put(id, disallowableRunnable);
        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                if (!TextUtils.isEmpty(pl.start())) {
                    dialog(pl.start(), new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, id);
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, id);
                }
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }


    /**
     * 请求权限
     *
     * @param permission 请求的权限
     */
    @SuppressWarnings({"RedundantIfStatement", "unused"})
    public boolean checkMyPermission(final String permission) {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //没有这个权限
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Runnable allowRun = allowablePermissionRunnables.get(requestCode);
            allowRun.run();
        } else {
            Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
            disallowRun.run();
        }
    }

    /**
     * 弹出点击外部不可取消的提示框
     *
     * @param msg 提示框内容
     */
    protected void dialog(String msg, final OnClickListener listener) {
        if (dialog == null) {
            dialog = new Dialog(this, "提示", msg);
        } else {
            dialog.setMessage(msg);
        }
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 沉寖式状态栏
     */
    @TargetApi(19)
    private void initStateBar(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
