package com.custom.android.util;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

public class WebViewUtil {
	/**
	 * Return a WebView that align setting of text is justify
	 */
	public static WebView getWebViewWithAlignJustify(Activity activity, int resourceString) {
		WebView webView = new WebView(activity);
		webView.setVerticalScrollBarEnabled(false);
		webView.loadDataWithBaseURL("",
				"<![CDATA[<html><head></head><body style=\"text-align:justify;\">" + activity.getResources().getString(resourceString) + "</body>",
				"text/html", "utf-8", null);
		return webView;
	}

	/**
	 * Set a exist WebView that align of text is justify
	 */
	public static void setWebViewWithAlignJustify(WebView webView, Activity activity, int resourceString) {
		webView.setVerticalScrollBarEnabled(false);
		webView.loadDataWithBaseURL("",
				"<![CDATA[<html><head></head><body style=\"text-align:justify;\">" + activity.getResources().getString(resourceString) + "</body>",
				"text/html", "utf-8", null);
	}

	/**
	 * For version above android 23
	 */
	@SuppressLint("NewApi")
	public static void saveCookie() {
		CookieManager.getInstance().flush();
	}

	/**
	 * For version below 23. Save cookie into permanent storage and webview's instances for later
	 * use. May used before WebView.load() to make the cookie useful.
	 */
	@SuppressWarnings("deprecation")
	public static void saveCookie(Context context, DefaultHttpClient httpClient) {
		// Get cookies from HttpClient
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();

		// <p>Create an CookieSynManager instance to move the cookies in ram into permanent storage,
		// which can make cookies be hold forever.</p>
		// The class was This class was deprecated in API level 21. The WebView now automatically
		// syncs cookies as necessary. You no longer need to create or use the CookieSyncManager. To
		// manually force a sync you can use the CookieManager method flush() which is a synchronous
		// replacement for sync().
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.createInstance(context);
		}
		// Load every cookies in list, then transform into String and save into WebView's cookie
		// list.
		for (int i = 0; i < cookies.size(); i++) {
			String cookieString = cookies.get(i).getName() + "=" + cookies.get(i).getValue() + "; domain=" + cookies.get(i).getDomain();

			// Save the cookies used by an application's WebView instances.
			CookieManager.getInstance().setCookie(cookies.get(i).getDomain(), cookieString);

			// Move the cookies from ram to permanet storage.
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				CookieSyncManager.createInstance(context);
			}
		}
	}

	/**
	 * Release webview, best to be call in onDestroy. {@link WebView#loadUrl} is used for replacing
	 * {@link WebView#clearView}
	 */
	public static void release(WebView webView) {
		if (webView != null) {
			webView.clearHistory();
			webView.clearCache(true);
			webView.loadUrl("about:blank");
			webView.pauseTimers();
			webView = null;
		}
	}
}
