package com.customclassutil.util;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ResourceUtil {
	/**
	 * Get a real image size for resource image
	 */
	public static Point getResourceImageSize(Context context, int resourceID) {
		BitmapFactory.Options dimensions = new BitmapFactory.Options();
		dimensions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resourceID, dimensions);
		return new Point(dimensions.outWidth, dimensions.outHeight);
	}
}