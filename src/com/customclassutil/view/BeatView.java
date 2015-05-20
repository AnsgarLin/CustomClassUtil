package com.customclassutil.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.customclassutil.R;

import java.util.Random;

public class BeatView extends LinearLayout {
	// Numbers of bar
	private int mBarCount;
	private int DEFAULT_BAR_COUNT = 3;
	// Color of bar
	private int mBarColor;
	private String mBarColorString;
	private int DEFAULT_BAR_COLOR = Color.BLACK;
    // Use gradient bar
    private boolean mBarGradientMode;
	private boolean DEFAULT_BAR_GRADIENT_MODE = false;
    // Use gradient bar
    private int mBarGradientColor;
    private String mBarGradientColorString;
	private int DEFAULT_BAR_GRADIENT_COLOR = Color.WHITE;
    // Changing rate(current height/maximum height) of gradient color of bar
    private float mBarGradientColorChangeRate;
	private float DEFAULT_BAR_GRADIENT_COLOR_CHANGE_RATE = 1f;
	// Numbers of value that will be set to bar's height
	private int mBarHeightValueNumber;
	private int DEFAULT_BAR_HEIGHT_VALUE_NUMBER = 20;
	// Start value of bar's height in pixel
	private int mBarHeightStartValue;
	private int DEFAULT_BAR_HEIGHT_START_VALUE = 5;
	// Maximum value of bar's height
	private int mBarHeightMaxValue;
	// Duration of each bar's animation
	private int mBarAnimateDuration;
	private int DEFAULT_BAR_ANIMATE_DURATION = 5000;
	// Margin between each bar
	private int mBarMarginBetween;
	private int DEFAULT_BAR_MARGIN_BETWEEN = 5;
	// Animators that will be used to calculate the bar's height of each frame
	private ValueAnimator[] mBarHeightAnimators;

