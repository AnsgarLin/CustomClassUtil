package com.customclassutil.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class MyUtils {
	public static String WHITE = "#ffffff";

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
