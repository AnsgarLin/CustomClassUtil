package com.customclassutil.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class MyUtils {

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
