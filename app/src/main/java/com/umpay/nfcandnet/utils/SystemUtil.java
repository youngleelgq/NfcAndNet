package com.umpay.nfcandnet.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

/**
 * @author LiZhipeng
 */
public class SystemUtil {

    private SystemUtil() {
    }

    /**
     * get MCC + MNC + MIN code (IMSI) MCC : Mobile country code . MNC : Mobile
     * network code . For example , a typical IMSI number is 460030912121001 .
     */
    public static String getImsi(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    /**
     * get IMEI IMEI : International Mobile Equipment Identity
     */
    public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * get phone model
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获得系统版本号
     */
    public static String getSystemVer() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机号号
     *
     * @param context
     * @return
     */
    public static String getMobileNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    /**
     * 根据PID获得包上下文
     */
    public static Context getPackageContextFromPid(Context context, int pid) {
        String processName = getPackageNameFromPid(context, pid);
        return getPackageContextFromPackageName(context, processName);
    }

    /**
     * 根据PID获得对应app包名
     */
    public static String getPackageNameFromPid(Context context, int pid) {
        String processName = "";
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> i = l.iterator();
        while (i.hasNext()) {
            RunningAppProcessInfo info = i
                    .next();
            try {
                if (info.pid == pid) {
                    processName = info.processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    /**
     * 根据包名，获得对应的上下文
     *
     * @param context     当前上下文
     * @param packageName 包名
     * @throws NameNotFoundException
     */
    public static Context getPackageContextFromPackageName(Context context,
                                                           String packageName) {
        try {
            return context.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE
                            | Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 应用版本号
     */
    public static String getVersionCode(Context context) {
        int verCode = 0;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            verCode = pi.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode + "";
    }

    /**
     * 版本名称
     *
     * @throws NameNotFoundException
     */
    public static final String getVersionName(Context context) {
        String verName = "0.0";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            verName = pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 签名hash
     *
     * @throws NameNotFoundException
     */
    public static String getCertificateSHA1Fingerprint(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = bytesToHexString(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    private static String bytesToHexString(byte[] bytes) {
        return bytesToHexString(bytes, "");
    }

    private static String bytesToHexString(byte[] bytes, String separator) {
        StringBuilder sb = new StringBuilder();

        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            String s = (i == len - 1) ? "" : separator;
            sb.append(String.format("%02X" + s, bytes[i] & 0xFF));
        }
        return sb.toString();
    }

    /**
     * 读取Application中的MetaData数据
     *
     * @param context 上下文
     * @param key     要得到的字段
     * @return
     */
    public static String getMetaDataFromApplication(Context context, String key) {
        String data = "";
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return ai.metaData.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 获取SANDBOX环境
     *
     * @param context
     * @param key
     * @return
     */
    public static String getSandboxEnv(Context context, String key) {
        String data = "false";
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            data = String.valueOf(ai.metaData.getBoolean(key));
            return (data == null || data.trim().length() == 0) ? "false" : data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 设备MAC地址
     */
    public static String getMacAddress(Context context) {
        WifiInfo wi = ((WifiManager) context
                .getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        return wi.getMacAddress();
    }

    private static String calculateSecretKey(String callerPackageName,
                                             String hexHash) {
        String data = hexHash + ";" + callerPackageName;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (Exception e) {
            throw new AccessControlException("Hash can not be computed");
        }

        return bytesToHexString(md.digest(data.getBytes()));
    }

    public static String getSecretKey(Context context) {
        String packageName = context.getPackageName();

//        String hexHash = getCertificateSHA1Fingerprint(context);
//        return calculateSecretKey(packageName, hexHash);
        return packageName;
    }

}
