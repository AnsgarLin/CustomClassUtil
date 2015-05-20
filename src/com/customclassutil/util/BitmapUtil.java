package com.customclassutil.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.customclassutil.util.MyUtils.InputStreamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class BitmapUtil {
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
