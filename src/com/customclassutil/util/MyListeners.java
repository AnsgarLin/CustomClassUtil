package com.customclassutil.util;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class MyListeners {
	/**
	 * A basic structure for a touch listener which can handle drag/scale/rotate event.
	 */
	public interface ScaledTouchListener extends OnTouchListener {
		int NONE = 0;
		int DRAG = 1;
		int ZOOM = 2;

		/**
		 * Set target view and initialize everything here. if don't, use setView instead at run time.<br>
		 * <Strong>Note:</Strong> After using, call onDestroy() to make sure listener will not keep the target view's reference.
		 */
		ScaledTouchListener initConfigure(View view);

		/**
		 * Release the target view's reference.
		 */
		void onDestroy();

		/**
		 * Reset th target view's reference and transition info
		 */
		void onReset();

		/**
		 * Override this method for doing things <strong>before</strong> handling drag/scale/rotate event.
		 */
		boolean beforeTouch(View v, MotionEvent event);

		/**
		 * Override this method for doing things <strong>after</strong> handling drag/scale/rotate event.
		 */
		boolean aferTouch(View v, MotionEvent event);

		/**
		 * Reset the target view that will be transmitted.
		 */
		void setView(View view);

		/**
		 * Get the target view that will be transmitted.
		 */
		View getView();

		/**
		 * A customized class used for storing the current transition state.
		 */
		class TransInfo {
			private float mWidth;
			private float mHeight;
			private PointF mTopLeft;
			private PointF mPivots;
			private float mScale;
			private float mRotation;

			/**
			 * Initial the transition state, use set method for setting each state
			 */
			public TransInfo() {
				mTopLeft = new PointF();
				mPivots = new PointF();
				mScale = 1;
			}

			public void setWidth(float width) {
				mWidth = width;
			}

			public float getWidth() {
				return mWidth;
			}

			public void setHeight(float height) {
				mHeight = height;
			}

			public float getHeight() {
				return mHeight;
			}

			public void setTopLeft(float left, float top) {
				mTopLeft.x = left;
				mTopLeft.y = top;
			}

			public PointF getTopLeft() {
				return mTopLeft;
			}

			public void setPivots(float x, float y) {
				mPivots.x = x;
				mPivots.y = y;
			}

			public PointF getPivots() {
				return mPivots;
			}

			public void setScale(float scale) {
				mScale = scale;
			}

			public float getScale() {
				return mScale;
			}

			public void setRotation(float rotation) {
				mRotation = rotation;
			}

			public float getRotation() {
				return mRotation;
			}
		}

		/**
		 * A customized class used for rotation
		 */
		class Vector2D extends PointF {
			public Vector2D(float x, float y) {
				super(x, y);
				normalize();
			}

			public void normalize() {
				float length = (float) Math.sqrt((x * x) + (y * y));
				x /= length;
				y /= length;
			}

			public float getAngle(Vector2D vector) {
				return (float) ((180.0 / Math.PI) * (Math.atan2(vector.y, vector.x) - Math.atan2(y, x)));
			}

		}

	}

	/**
	 * A listener for handling drag/scale/rotate event of view.<br>
	 * <h3>Note:</h3> The target view should be contained in a <strong>FrameLayout</strong>, which will be bigger than the target view, or the size
	 * will be limited by right and bottom bounds while scaling.<br>
	 * <h3>Usage:</h3> <strong>TargetFrameLayout</strong>.setOnTouchListener(new ScaledLayoutListener().initConfigure(<strong>targetView</strong>));
	 */
	public static abstract class ScaledLayoutListener implements ScaledTouchListener {
		protected int mMode;

		protected View mView;
		protected TransInfo mTransInfo;

		private float preWidth;
		private float preHeight;

		private float preLeft;
		private float preTop;

		private float preX;
		private float preY;

		private PointF preMidPoint;
		private float preDistance;

		private float preRotate;
		private Vector2D preVector;

		public ScaledLayoutListener() {
			mTransInfo = new TransInfo();
		}

		/**
		 * @param view
		 *            The target view's layout will be changed by touch event.
		 */
		@Override
		public ScaledLayoutListener initConfigure(View view) {
			mView = view;

			return this;
		}

		@Override
		public void onDestroy() {
			mView = null;
			mTransInfo = null;
		}

		@Override
		public void onReset() {
			mView = null;
			mTransInfo = new TransInfo();
		}

		/**
		 * Return false by default to pass the event to onTouch, true otherwise.
		 */
		@Override
		public boolean beforeTouch(View v, MotionEvent event) {
			return false;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (beforeTouch(v, event)) {
				return aferTouch(v, event);
			}

			// No view to control, just ignore.
			if (mView == null) {
				Log.e("ScaledLayoutListener", "Need to use initConfigure(View view) to assign a view to control");
				return aferTouch(v, event);
			}

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mMode = DRAG;

				recordLayoutState();

				preX = event.getX();
				preY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mMode != NONE) {
					if (mMode == DRAG) {
						mTransInfo.setTopLeft(preLeft + (event.getX() - preX), preTop + (event.getY() - preY));
						mTransInfo.setWidth(preWidth);
						mTransInfo.setHeight(preHeight);
					} else if (mMode == ZOOM) {
						PointF curPivot = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
						mTransInfo.setScale(getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1)) / preDistance);
						mTransInfo.setWidth(preWidth * mTransInfo.getScale());
						mTransInfo.setHeight(preHeight * mTransInfo.getScale());
						mTransInfo.setTopLeft(preLeft + ((preWidth - mTransInfo.getWidth()) * ((preMidPoint.x - preLeft) / preWidth))
								+ (curPivot.x - preMidPoint.x), preTop
								+ ((preHeight - mTransInfo.getHeight()) * ((preMidPoint.y - preTop) / preHeight)) + (curPivot.y - preMidPoint.y));

						mTransInfo.setRotation(preRotate
								+ preVector.getAngle(new Vector2D(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0))));
						mView.setRotation(mTransInfo.getRotation());
					}
					mView.setLayoutParams(generateLayoutParam(mTransInfo));
				}

				break;
			case MotionEvent.ACTION_UP:
				mMode = NONE;

				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mMode = ZOOM;

				recordLayoutState();

				preDistance = getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				preMidPoint = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				preRotate = mView.getRotation();
				preVector = new Vector2D(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mMode = NONE;

				break;
			default:
				break;
			}

			return aferTouch(v, event);
		}

		/**
		 * Return true by default if the listener has consumed the event, false otherwise.
		 */
		@Override
		public boolean aferTouch(View v, MotionEvent event) {
			return true;
		}

		/**
		 * Get distance between two points in 2-dimension
		 */
		private float getDistance(float sX, float tX, float sY, float tY) {
			float x = sX - tX;
			float y = sY - tY;
			return (float) Math.sqrt((x * x) + (y * y));
		}

		/**
		 * Get mid-point between two points in 2-dimension
		 */
		private PointF getMidPoint(float sX, float tX, float sY, float tY) {
			return new PointF((sX + tX) / 2, (sY + tY) / 2);
		}

		/**
		 * New a layout params for target view based current transition state
		 */
		private LayoutParams generateLayoutParam(TransInfo layoutInfo) {
			ViewGroup.LayoutParams tmpLayoutParams = mView.getLayoutParams();
			tmpLayoutParams.width = (int) layoutInfo.getWidth();
			tmpLayoutParams.height = (int) layoutInfo.getHeight();
			((FrameLayout.LayoutParams) tmpLayoutParams).leftMargin = (int) mTransInfo.getTopLeft().x;
			((FrameLayout.LayoutParams) tmpLayoutParams).topMargin = (int) mTransInfo.getTopLeft().y;
			return tmpLayoutParams;
		}

		/**
		 * Record target view's current layout state
		 */
		private void recordLayoutState() {
			preWidth = ((FrameLayout.LayoutParams) mView.getLayoutParams()).width;
			preHeight = ((FrameLayout.LayoutParams) mView.getLayoutParams()).height;
			preLeft = ((FrameLayout.LayoutParams) mView.getLayoutParams()).leftMargin;
			preTop = ((FrameLayout.LayoutParams) mView.getLayoutParams()).topMargin;
		}

		public int getMode() {
			return mMode;
		}

		public TransInfo getLayoutInfo() {
			return mTransInfo;
		}

		@Override
		public void setView(View view) {
			mView = view;
		}

		@Override
		public View getView() {
			return mView;
		}
	}
}
