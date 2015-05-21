package com.custom.android.listener;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class TouchListenerUtil {

	public interface ScaledListener extends OnTouchListener {
		int NONE = 0;
		int DRAG = 1;
		int ZOOM = 2;

		class TransInfo {
			private PointF mTranslate;
			private float mScale;
			private float mRotation;

			/**
			 * Initial the transition state. Use set() method for setting each state
			 */
			public TransInfo() {
				mTranslate = new PointF(0f, 0f);
				mScale = 1;
				mRotation = 0;
			}

			public void reset() {
				mTranslate.set(0f, 0f);
				mScale = 1;
				mRotation = 0;
			}
			
			public PointF getTranslate() {
				return mTranslate;
			}

			public void setTranslate(float translateX, float translateY) {
				mTranslate.x += (translateX - mTranslate.x);
				mTranslate.y += (translateY - mTranslate.y);
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

			@Override
			public boolean equals(Object o) {
				if (this == o) {
					return true;
				}

				if (!(o instanceof TransInfo)) {
					return false;
				}

				TransInfo target = (TransInfo) o;

				return (mRotation == target.mRotation) && (mScale == target.mScale)
						&& (mTranslate == null ? target.mTranslate == null : mTranslate.equals(target.mTranslate));
			}

			@Override
			public int hashCode() {
				// Start with a non-zero constant.
				int result = 17;

				// Include a hash for each field.
				result = (31 * result) + Float.floatToIntBits(mScale);
				result = (31 * result) + Float.floatToIntBits(mRotation);
				result = (31 * result) + (mTranslate == null ? 0 : mTranslate.hashCode());

				return result;
			}

			@Override
			public String toString() {
				return getClass().getName() + "[" + "Translate = (" + mTranslate.x + ", " + mTranslate.y + ") " + "Scale = " + mScale + " "
						+ "Rotation = " + mRotation + "]";
			}

		}

	}

	/**
	 * A simple class to be extended for developing a listener to handle drag/zoom/rotate event in one.<br>
	 * <strong>Note:</strong> Be aware that a reliable listener for handling drag/zoom/rotate event should always be able to provider user the current
	 * mode and transition state. And always call {@link #onDestroy} to release all references.
	 */
	public abstract static class ScaledTouchListenerImpl implements ScaledListener {
		// The target view
		protected View mView;

		// Record current mode
		protected int mMode;

		// Record the transition state between the current and the begin
		protected TransInfo mTransInfo;

		// Whether drag event should be triggered on one finger, default is true
		protected boolean mSingleDrag;

		// Whether rotate event should be triggered, default is true
		protected boolean mRotatable;

		public ScaledTouchListenerImpl(View view) {
			initConfigure(view);
		}

		/**
		 * Set changeable parameters with default value, and will be changed during run time
		 */
		private void initConfigure(View view) {
			mView = view;
			mMode = NONE;
			mTransInfo = new TransInfo();
			mSingleDrag = true;
			mRotatable = true;
		}
		
		/**
		 * Release the all references
		 */
		public void onDestroy() {
			mView = null;
			mTransInfo = null;
		}

		/**
		 * Reset transition state
		 */
		public void onReset() {
			setTransInfo(new TransInfo());
		}

		/**
		 * Override this method for doing things <strong>before</strong> handling drag/scale/rotate event.<br>
		 * 
		 * @param v
		 *            The view the touch event has been dispatched to.
		 * @param event
		 *            The MotionEvent object containing full information about the event.
		 * @return False by default to pass the event to {@link #onCustomTouch}, true otherwise.
		 */
		public boolean onTouchBefore(View v, MotionEvent event) {
			return false;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean result = onTouchBefore(v, event);
			if (!result) {
				result = onCustomTouch(v, event);
			}
			onTouchAfter(v, event);
			return result;
		}

		/**
		 * Override this method for doing things <strong>after</strong> {@link #onCustomTouch} or {@link #onTouchBefore}.
		 * 
		 * @param v
		 *            The view the touch event has been dispatched to.
		 * @param event
		 *            The MotionEvent object containing full information about the event.
		 */
		public void onTouchAfter(View v, MotionEvent event) {
		}

		/**
		 * Set the target view to be scaled
		 */
		public void setView(View view) {
			mView = view;
		}

		/**
		 * Get the target view to be scaled
		 */
		public View getView() {
			return mView;
		}

		/**
		 * Get current mode(Drag/Zoom(Scale))
		 */
		public int getMode() {
			return mMode;
		}

		/**
		 * Override this method for setting transition state from outside
		 */
		public abstract void setTransInfo(TransInfo transInfo);
		
		/**
		 * Get transition state between each step
		 */
		public TransInfo getTransInfo() {
			return mTransInfo;
		}

		/**
		 * Get whether drag event will be triggered on one finger
		 */
		public boolean isSingleDrag() {
			return mSingleDrag;
		}

		/**
		 * Set whether drag event should be triggered on one finger
		 */
		public void setSingleDrag(boolean singleDrag) {
			mSingleDrag = singleDrag;
		}

		/**
		 * Get whether rotate event will be triggered
		 */
		public boolean isRotatable() {
			return mRotatable;
		}

		/**
		 * Set whether rotate event should be triggered
		 */
		public void setRotatable(boolean rotatable) {
			mRotatable = rotatable;
		}

		/**
		 * Get distance between two points in 2-dimension
		 */
		protected float getDistance(float sX, float tX, float sY, float tY) {
			float x = sX - tX;
			float y = sY - tY;
			return (float) Math.sqrt((x * x) + (y * y));
		}

		/**
		 * Get mid-point between two points in 2-dimension
		 */
		protected PointF getMidPoint(float sX, float tX, float sY, float tY) {
			return new PointF((sX + tX) / 2, (sY + tY) / 2);
		}

		/**
		 * Override this method for handling drag/scale/rotate event.
		 */
		public abstract boolean onCustomTouch(View v, MotionEvent event);

		/**
		 * A customized class used for rotation
		 */
		protected class Vector2D extends PointF {
			protected Vector2D(float x, float y) {
				super(x, y);
				normalize();
			}

			protected void normalize() {
				float length = (float) Math.sqrt((x * x) + (y * y));
				x /= length;
				y /= length;
			}

			protected float getAngle(Vector2D vector) {
				return (float) ((180.0 / Math.PI) * (Math.atan2(vector.y, vector.x) - Math.atan2(y, x)));
			}

		}
	}

	/**
	 * A listener for handling drag/scale/rotate event of the bitmap inside the target ImageView.<br>
	 * <strong>Note:</strong> The target ImageView's LayoutParams should be set to {@link LayoutParams#WRAP_CONTENT} and scale type must set to
	 * {@link ScaleType#MATRIX}. After using, be aware to use {@link #onDestroy()} to release all references. <br>
	 * <strong>Usage:</strong> <strong>targetView</strong>.setOnTouchListener(new ScaledImageViewTouchListener())
	 */
	public static class ScaledImageViewTouchListener extends ScaledTouchListenerImpl {
		// Start point of the first finger point
		private PointF startPoint;

		// Record the state while the second finger touch
		// Distance between two finger point
		private float startDistance;
		// Middle point of two finger point
		private PointF startMidPoint;
		private Matrix startMatrix;
		// Vector of the line between fingers
		private Vector2D startVector;

		// Record current transition state
		// Translate
		private PointF tmpTranlate;
		// Scale
		private float tmpScale;
		// Image matrix
		private Matrix tmpMatrix;
		// Rotate
		private float tmpRotate;

		public ScaledImageViewTouchListener(ImageView view) {
			super(view);
			initConfigure();
		}

		private void initConfigure() {
			startPoint = new PointF();
			startMatrix = new Matrix();

			tmpTranlate = new PointF();
			tmpMatrix = new Matrix();
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public boolean onCustomTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (mSingleDrag) {
					mMode = DRAG;
					startPoint.set(event.getX(), event.getY());
					startMatrix.set(((ImageView) v).getImageMatrix());
				}

				break;
			case MotionEvent.ACTION_MOVE:
				if (mMode != NONE) {
					tmpMatrix.set(startMatrix);
					if (mMode == DRAG) {
						// Calculate the current transition state
						// Translate
						tmpTranlate.x = event.getX() - startPoint.x;
						tmpTranlate.y = event.getY() - startPoint.y;
					} else if (mMode == ZOOM) {
						// Calculate the current transition state
						PointF curPivot = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
						// Translate
						tmpTranlate.x = curPivot.x - startMidPoint.x;
						tmpTranlate.y = curPivot.y - startMidPoint.y;
						// Scale
						tmpScale = getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1)) / startDistance;
						// Set the current transition state to target view
						tmpMatrix.postScale(tmpScale, tmpScale, curPivot.x, curPivot.y);

						// Record the current transition state
						mTransInfo.setScale(tmpScale);

						if (mRotatable) {
							// Calculate the current transition state
							// Rotate
							tmpRotate = startVector.getAngle(new Vector2D(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0)));
							// Set the current transition state to target view
							tmpMatrix.postRotate(tmpRotate, v.getWidth() / 2, v.getHeight() / 2);
							// Record the current transition state
							mTransInfo.setRotation(tmpRotate);
						}
					}
					// Set the current transition state to target view
					tmpMatrix.postTranslate(tmpTranlate.x, tmpTranlate.y);
					((ImageView) v).setImageMatrix(tmpMatrix);

					// Record the current transition state
					mTransInfo.setTranslate(tmpTranlate.x, tmpTranlate.y);
				}

				break;
			case MotionEvent.ACTION_UP:
				mMode = NONE;

				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mMode = ZOOM;

				startDistance = getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				startMidPoint = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				startMatrix.set(((ImageView) v).getImageMatrix());
				if (mRotatable) {
					startVector = new Vector2D(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
				}

				tmpMatrix.set(startMatrix);

				break;
			case MotionEvent.ACTION_POINTER_UP:
				if (mSingleDrag) {
					mMode = DRAG;
					// If release the second finger, use first finger point to do drag as normal.
					if (event.getActionIndex() == 1) {
						startPoint.set(event.getX(0), event.getY(0));
					} else {
						startPoint.set(event.getX(1), event.getY(1));
					}
					startMatrix.set(((ImageView) v).getImageMatrix());
				} else {
					mMode = NONE;
				}
				break;
			default:
				break;
			}

			return true;
		}
	
		@Override
		public void setTransInfo(TransInfo transInfo) {
			// Set the current transition state to target view
			tmpMatrix.reset();
			tmpMatrix.postTranslate(transInfo.getTranslate().x, transInfo.getTranslate().y);
			mTransInfo.setScale(transInfo.getScale());
			if (mRotatable) {
				mTransInfo.setRotation(transInfo.getRotation());
			}
			((ImageView) mView).setImageMatrix(tmpMatrix);
			
			// Record the current transition state
			mTransInfo.setTranslate(transInfo.getTranslate().x, transInfo.getTranslate().y);
			mTransInfo.setScale(transInfo.getScale());
			mTransInfo.setRotation(transInfo.getRotation());
		}
	}

	/**
	 * A listener for handling drag/scale/rotate event of view.<br>
	 * <strong>Note:</strong> The target view should be contained in a <strong>FrameLayout</strong>, which will be bigger than the target view, or the
	 * size will be limited by right and bottom bounds while scaling. And be aware to use {@link #onDestroy()} to release all references. <br>
	 * <strong>Usage:</strong> <strong>TargetFrameLayout</strong>.setOnTouchListener(new ScaledLayoutListener(<strong>targetView</strong>));
	 */
	public static class ScaledLayoutListener extends ScaledTouchListenerImpl {
		// Record current layout state
		private float mWidth;
		private float mHeight;
		private float mLeft;
		private float mTop;

		// Start point of the first finger point
		private PointF startPoint;

		// Record the state while the second finger touch
		// Distance between two finger point
		private float startDistance;
		// Middle point of two finger point
		private PointF startMidPoint;
		// Rotation degree of target view
		private float startRotate;
		// Vector of the line between fingers
		private Vector2D startVector;

		// Record current transition state
		// Shift
		private PointF tmpTranslate;
		// Scale
		private float tmpScale;
		// Rotate
		private float tmpRotate;

		public ScaledLayoutListener(View view) {
			super(view);

			startPoint = new PointF();

			tmpTranslate = new PointF();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public void onReset() {
			super.onReset();
		}

		@Override
		public boolean onCustomTouch(View v, MotionEvent event) {
			// No view to control, just ignore.
			if (mView == null) {
				Log.e("ScaledLayoutListener", "Need to use initConfigure(View view) to assign a view to control");
				return false;
			}

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (mSingleDrag) {
					mMode = DRAG;
					recordLayoutState();
					startPoint.set(event.getX(), event.getY());
				}

				break;
			case MotionEvent.ACTION_MOVE:
				if (mMode != NONE) {
					ViewGroup.LayoutParams tmpLayoutParams = mView.getLayoutParams();
					if (mMode == DRAG) {
						// Calculate the current transition state
						// Translate
						tmpTranslate.set(event.getX() - startPoint.x, event.getY() - startPoint.y);
					} else if (mMode == ZOOM) {
						// Calculate the current transition state
						// Scale
						PointF curPivot = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
						tmpScale = getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1)) / startDistance;
						// Shift is based on two things: 1. The width transition with the mid points' position. 2. The transition of mid points.
						tmpTranslate.set(((mWidth - (mWidth * tmpScale)) * ((startMidPoint.x - mLeft) / mWidth)) + (curPivot.x - startMidPoint.x),
								((mHeight - (mHeight * tmpScale)) * ((startMidPoint.y - mTop) / mHeight)) + (curPivot.y - startMidPoint.y));

						// Set the current transition state to target view
						tmpLayoutParams.width = (int) (mWidth * tmpScale);
						tmpLayoutParams.height = (int) (mHeight * tmpScale);

						// Record the current transition state
						mTransInfo.setScale(tmpScale);

						if (mRotatable) {
							// Calculate the current transition state
							// Rotate
							tmpRotate = startRotate
									+ startVector.getAngle(new Vector2D(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0)));
							// Set the current transition state to target view
							mView.setRotation(tmpRotate);
							// Record the current transition state
							mTransInfo.setRotation(tmpRotate);
						}
					}
					// Set the current transition state to target view
					((FrameLayout.LayoutParams) tmpLayoutParams).leftMargin = (int) (mLeft + tmpTranslate.x);
					((FrameLayout.LayoutParams) tmpLayoutParams).topMargin = (int) (mTop + tmpTranslate.y);
					mView.setLayoutParams(tmpLayoutParams);

					// Record the current transition state
					mTransInfo.setTranslate(tmpTranslate.x, tmpTranslate.y);
				}

				break;
			case MotionEvent.ACTION_UP:
				mMode = NONE;

				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mMode = ZOOM;

				recordLayoutState();

				startDistance = getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				startMidPoint = getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				if (mRotatable) {
					startRotate = mView.getRotation();
					startVector = new Vector2D(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
				}

				break;
			case MotionEvent.ACTION_POINTER_UP:
				if (mSingleDrag) {
					mMode = DRAG;
					recordLayoutState();
					// If release the second finger, use first finger point to do drag as normal.
					if (event.getActionIndex() == 1) {
						startPoint.set(event.getX(0), event.getY(0));
					} else {
						startPoint.set(event.getX(1), event.getY(1));
					}
				} else {
					mMode = NONE;
				}

				break;
			default:
				break;
			}
			return true;
		}

		/**
		 * Record target view's current layout state
		 */
		private void recordLayoutState() {
			mWidth = ((FrameLayout.LayoutParams) mView.getLayoutParams()).width;
			mHeight = ((FrameLayout.LayoutParams) mView.getLayoutParams()).height;
			mLeft = ((FrameLayout.LayoutParams) mView.getLayoutParams()).leftMargin;
			mTop = ((FrameLayout.LayoutParams) mView.getLayoutParams()).topMargin;
		}

		@Override
		public void setTransInfo(TransInfo transInfo) {
			// Set the current transition state to target view
			ViewGroup.LayoutParams tmpLayoutParams = mView.getLayoutParams();
			tmpLayoutParams.width = (int) (tmpLayoutParams.width / mTransInfo.getScale() * transInfo.getScale());
			tmpLayoutParams.height = (int) (tmpLayoutParams.height / mTransInfo.getScale() * transInfo.getScale());
			((FrameLayout.LayoutParams) tmpLayoutParams).leftMargin = (int) transInfo.getTranslate().x;
			((FrameLayout.LayoutParams) tmpLayoutParams).topMargin = (int) transInfo.getTranslate().y;
			mView.setRotation(transInfo.getRotation());
			mView.setLayoutParams(tmpLayoutParams);

			// Record the current transition state
			mTransInfo.setTranslate(transInfo.getTranslate().x, transInfo.getTranslate().y);
			mTransInfo.setScale(transInfo.getScale());
			mTransInfo.setRotation(transInfo.getRotation());
		}
	}

}
