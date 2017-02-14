package com.umpay.nfcandnet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;

import com.umpay.nfcandnet.common.Const;


public class NetworkUtil {
	/* 无网络 */
	public final static int NONE = Const.Network.NONE;
	/* Wi-Fi */
	public final static int WIFI = Const.Network.WIFI;
	/* 3G,GPRS */
	public final static int MOBILE = Const.Network.MOBILE;

	/**
	 * * 获取当前网络状态
	 * 
	 * @param context
	 * @return
	 */

	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 手机网络判断
		State state = connManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return MOBILE;
		}
		// Wifi网络判断
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return WIFI;
		}
		return NONE;
	}

	public static boolean isNetWorkAvailable(Context context) {
		return getNetworkState(context) != NONE;
	}
	
	 /**
     * 判断网络环境是否为WIFI
     * @param context
     * @return
     */
    public static boolean isWifi(Context context){
    	ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null  
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;  
        } 
        if (activeNetInfo != null  
        		&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE){
			return activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
		}
    	return false;
    }
    
    /**
     * 判断网络环境是否为wife或4G
     * @param context
     * @return
     */
    public static boolean isWifiOr4G(Context context){
    	ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        boolean isMobileConn = networkInfo.isConnected();
    	if(isWifiConn){
    		return true;
    	}
    	
    	//4G网络判断
//    	if(isMobileConn){
//    		if(networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE){
//    			return true;
//    		}
//    	}
        
        return false;
    }

	public static final String NET_WIFI = "Wifi";
	public static final String NET_2G = "2G";
	public static final String NET_3G = "3G";
	public static final String NET_4G = "4G";
	public static final String NET_UNKNOW = "unknow";

	public static String getNetType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return NET_WIFI;
		} else if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			switch (networkInfo.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					return NET_2G;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					return NET_3G;
				case TelephonyManager.NETWORK_TYPE_LTE:
					return NET_4G;
				default:
					return NET_UNKNOW;
			}
		}
		return NET_UNKNOW;
	}
    
}
