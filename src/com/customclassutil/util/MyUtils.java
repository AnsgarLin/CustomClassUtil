package com.customclassutil.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyUtils {
	public static String WHITE = "#ffffff";

	/**
	 * Default width and height are WRAP_CONTENT
	 */
	public static class RelativeLayoutUtil {
		/**
		 * Return a Center-Button RelativeLayout.LayoutParams
		 */
		public static RelativeLayout.LayoutParams getCenterBottomLayoutParams() {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			return lp;
		}

		/**
		 * Return a Center-Button RelativeLayout.LayoutParams with margin from bottom
		 */
		public static RelativeLayout.LayoutParams getCenterBottomLayoutParams(int bottom) {
			RelativeLayout.LayoutParams lp = getCenterBottomLayoutParams();
			lp.setMargins(0, 0, 0, bottom);
			return lp;
		}

		/**
		 * Return a RelativeLayout.LayoutParams with margin from left, top, right and bottom
		 */
		public static RelativeLayout.LayoutParams getMarginLayoutParams(int left, int top, int right, int bottom) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(left, top, right, bottom);
			return lp;
		}

	}

	public static class SearchViewUtil {
		/**
		 * SearchView will always expand to fix the width of screen
		 */
		public static void setWidthToFitWindow(Context context, SearchView searchView) {
			searchView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
		}

		/**
		 * Customized SearView button with image resource
		 */
		public static void setSearchButton(SearchView searchView, int resourceId) {
			ImageView searchHintIcon = (ImageView) searchView.findViewById(searchView.getContext().getResources()
					.getIdentifier("android:id/search_button", null, null));
			if (searchHintIcon != null) {
				searchHintIcon.setImageResource(resourceId);
			}
		}
	}

	public static class BitmapUtil {
		/**
		 * Recycle a Bitmap resource, but need to set null outside
		 */
		public static void recycleBitmap(Bitmap bitmap) {
			bitmap.recycle();
			System.gc();
		}

		/**
		 * Save a Bitmap as a JPG file in given filePath and quality
		 */
		public static void saveBitmapAsJPGFile(File filePath, Bitmap bitmap, int quality) {
			try {
				filePath.createNewFile();
				bitmap.compress(CompressFormat.JPEG, quality, new FileOutputStream(filePath, false));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Get a Bitmap from URL
		 */
		public static Bitmap getBitmapFromURL(URL url) {
			return BitmapFactory.decodeStream(InputStreamUtil.getURLInputStream(url));
		}

		/**
		 * Get a Bitmap from URL and scaled to simple size by given width and height
		 */
		public static Bitmap getBitmapFromURLBySize(URL url, int width, int height) {
			byte[] buff;
			if ((buff = InputStreamUtil.convertInStreamToBytes(InputStreamUtil.getURLInputStream(url))) == null) {
				return null;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(buff, 0, buff.length, options);

			options.inSampleSize = 1;
			if ((options.outWidth > width) || (options.outHeight > height)) {
				// Use floor to make the ratio as minimum as possible
				int widthRatio = (int) Math.floor(options.outWidth / (float) width);
				int heightRatio = (int) Math.floor(options.outHeight / (float) height);

				// If one of width/height is bigger then target width/height, use the big one ratio to make sure the bitmap will smaller
				options.inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
			}

			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeByteArray(buff, 0, buff.length, options);
		}

		/**
		 * Get a bitmap with target size
		 * 
		 * @param bitmap
		 *            The original bitmap
		 * @param dstW
		 *            The target width
		 * @param dstH
		 *            The target height
		 * @param recycle
		 *            true if recycle the origin, false otherwise
		 * @return The small bitmap with target size
		 */
		public static Bitmap getScaleBitmap(Bitmap bitmap, float dstW, float dstH, boolean recycle) {
			Bitmap cloneBitmap = createMutableBitmap(bitmap);

			android.graphics.Point smallSize = measureSmallSize(dstW, dstH, cloneBitmap.getWidth(), cloneBitmap.getHeight());
			Bitmap smallBitmap = Bitmap.createScaledBitmap(cloneBitmap, smallSize.x, smallSize.y, false);
			if (recycle && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			cloneBitmap.recycle();

			return smallBitmap;
		}

		public static android.graphics.Point measureSmallSize(float dstW, float dstH, float srcW, float srcH) {
			float scaleHeight = srcH / dstH;
			float scaleWidth = srcW / dstW;
			float finalW = srcW;
			float finalH = srcH;

			// To get a smaller size, either scaleHeight or scaleWidth need to be greater than 0 as int
			if (((srcW / scaleHeight) > 0f) && (scaleHeight > 0f) && (scaleHeight > scaleWidth)) {
				Log.d("Ansgar", "Scale by height");
				finalW = srcW / scaleHeight;
				finalH = srcH / scaleHeight;
			} else if (((srcH / scaleWidth) > 0f) && (scaleWidth > 0f) && (scaleWidth > scaleHeight)) {
				Log.d("Ansgar", "Scale by width");
				finalW = srcW / scaleWidth;
				finalH = srcH / scaleWidth;
			}

			// Too make the small size is absolutely smaller than the target size, resize again if necessary.
			if (dstW < finalW) {
				float scale = finalW / dstW;
				finalW = dstW;
				finalH = finalH / scale;
			} else if (dstH < finalH) {
				float scale = finalH / dstH;
				finalH = dstH;
				finalW = finalW / scale;
			}
			return new android.graphics.Point((int) finalW, (int) finalH);
		}

		public static Bitmap createMutableBitmap(Bitmap bitmap) {
			Bitmap mutableBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
			Canvas canvas = new Canvas(mutableBitmap);
			canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG));

			return mutableBitmap;
		}

	}

	public static class InputStreamUtil {
		/**
		 * Get a opened input stream for given url
		 */
		public static InputStream getURLInputStream(URL url) {
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				return connection.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * Convert inputStream to byteArray
		 */
		public static byte[] convertInStreamToBytes(InputStream inputStream) {
			if (inputStream == null) {
				Logger.d(MyUtils.class, "Something wrong with loading image");

				return null;
			}

			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buff)) != -1) {
					outputStream.write(buff, 0, len);
				}
				inputStream.close();
				outputStream.close();
				return outputStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class DrawableUtil {
		/**
		 * Get a Drawable from URL
		 */
		public static Drawable getDrawableFromURL(URL url) {
			try {
				return Drawable.createFromStream((InputStream) url.getContent(), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Get a StateDrawable with press state
		 */
		public static StateListDrawable getStateDrawableWithColor(String pressColor, String defaultColor) {
			StateListDrawable stateListDrawable = new StateListDrawable();
			stateListDrawable.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
					new ColorDrawable(Color.parseColor(pressColor)));
			stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, new ColorDrawable(Color.parseColor(defaultColor)));
			return stateListDrawable;
		}

		/**
		 * Get a StateDrawable with press state
		 */
		public StateListDrawable getStateDrawableWithColor(int pressColor, int defaultColor) {
			StateListDrawable stateListDrawable = new StateListDrawable();
			stateListDrawable.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, new ColorDrawable(pressColor));
			stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, new ColorDrawable(defaultColor));
			return stateListDrawable;
		}
	}

	public static class WebViewUtil {
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

	public static class SystemUtil {
		/**
		 * Check there has at least "sizeNeed" MB in storage
		 */
		@SuppressLint("NewApi")
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

	public static class InflateUtil {
		/**
		 * Inflate the specific layout, default attach to root is false
		 */
		public static View InflateReource(Context context, int resourceID, ViewGroup parent) {
			return SystemUtil.getLayoutInflater(context).inflate(resourceID, parent, false);
		}
	}

	public static class FileUtil {
		/**
		 * Create a directory for specific type and name
		 */
		public static File getDir(String type, String name) {
			File tempDir = null;
			if (type == Environment.DIRECTORY_PICTURES) {
				tempDir = new File(Environment.getExternalStoragePublicDirectory(type), name);
			}

			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
			return tempDir;
		}
	}

	public static class TimerUtil {
		/**
		 * Start or Reset timer with given time
		 */
		public static Timer start(Timer timer, TimerTask task, long delay) {
			if (timer != null) {
				timer.purge();
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule(task, delay);
			return timer;
		}

		/**
		 * Stop and clear timer
		 */
		public static Timer stop(Timer timer) {
			if (timer != null) {
				timer.purge();
				timer.cancel();
			}
			return null;
		}
	}

	public static class ResourceUtil {
		/**
		 * Get a real image size for resource image
		 */
		public static Point getActualImageSize(Context context, int resourceID) {
			BitmapFactory.Options dimensions = new BitmapFactory.Options();
			dimensions.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(context.getResources(), resourceID, dimensions);
			return new Point(dimensions.outWidth, dimensions.outHeight);
		}
	}

	public static class TypedValueUtil {
		/**
		 * Transfer value from given dimension unit to pixel, return -1 for unknown dimension
		 */
		public static float toPixel(DisplayMetrics dm, String unit, float value) {
			if ((unit == "dip") || (unit == "dp")) {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm);
			} else if (unit == "in") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, value, dm);
			} else if (unit == "mm") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, dm);
			} else if (unit == "pt") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, dm);
			} else if (unit == "px") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, value, dm);
			} else if (unit == "sp") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, dm);
			} else {
				return -1;
			}
		}
	}

	public static class ToastUtil {
		/**
		 * Re-trigger a toast by using the same toast instance
		 */
		public static Toast restart(Context context, Toast oldToast, String message) {
			if (oldToast != null) {
				oldToast.cancel();
			}
			oldToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			oldToast.show();
			return oldToast;
		}

		/**
		 * Re-trigger a toast by using the same toast instance
		 */
		public static Toast restart(Context context, Toast oldToast, int messageID) {
			if (oldToast != null) {
				oldToast.cancel();
			}
			oldToast = Toast.makeText(context, context.getString(messageID), Toast.LENGTH_SHORT);
			oldToast.show();
			return oldToast;
		}
	}

	public static class DateUtil {
		/**
		 * Get the date after days from today in format
		 */
		public static String getDateFromToday(int range, SimpleDateFormat format) {
			Calendar today = Calendar.getInstance();
			today.add(Calendar.DATE, range);

			return format.format(today.getTime());
		}

		/**
		 * Get today's date info by field
		 */
		public static String getToday(int field) {
			Calendar today = Calendar.getInstance();
			if (field == Calendar.MONTH) {
				return String.valueOf(today.get(field) + 1);
			} else {
				return String.valueOf(today.get(field));
			}
		}

		/**
		 * Get calendar instance by format
		 */
		public static Calendar getCalenderByFormat(String dateString, String format) throws ParseException {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			Calendar calender = Calendar.getInstance();
			calender.setTime(simpleDateFormat.parse(dateString));
			return calender;
		}

		/**
		 * Get date instance by format
		 * 
		 * @throws ParseException
		 */
		public static Date getDateByFormat(String dateString, String format) throws ParseException {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			return simpleDateFormat.parse(dateString);
		}

		/**
		 * Get date duration, use date as basic unit
		 * 
		 * @throws ParseException
		 */
		public static int getDateDuration(Date startDate, Date endDate) {
			return (int) Math.ceil((endDate.getTime() - startDate.getTime()) / (1000f * 3600f * 24f)); // A day in milliseconds
		}
	}

	public static class ColorUtil {
		/**
		 * Mix two color with the front in alpha
		 */
		public static String mixColor(String back, String cover, float coverAlpha) {
			int[] backRGB = MyUtils.ColorUtil.hexToRGB(back);
			int[] coverRGB = MyUtils.ColorUtil.hexToRGB(cover);
			int[] mixRGB = new int[3];
			for (int i = 0; i < backRGB.length; i++) {
				mixRGB[i] = backRGB[i] + Math.round(((coverRGB[i] - backRGB[i]) * coverAlpha));
			}
			return MyUtils.ColorUtil.rgbToHex(mixRGB);
		}

		/**
		 * Transform hex color string to integer RBG array
		 */
		public static int[] hexToRGB(String color) {
			int[] numRGB = new int[3];
			numRGB[0] = Integer.parseInt(color.substring(1, 3), 16);
			numRGB[1] = Integer.parseInt(color.substring(3, 5), 16);
			numRGB[2] = Integer.parseInt(color.substring(5, 7), 16);
			return numRGB;
		}

		/**
		 * Transform integer RBG to hex color string
		 */
		public static String rgbToHex(int[] color) {
			String colorString = "#";
			for (int element : color) {
				if (element < 16) {
					colorString += "0";
				}
				colorString += Integer.toHexString(element);
			}
			return colorString;
		}

		/**
		 * Get color state list with press state. Press color will be mix with white in given alpha
		 */
		public static ColorStateList getColorStateList(String pressColor, String defaultColor, float mixAlpha) {
			int[][] states = new int[][] { new int[] { android.R.attr.state_enabled, android.R.attr.state_pressed },
					new int[] { android.R.attr.state_enabled } };
			int[] colors = new int[] { Color.parseColor(mixColor(pressColor, WHITE, mixAlpha)), Color.parseColor(defaultColor) };
			return new ColorStateList(states, colors);
		}
	}

	public static class ListViewUtil {
		/**
		 * Calculate the scroll bar's top-left and bottom-right position on screen with limit
		 */
		public static float[] getScrollBarDrawPos(ListView listView, float childH, float dividerH, int childCount, float scrollBarR,
				float scrollBarW, float scrollBarH, float scrollBarHLimit) {
			float scrollbarButtom = getScrollBarButtomPos(getScrollOffset(listView, childH, dividerH),
					calEndScrollY(childCount, childH, dividerH, listView.getHeight()), scrollBarH, listView.getHeight());

			scrollBarH /= ((((childH + dividerH) * childCount) - dividerH) / listView.getHeight());

			if (scrollBarH < scrollBarHLimit) {
				scrollBarH = scrollBarHLimit;
			}

			float[] scrollBarPos = new float[4];
			scrollBarPos[0] = scrollBarR - scrollBarW;
			scrollBarPos[1] = scrollbarButtom - scrollBarH;
			scrollBarPos[2] = scrollBarR;
			scrollBarPos[3] = scrollbarButtom;
			return scrollBarPos;
		}

		/**
		 * Calculate the scroll offset manually by given item view's height and divider's height
		 */
		public static float getScrollOffset(ListView listView, float itemViewHeight, float dividerHeight) {
			float offset;
			if (listView.getChildAt(0).getTop() < 0) {
				offset = ((itemViewHeight + dividerHeight) * listView.getFirstVisiblePosition()) + Math.abs(listView.getChildAt(0).getTop());
			} else if (listView.getChildAt(0).getTop() > 0) {
				offset = (itemViewHeight * listView.getFirstVisiblePosition()) + (dividerHeight * (listView.getFirstVisiblePosition() - 1))
						+ Math.abs(listView.getChildAt(0).getTop() - dividerHeight);
			} else {
				offset = ((itemViewHeight + dividerHeight) * listView.getFirstVisiblePosition());
			}
			return offset;
		}

		/**
		 * Calculate the total height of all the views, than calculate the end offset while the list is scroll to the end
		 */
		public static float calEndScrollY(int itemCount, float itemH, float dividerH, float viewH) {
			float totalLength = ((itemH + dividerH) * itemCount) - dividerH;
			return (((int) (totalLength / viewH) - 1) * viewH) + (totalLength % viewH);
		}

		/**
		 * Reflect the offset on the total height of all view to the list height to get the scroll bar offset
		 */
		public static float getScrollBarButtomPos(float scrollOffset, float endScrollY, float scrollBarH, float viewH) {
			return ((scrollOffset / endScrollY) * (viewH - ((scrollOffset / endScrollY) * scrollBarH))) + ((scrollOffset / endScrollY) * scrollBarH);
		}
	}

	public static class MathUtil {
		/**
		 * Ceiling of the number, accurate to given decimal places
		 */
		public static double CeilWithPoint(double num, int decimalAfter) {
			return Math.ceil(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
		}

		/**
		 * Floor of the number, accurate to given decimal places
		 */
		public static double FloorWithPoint(double num, int decimalAfter) {
			return Math.floor(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
		}

		/**
		 * Round of the number, accurate to given decimal places
		 */
		public static double RoundWithPoint(double num, int decimalAfter) {
			return Math.round(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
		}

		/**
		 * Get distance between two points in 2-dimension
		 */
		public static double getDistance(float sX, float tX, float sY, float tY) {
			float x = sX - tX;
			float y = sY - tY;
			return Math.sqrt((x * x) + (y * y));
		}

		/**
		 * Get mid-point between two points in 2-dimension
		 */
		public static PointF getMidPoint(float sX, float tX, float sY, float tY) {
			return new PointF((sX + tX) / 2, (sY + tY) / 2);
		}
	}

	public static class ImageViewUtil {
		/**
		 * Get a matrix by the original and target size, which makes the image to fit the target size. Then move to center
		 */
		public static Matrix setMatrix(float targetX, float originX, float targetY, float originY) {
			float scaleX = targetX / originX;
			float scaleY = targetY / originY;
			float scaleFinal = scaleX >= scaleY ? scaleY : scaleX;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleFinal, scaleFinal);

			float finalX = originX * scaleFinal;
			float finalY = originY * scaleFinal;

			if (scaleFinal == scaleX) {
				matrix.postTranslate(0, (targetY - finalY) / 2f);
			} else {
				matrix.postTranslate((targetX - finalX) / 2f, 0);
			}
			return matrix;
		}

		/**
		 * Get a scaled bitmap base on width
		 */
		public static Bitmap scaleBitmapByWidth(Bitmap bitmap, int width) {
			double scale = (double) bitmap.getWidth() / (double) width;
			Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width, (int) (bitmap.getHeight() / scale), false);
			bitmap.recycle();
			return newBitmap;
		}

		/**
		 * Get bitmap left/top/right/bottom/scaleX/scaleY in ImageView
		 */
		public static float[] getBitmapTransStateInImageView(ImageView imageView) {
			float[] values = new float[9];
			imageView.getImageMatrix().getValues(values);
			float[] bounds = new float[6];
			bounds[0] = values[Matrix.MTRANS_X];
			bounds[1] = values[Matrix.MTRANS_Y];
			bounds[2] = (imageView.getWidth() * values[Matrix.MSCALE_X]) + bounds[0];
			bounds[3] = (imageView.getHeight() * values[Matrix.MSCALE_Y]) + bounds[1];
			bounds[4] = values[Matrix.MSCALE_X];
			bounds[5] = values[Matrix.MSCALE_Y];
			return bounds;
		}

		/**
		 * Recycle bitmap in ImageView
		 */
		public static void recycleBitmap(ImageView imageView) {
			if ((BitmapDrawable) imageView.getDrawable() != null) {
				Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
				if ((bitmap != null) && !bitmap.isRecycled()) {
					bitmap.recycle();
				}
				imageView.setImageBitmap(bitmap = null);
			}
		}
	}

	public static class IntegerUtil {
		/**
		 * Convert integer to bit set
		 */
		public static BitSet toBitSet(int number) {
			BitSet bs = new BitSet(Integer.SIZE);
			for (int k = 0; k < Integer.SIZE; k++) {
				if ((number & (1 << k)) != 0) {
					bs.set(k);
				}
			}
			return bs;
		}

		/**
		 * Convert integer to bit string
		 */
		public static String toBitString(int number) {
			String str = "";
			for (int k = 0; k < Integer.SIZE; k++) {
				if ((number & (1 << k)) != 0) {
					str += "1";
				} else {
					str += "0";
				}
			}
			return str;
		}
	}

	public static class BitUtil {
		/**
		 * Convert bit set to integer
		 */
		public static int toInt(BitSet bs) {
			int i = 0;
			for (int pos = -1; (pos = bs.nextSetBit(pos + 1)) != -1;) {
				i |= (1 << pos);
			}
			return i;
		}

		/**
		 * Convert bit string to integer
		 */
		public static int toInt(String bs) {
			int i = 0;
			for (int pos = -1; (pos = bs.indexOf(pos + 1)) != -1;) {
				i |= (1 << pos);
			}
			return i;
		}
	}

	public static class UriUtil {
		/**
		 * Convert file uri(file://) to content uri(content://)
		 */
		public static Uri toContentUri(Context context, File imageFile) {
			String filePath = imageFile.getAbsolutePath();
			Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ", new String[] { filePath }, null);

			if ((cursor != null) && cursor.moveToFirst()) {
				int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
				Uri baseUri = Uri.parse("content://media/external/images/media");
				return Uri.withAppendedPath(baseUri, "" + id);
			} else {
				if (imageFile.exists()) {
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.DATA, filePath);
					return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
				} else {
					return null;
				}
			}
		}

		/**
		 * Convert file uri(file://) to content uri(content://)
		 */
		public static String toFileUri(Context context, Uri contentUri) {
			String[] proj = { MediaStore.Images.Media.DATA };

			CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
			Cursor cursor = cursorLoader.loadInBackground();

			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}

	}

	public static class ListenerUtil {
		/**
		 * Get a touch event listener with scale handle
		 */
		public static OnTouchListener getScaleTouchListener(ImageView imageView) {
			class ScaleTouchListener implements OnTouchListener {
				private final int NONE = 0;
				private final int DRAG = 1;
				private final int ZOOM = 2;
				private int mMode;
				private ImageView mImageView;

				private Matrix tempMatrix;
				private Matrix startMatrix;

				private PointF startPoint;
				private PointF startMidPoint;
				private float startDistance;

				public ScaleTouchListener(ImageView imageView) {
					mMode = NONE;
					mImageView = imageView;

					tempMatrix = new Matrix();
					startMatrix = new Matrix();

					startPoint = new PointF();
					startMidPoint = new PointF();
				}

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN:
						mMode = DRAG;
						startPoint.set(event.getX(), event.getY());
						startMatrix.set(mImageView.getImageMatrix());
						tempMatrix.set(startMatrix);

						break;
					case MotionEvent.ACTION_MOVE:
						if (mMode != NONE) {
							tempMatrix.set(startMatrix);
							if (mMode == DRAG) {
								tempMatrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
							} else if (mMode == ZOOM) {
								float scale = (float) getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1)) / startDistance;
								tempMatrix.postScale(scale, scale, startMidPoint.x, startMidPoint.y);
							}
						}

						break;
					case MotionEvent.ACTION_UP:
						mMode = NONE;

						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						mMode = ZOOM;
						startDistance = (float) getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
						startMidPoint = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
						startMatrix.set(mImageView.getImageMatrix());
						tempMatrix.set(startMatrix);

						break;
					case MotionEvent.ACTION_POINTER_UP:
						mMode = NONE;

						break;
					default:
						break;
					}
					mImageView.setImageMatrix(tempMatrix);

					return true;
				}

				/**
				 * Get distance between two points in 2-dimension
				 */
				private double getDistance(float sX, float tX, float sY, float tY) {
					float x = sX - tX;
					float y = sY - tY;
					return Math.sqrt((x * x) + (y * y));
				}

				/**
				 * Get mid-point between two points in 2-dimension
				 */
				private PointF getMidPoint(float sX, float tX, float sY, float tY) {
					return new PointF((sX + tX) / 2, (sY + tY) / 2);
				}
			}
			return new ScaleTouchListener(imageView);
		}
	}

	public static class ViewUtil {
		/**
		 * Get view left/top/right/bottom in parent
		 */
		public static float[] getViewBoundsInParent(View view) {
			float[] values = new float[4];
			values[0] = view.getX() + (view.getPivotX() * (1 - view.getScaleX()));
			values[1] = view.getY() + (view.getPivotY() * (1 - view.getScaleY()));
			values[2] = values[0] + (view.getWidth() * view.getScaleX());
			values[3] = values[1] + (view.getHeight() * view.getScaleY());
			return values;
		}
	}

	public static class OpenCVUtil {
		/**
		 * Show mat as bitmap on image view
		 * 
		 * @param targetMat
		 *            The mat that will be show
		 * @param targetView
		 *            The ImageView will show the mat
		 */
		public static void showMatAsImage(Mat targetMat, ImageView targetView) {
			Bitmap bitmap = null;
			if (targetMat != null) {
				bitmap = Bitmap.createBitmap(targetMat.width(), targetMat.height(), Config.ARGB_8888);
				Utils.matToBitmap(targetMat, bitmap);
			}
			targetView.setImageBitmap(bitmap);
		}

		/**
		 * Combine all MatOfPoint in list into a single one.
		 * 
		 * @param contours
		 *            The target list of MatOfPoint.
		 * @return The original list with one MatOfPoint.
		 */
		public static List<MatOfPoint> combineContour(List<MatOfPoint> contours) {
			List<org.opencv.core.Point> mixPoints = new ArrayList<org.opencv.core.Point>();
			for (int k = 0; k < contours.size(); k++) {
				mixPoints.addAll(contours.get(k).toList());
				mixPoints.addAll(contours.get(k).toList());
			}
			contours.clear();
			contours.add(new MatOfPoint(mixPoints.toArray(new org.opencv.core.Point[mixPoints.size()])));
			mixPoints.clear();
			mixPoints = null;
			return contours;
		}
	}

}
