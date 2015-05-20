package com.custom.opencv.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.List;

public class OpenCVUtil {
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
