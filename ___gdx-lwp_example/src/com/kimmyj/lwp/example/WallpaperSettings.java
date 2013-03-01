package com.kimmyj.lwp.example;

import com.badlogic.gdx.math.MathUtils;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.WindowManager;

public class WallpaperSettings extends PreferenceActivity{
	

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Prefs.init(this);
		final SharedPreferences pref = getSharedPreferences(
				Prefs.PREFERENCES_NAME, Activity.MODE_PRIVATE);
		getPreferenceManager().setSharedPreferencesName(
				Prefs.PREFERENCES_NAME);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//force an update to start lwp in the background-------------------
		//remember to put all updating of your app in render or update - not in onsharedprefschanged function
		//it can crash your app if you use the live preview.
		SharedPreferences.Editor edits = pref.edit();
		edits.putInt("Resume", MathUtils.random(-16777215, 0));		
		edits.commit();
		//---------------------------------------------
		this.setContentView(R.layout.settings);

	}
}
