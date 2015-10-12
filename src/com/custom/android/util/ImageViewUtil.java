package com.custom.android.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ImageViewUtil {
	/**
	 * Get a matrix by the original and target size, which makes the image to fit the target size.
	 * Then move to center
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
	 * Recycle bitmap in ImageView. This method only use when the bitmap is generate by programming
	 * and set by {@link ImageView#setImageBitmap(Bitmap)}
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
