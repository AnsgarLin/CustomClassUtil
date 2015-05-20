package com.custom.android.util;

import android.app.Activity;
import android.webkit.WebView;

public class WebViewUtil {
	/**
	 * Return a WebView that align setting of text is justify
	 */
	public static WebView getWebViewWithAlignJustify(Activity activity, int resourceString) {
		WebView webView = new WebView(activity);
		webView.setVerticalScrollBarEnabled(false);
		webView.loadDataWithBaseURL("",
				"<![CDATA[<html><head></head><body style=\"text-align:justify;\">" + activity.getResources().getString(resourceString)
						+ "</body>", "text/html", "utf-8", null);
		return webView;
	}

	/**
	 * Set a exist WebView that align of text is justify
	 */
	public static void setWebViewWithAlignJustify(WebView webView, Activity activity, int resourceString) {
		webView.setVerticalScrollBarEnabled(false);
		webView.loadDataWithBaseURL("",
				"<![CDATA[<html><head></head><body style=\"text-align:justify;\">" + activity.getResources().getString(resourceString)
						+ "</body>", "text/html", "utf-8", null);
	}
}
