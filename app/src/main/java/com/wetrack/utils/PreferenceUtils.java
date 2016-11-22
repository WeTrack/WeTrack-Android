package com.wetrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
	public static final String KEY_USERNAME = "username";
	public static final String KEY_TOKEN = "token";

	public static final String PREFERENCE_NAME = "setting";
	
	
	private static SharedPreferences preferences;
	
	public static void initPreference(Context context) {
		preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}
	
	public static String getStringValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getString(key, "");
	}
	
	public static float getFloatValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getFloat(key, 0);
	}
	
	public static boolean getBooleanValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getBoolean(key, false);
	}
	
	public static int getIntValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getInt(key, 1);
	}
	
	public static long getLongValue(Context context, String key) {
		if(preferences == null && context != null) 
			initPreference(context);
		return preferences.getLong(key, 0);
	}
	
	public static void saveStringValue(Context context, String key,String info) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, info);
		editor.commit();
	}
	
	public static void saveFloatValue(Context context, String key,float info) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(key, info);
		editor.commit();
	}
	
	public static void saveBooleanValue(Context context, String key,Boolean info) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, info);
		editor.commit();
	}
	
	public static void saveIntValue(Context context, String key, int info) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, info);
		editor.commit();
	}
	
	public static void saveLongValue(Context context, String key, long value) {
		if(preferences == null && context != null) 
			initPreference(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
}
