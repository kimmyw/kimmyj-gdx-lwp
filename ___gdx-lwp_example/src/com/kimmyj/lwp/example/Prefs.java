package com.kimmyj.lwp.example;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	//helper pref file I like to use! Feel free to use it!
	public static final String COLOR_1 = "Color1"; //name of something to save/load

	public static final String PREFERENCES_NAME = "Live Wallpaper Prefs";
	private static SharedPreferences smPrefs;
	private static SharedPreferences.Editor smPrefsEditor;
	
	//init in order to use it.
	public static void init(Context context){
		smPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		smPrefsEditor = smPrefs.edit();
	}
	public static SharedPreferences getPreferences(){
		return smPrefs;
	}
	public static SharedPreferences.Editor getPrefsEditor(){
		return smPrefsEditor;
	}
	public static void clearVals(){
		smPrefsEditor.clear();
		smPrefsEditor.commit();
	}
	public static void saveFloat(String id, float value){
		smPrefsEditor.putFloat(id, value);
		smPrefsEditor.commit();
	}
	public static void saveString(String id, String value){
		smPrefsEditor.putString(id, value);
		smPrefsEditor.commit();
	}
	public static void saveInt(String id, int value){
		smPrefsEditor.putInt(id, value);
		smPrefsEditor.commit();
	}
	public static void saveBoolean(String id, boolean value){
		smPrefsEditor.putBoolean(id, value);
		smPrefsEditor.commit();
	}

	public static boolean getBoolean(String id, boolean defaultv){
		return smPrefs.getBoolean(id, defaultv);
	}
	public static int getInt(String id, int defaultv){
		return smPrefs.getInt(id, defaultv);
	}
	
	public static float getFloat(String id, float defaultv){
		return smPrefs.getFloat(id, defaultv);
	}
	
	public static String getString(String id, String defaultv){
		return smPrefs.getString(id, defaultv);
	}
}
