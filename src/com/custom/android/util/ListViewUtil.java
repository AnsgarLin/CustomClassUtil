package com.custom.android.util;

import android.widget.ListView;

public class ListViewUtil {
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
