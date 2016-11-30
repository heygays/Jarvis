package com.heygays.jarvis.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/**
 * 网络工具类
 * 
 * @author Administrator
 *
 */
public class NetUtil {
	/**
	 * 判断有无网络链接
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean checkNetwork(Context mContext) {
		ConnectivityManager conMan = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
			return true;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			return true;
		}
		return false;
	}
}
