package com.customclassutil.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;

public class SystemUtil {
	/**
	 * Check there has at least "sizeNeed" MB in storage
	 */
	public static Boolean isEnoughAvalibleStorage(int sizeNeed) {
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		return (fs.getAvailableBytes() / 1024f / 1024f) >= sizeNeed;
	}

	/**
	 * Get LayoutInflater from context service
	 */
	public static LayoutInflater getLayoutInflater(Context context) {
		return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Check whether network connection is available
	 */
	public static boolean isNetworkEnabled(Context context) {
		if (context != null) {
			NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if ((info != null) && info.isConnected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether IME soft input window is open, the input view must the same size as the full screen
	 */
	public static boolean isSoftInputWinOpen(View view) {
		int heightDiff = view.getRootView().getHeight() - view.getHeight();
		if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard
			return true;
		}
		return false;
	}

	/**
	 * Get device's imei
	 */
	public static String getImei(Context context) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			return telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get current device's os version
	 */
	public static int getDeviceSDKVersion() {
		return Integer.valueOf(Build.VERSION.SDK_INT);
	}
}
