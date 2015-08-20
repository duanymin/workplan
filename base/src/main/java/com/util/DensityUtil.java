package com.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import org.jetbrains.annotations.NotNull;

public class DensityUtil {
	// 设计图的屏幕宽度
	private final static double WIDTH = 720;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(@NotNull Context context, float dpValue) {
		final float scale = getDisplay(context).density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(@NotNull Context context, float pxValue) {
		final float scale = getDisplay(context).density;
		return (int) (pxValue / scale + 0.5f);
	}

	/***
	 * 屏幕的宽高相关
	 * 
	 * @param context
	 */
	public static DisplayMetrics getDisplay(@NotNull Context context) {
		return context.getResources().getDisplayMetrics();
	}

	public static double getRatio(@NotNull Context context) {
		// 屏幕宽
		double screenwidth = DensityUtil.getDisplay(context).widthPixels;
		// 比值
		double ratio = screenwidth / WIDTH;
		return ratio;
	}

	/***
	 * 状态栏高度
	 */
	public static int getDecorHeight(@NotNull Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}
	
	/***
	 * actionBar高度  已经转化为dp
	 */
	public static int getActionBarHeight(@NotNull Activity activity){
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
		}
		int margenTop = px2dip(activity, (float) (actionBarHeight));
		return margenTop;
	}
}
