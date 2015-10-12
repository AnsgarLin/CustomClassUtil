package com.custom.android.view;

public interface Custom {
	/**
	 * Set stick parameters with default value, and will not be changed during run time
	 */
	void initStickConfigure();

	/**
	 * Set changeable parameters with default value, and will be changed during run time
	 */
	void initConfigure();

	/**
	 * Override onDetachedFromWindow to Release resource and reference
	 */
}