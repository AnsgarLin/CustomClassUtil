package com.custom.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

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
	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if (networkInfo != null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Check whether wi-fi network connection is available
	 */
	public boolean isWifiConnected(Context context) {
		if (context != null) {
			NetworkInfo wifiNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetworkInfo != null) {
				return wifiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Check whether mobile(3G/4G/GPRS) network connection is available
	 */
	public boolean isMobileConnected(Context context) {
		if (context != null) {
			NetworkInfo mobileNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetworkInfo != null) {
				return mobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Check whether the network connection is fast
	 */
	public static boolean isConnectedFast(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return ((info != null) && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
	}

	/**
	 * Check if the connection is fast
	 */
	private static boolean isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			System.out.println("CONNECTED VIA WIFI");
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return true; // ~ 2-14 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return true; // ~ 700-1700 kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
				/*
				 * Above API level 7, make sure to set android:targetSdkVersion to appropriate level
				 * to use these
				 */
			case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
				return true; // ~ 1-2 Mbps
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
				return true; // ~ 5 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
				return true; // ~ 10-20 Mbps
			case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
				return false; // ~25 kbps
			case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
				return true; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Get the current network connection type
	 */
	public int getConnectedType(Context context) {
		if (context != null) {
			NetworkInfo mNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if ((mNetworkInfo != null) && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * Check whether IME soft input window is open, the input view must the same size as the full
	 * screen
	 */
	public static boolean isSoftInputWindowOpen(View view) {
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

	/**
	 * Make app to be full screen
	 */
	public static void setFullScreen(Activity activity, boolean isFullScreen) {
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		if (isFullScreen == false) {
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			activity.getWindow().setAttributes(attrs);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			activity.getActionBar().show();
		} else {
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			activity.getWindow().setAttributes(attrs);
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			activity.getActionBar().hide();
		}
	}

	/**
	 * Install apk from file
	 */
	public static void installAPKFromFile(Context context, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
