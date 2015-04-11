package com.customclassutil.rule;

import android.util.AttributeSet;

interface CustomView {
	/**
	 * Set custom value in xml
	 */
	void initAttr(AttributeSet attrs);

	/**
	 * Set stick parameters with default value, and will not be changed during run time
	 */
	void initStickConfigure();

	/**
	 * Set changeable parameters with default value, and will be changed during run time
	 */
	void initConfigure();

	/**
	 * Set view or listener, should always call after setContentView() in onCreate()
	 */
	void initView();

	/**
	 * Override onDetachedFromWindow to Release resource and reference
	 */
}