	public BeatView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(attrs);
		initConfigure();
	}

	public BeatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(attrs);
		initConfigure();
	}

	public BeatView(Context context) {
		super(context);
		init();
		initConfigure();
	}

	/**
	 * Get the custom value in xml
	 * 
	 * @param attrs
	 *            The attributes in xml
	 */
	private void initAttr(AttributeSet attrs) {
		if (attrs == null) {
			return;
		}

		TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.BeatView);
		if (attrArray != null) {
			mBarCount = attrArray.getInt(R.styleable.BeatView_barCount, DEFAULT_BAR_COUNT);
			mBarColor = attrArray.getColor(R.styleable.BeatView_barColor, DEFAULT_BAR_COLOR);
			mBarColorString = rgbToHex(new int[] { Color.red(mBarColor), Color.green(mBarColor), Color.blue(mBarColor) });
			mBarGradientMode = attrArray.getBoolean(R.styleable.BeatView_barGradientMode, DEFAULT_BAR_GRADIENT_MODE);
			mBarGradientColor = attrArray.getColor(R.styleable.BeatView_barGradientColor, DEFAULT_BAR_GRADIENT_COLOR);
			mBarGradientColorString = rgbToHex(new int[] { Color.red(mBarGradientColor), Color.green(mBarGradientColor), Color.blue(mBarGradientColor) });
			mBarGradientColorChangeRate = attrArray.getFloat(R.styleable.BeatView_barGradientColorChangeRate, DEFAULT_BAR_GRADIENT_COLOR_CHANGE_RATE);
			mBarHeightValueNumber = attrArray.getInt(R.styleable.BeatView_barHeightValueCount, DEFAULT_BAR_HEIGHT_VALUE_NUMBER);
			mBarHeightStartValue = attrArray.getInt(R.styleable.BeatView_barHeightStartValue, DEFAULT_BAR_HEIGHT_START_VALUE);
			mBarAnimateDuration = attrArray.getInt(R.styleable.BeatView_barAnimateDuration, DEFAULT_BAR_ANIMATE_DURATION);
			mBarMarginBetween = (int) Math.ceil(attrArray.getFloat(R.styleable.BeatView_barMarginBetween, DEFAULT_BAR_MARGIN_BETWEEN) / 2f);
			attrArray.recycle();
		}
	}

	/**
	 * Set default value
	 */
	private void init() {
		mBarCount = DEFAULT_BAR_COUNT;
		mBarColor = DEFAULT_BAR_COLOR;
		mBarColorString = rgbToHex(new int[] { Color.red(mBarColor), Color.green(mBarColor), Color.blue(mBarColor) });
		mBarGradientMode = DEFAULT_BAR_GRADIENT_MODE;
		mBarGradientColor = DEFAULT_BAR_GRADIENT_COLOR;
		mBarGradientColorString = rgbToHex(new int[] { Color.red(mBarGradientColor), Color.green(mBarGradientColor), Color.blue(mBarGradientColor) });
		mBarGradientColorChangeRate = DEFAULT_BAR_GRADIENT_COLOR_CHANGE_RATE;
		mBarHeightValueNumber = DEFAULT_BAR_HEIGHT_VALUE_NUMBER;
		mBarHeightStartValue = DEFAULT_BAR_HEIGHT_START_VALUE;
		mBarAnimateDuration = DEFAULT_BAR_ANIMATE_DURATION;
		mBarMarginBetween = (int) Math.ceil(DEFAULT_BAR_MARGIN_BETWEEN / 2f);
	}

	/**
	 * Initializing all bars, and setting the listener for getting the bar's maximum height which will be used in animation
	 */
	private void initConfigure() {
		// Construct all bars
		for (int i = 0; i < mBarCount; i++) {
			View bar = new View(getContext());
			LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(0, mBarHeightStartValue);
			if (i == 0) {
				barParams.rightMargin = mBarMarginBetween;
			} else if (i == mBarCount - 1) {
				barParams.leftMargin = (int) mBarMarginBetween;
			} else {
				barParams.rightMargin = (int) mBarMarginBetween;
				barParams.leftMargin = (int) mBarMarginBetween;
			}

			barParams.weight = 1;
			barParams.gravity = Gravity.BOTTOM;
			bar.setLayoutParams(barParams);
			bar.setBackgroundColor(mBarColor);
			addView(bar, i);
		}
	}

	/**
	 * Stop all the bar's animation and release all resources
	 */
	@Override
	protected void onDetachedFromWindow() {
		for (int i = 0; i < mBarCount; i++) {
			mBarHeightAnimators[i].removeAllUpdateListeners();
			mBarHeightAnimators[i].end();
			mBarHeightAnimators[i] = null;
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		// Not null means the animation of bar has been initialized
		if (mBarHeightAnimators == null) {
			// Catch the view's maximum height
			mBarHeightMaxValue = b - t;
			mBarHeightAnimators = new ValueAnimator[mBarCount];

			Random rnd = new Random(System.currentTimeMillis());
			for (int i = 0; i < mBarCount ; i++) {
				int[] barHeightValueSet = new int[mBarHeightValueNumber];

				// Get the value sets for the current animator
				int j = 0;
				for (;j < mBarHeightValueNumber - 1; j++) {
					barHeightValueSet[j] = rnd.nextInt(mBarHeightMaxValue);
				}
				// Make the last value not to too familiar to the second-last
				barHeightValueSet[j] = rnd.nextInt(j - 1);
						
				mBarHeightAnimators[i] = ValueAnimator.ofInt(barHeightValueSet);
				mBarHeightAnimators[i].setInterpolator(new LinearInterpolator());
				mBarHeightAnimators[i].setDuration(mBarAnimateDuration);
				mBarHeightAnimators[i].setRepeatCount(Animation.INFINITE);
				mBarHeightAnimators[i].setRepeatMode(Animation.REVERSE);
				mBarHeightAnimators[i].addUpdateListener(new BarHeightUpdateListener(i));
			}
		}
	}

	// Listener used to update the bar's height
	private class BarHeightUpdateListener implements AnimatorUpdateListener {
		private int mBarIndex;

		public BarHeightUpdateListener(int barIndex) {
			mBarIndex = barIndex;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			LinearLayout.LayoutParams barParams = (LinearLayout.LayoutParams) getChildAt(mBarIndex).getLayoutParams();
			barParams.height = (Integer) animation.getAnimatedValue();
			getChildAt(mBarIndex).setLayoutParams(barParams);

			// Set gradient color
			if (mBarGradientMode) {
				float colorRate = ((Integer) animation.getAnimatedValue() / (float) mBarHeightMaxValue) * mBarGradientColorChangeRate;
				if (colorRate > 1)
					colorRate = 1;

				getChildAt(mBarIndex).setBackgroundColor(Color.parseColor(mixColor(mBarColorString, mBarGradientColorString, colorRate)));
			}
		}
	}
	// =============================================================================
	/**
	 * Start all the bar's animation
	 */
	public void start() {
		for (int i = 0; i < mBarCount; i++) {
			if (!mBarHeightAnimators[i].isStarted()) {
				mBarHeightAnimators[i].start();
			}
		}
	}

	/**
	 * Cancel all the bar's animation
	 */
	public void end() {
		for (int i = 0; i < mBarCount; i++) {
			mBarHeightAnimators[i].end();
		}
	}

	/**
	 * Pause all the bar's animation
	 */
	public void pause() {
		for (int i = 0; i < mBarCount; i++) {
			mBarHeightAnimators[i].pause();
		}
	}

	/**
	 * Resume all the bar's animation
	 */
	public void resume() {
		for (int i = 0; i < mBarCount; i++) {
			mBarHeightAnimators[i].resume();
		}
	}

	/**
	 * Stop all the bar's animation
	 */
	public void stop() {
		for (int i = 0; i < mBarCount; i++) {
			mBarHeightAnimators[i].end();
			LinearLayout.LayoutParams barParams = (LinearLayout.LayoutParams) getChildAt(i).getLayoutParams();
			barParams.height = mBarHeightStartValue;
			getChildAt(i).setLayoutParams(barParams);
		}
	}
	// =============================================================================
	public ValueAnimator[] getBarValueAnimators() {
		return mBarHeightAnimators;
	}
	// =============================================================================
	/**
	 * Mix two color with the front in alpha
	 */
	public static String mixColor(String back, String cover, float coverAlpha) {
		int[] backRGB = hexToRGB(back);
		int[] coverRGB = hexToRGB(cover);
		int[] mixRGB = new int[3];
		for (int i = 0; i < backRGB.length; i++) {
			mixRGB[i] = (int) (backRGB[i] + ((coverRGB[i] - backRGB[i]) * coverAlpha));
		}
		return rgbToHex(mixRGB);
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
}
