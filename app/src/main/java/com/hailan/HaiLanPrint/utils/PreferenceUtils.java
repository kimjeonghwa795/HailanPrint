package com.hailan.HaiLanPrint.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;

/**
 * SharedPreferences 存储工具类
 * 
 * @author yoghourt
 * 
 */

public class PreferenceUtils {

	public static SharedPreferences getSharedPreferences() {
		return MDGroundApplication.sInstance.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE); //私有数据
	}

	public static String getPrefString(String key, final String defaultValue) {
		return getSharedPreferences().getString(key, defaultValue);
	}

	public static boolean setPrefString(final String key, final String value) {
		return getSharedPreferences().edit().putString(key, value).commit();
	}

	public static boolean getPrefBoolean(final String key, final boolean defaultValue) {
		return getSharedPreferences().getBoolean(key, defaultValue);
	}

	public static boolean hasKey(Context context, final String key) {
		return getSharedPreferences().contains(key);
	}

	public static boolean setPrefBoolean(final String key, final boolean value) {
		return getSharedPreferences().edit().putBoolean(key, value).commit();
	}

	public static boolean setPrefInt(final String key, final int value) {
		return getSharedPreferences().edit().putInt(key, value).commit();
	}

	public static int getPrefInt(final String key, final int defaultValue) {
		return getSharedPreferences().getInt(key, defaultValue);
	}

	public static boolean setPrefFloat(final String key, final float value) {
		return getSharedPreferences().edit().putFloat(key, value).commit();
    }

	public static float getPrefFloat(final String key, final float defaultValue) {
		return getSharedPreferences().getFloat(key, defaultValue);
	}

	public static boolean setPrefLong(final String key, final long value) {
		return getSharedPreferences().edit().putLong(key, value).commit();
	}

	public static long getPrefLong(final String key, final long defaultValue) {
		return getSharedPreferences().getLong(key, defaultValue);
	}


	public static void clearPreference() {
		final Editor editor = getSharedPreferences().edit();
		editor.clear();
		editor.commit();
	}
}
