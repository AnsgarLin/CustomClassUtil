package com.custom.android.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * Re-trigger a toast by using the same toast instance
	 */
	public static Toast restartToast(Context context, Toast oldToast, String message) {
		if (oldToast != null) {
			oldToast.setText(message);
		} else {
			oldToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}
		oldToast.show();
		return oldToast;
	}

	/**
	 * Re-trigger a toast by using the same toast instance
	 */
	public static Toast restartToast(Context context, Toast oldToast, int messageID) {
		if (oldToast != null) {
			oldToast.setText(messageID);
		} else {
			oldToast = Toast.makeText(context, context.getString(messageID), Toast.LENGTH_SHORT);
		}
		oldToast.show();
		return oldToast;
	}
}