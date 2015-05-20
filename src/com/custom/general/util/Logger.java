package com.custom.general.util;

import android.util.Log;

public class Logger {
	public static boolean LOG = true; 

	public static void d(Class<?> c, String message) {
		if (LOG) {
			Log.d(parserClassName(c), message);
		}
	}
	public static void d(String tag, Class<?> c, String message) {
		if (LOG) {
			Log.d(tag + ":" + parserClassName(c), message);
		}
	}

	public static void e(Class<?> c, String message) {
		if (LOG) {
			Log.e(parserClassName(c), message);
		}
	}
	public static void e(String tag, Class<?> c, String message) {
		if (LOG) {
			Log.e(tag + ":" + parserClassName(c), message);
		}
	}

	public static void i(Class<?> c, String message) {
		if (LOG) {
			Log.i(parserClassName(c), message);
		}
	}
	public static void i(String tag, Class<?> c, String message) {
		if (LOG) {
			Log.i(tag + ":" + parserClassName(c), message);
		}
	}

	public static void v(Class<?> c, String message) {
		if (LOG) {
			Log.v(parserClassName(c), message);
		}
	}
	public static void v(String tag, Class<?> c, String message) {
		if (LOG) {
			Log.v(tag + ":" + parserClassName(c), message);
		}
	}

	public static void w(Class<?> c, String message) {
		if (LOG) {
			Log.w(parserClassName(c), message);
		}
	}
	public static void w(String tag, Class<?> c, String message) {
		if (LOG) {
			Log.w(tag + ":" + parserClassName(c), message);
		}
	}

	private static String parserClassName(Class<?> c) {
		int dotLastIndex = c.getName().lastIndexOf('.');
		int moneyIndex = c.getName().lastIndexOf('$');
		if(moneyIndex != -1) {
			return c.getName().substring(++dotLastIndex, moneyIndex--);
		} else {
			return c.getName().substring(++dotLastIndex);
		}
	}
}
