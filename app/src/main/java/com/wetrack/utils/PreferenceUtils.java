package com.wetrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wetrack.BaseApplication;

public class PreferenceUtils {
	public static final String KEY_USERNAME = "username";
	public static final String KEY_TOKEN = "token";

	public static final String PREFERENCE_NAME = "setting";
	
	
	private static SharedPreferences preferences;
	
	public static void initPreference() {
		preferences = BaseApplication.getContext().getSharedPreferences(PREFERENCE_NAME, BaseApplication.getContext().MODE_PRIVATE);
	}
	
	public static String getStringValue(String key) {
		if(preferences == null)
			initPreference();
		return preferences.getString(key, "");
	}
	
	public static float getFloatValue(String key) {
		if(preferences == null)
			initPreference();
		return preferences.getFloat(key, 0);
	}
	
	public static boolean getBooleanValue(String key) {
		if(preferences == null)
			initPreference();
		return preferences.getBoolean(key, false);
	}
	
	public static int getIntValue(String key) {
		if(preferences == null)
			initPreference();
		return preferences.getInt(key, 1);
	}
	
	public static long getLongValue(String key) {
		if(preferences == null)
			initPreference();
		return preferences.getLong(key, 0);
	}
	
	public static void saveStringValue(String key,String info) {
		if(preferences == null)
			initPreference();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, info);
		editor.commit();
	}
	
	public static void saveFloatValue(String key,float info) {
		if(preferences == null)
			initPreference();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(key, info);
		editor.commit();
	}
	
	public static void saveBooleanValue(String key,Boolean info) {
		if(preferences == null)
			initPreference();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, info);
		editor.commit();
	}
	
	public static void saveIntValue(String key, int info) {
		if(preferences == null)
			initPreference();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, info);
		editor.commit();
	}
	
	public static void saveLongValue(String key, long value) {
		if(preferences == null)
			initPreference();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
}
