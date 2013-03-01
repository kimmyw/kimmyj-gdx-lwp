package com.kimmyj.lwp.example;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.badlogic.gdx.backends.android.livewallpaper.AndroidApplicationLW;
import com.eightbitmage.gdxlw.LibdgxWallpaperService;


public class LWPStarter extends LibdgxWallpaperService {
		
	static public SharedPreferences prefs;
	@Override
	public Engine onCreateEngine() {
		
		return new ExampleLibdgxWallpaperEngine(this); 
	}

	public class ExampleLibdgxWallpaperEngine extends LibdgxWallpaperEngine implements OnSharedPreferenceChangeListener {

	
		LiveWallpaperProgram app;


		public ExampleLibdgxWallpaperEngine(LibdgxWallpaperService libdgxWallpaperService) {
			super(libdgxWallpaperService);
			this.setTouchEventsEnabled(true);
		}
	
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			app.onSurfaceChanged(width, height);
		}
		
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			// TODO Auto-generated method stub
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			app.offsetChange( xOffset, yOffset,
				xOffsetStep,  yOffsetStep,  xPixelOffset,
				 yPixelOffset);
		}

		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
		}

		@Override
		protected void initialize(AndroidApplicationLW androidApplicationLW) {

			 app = new LiveWallpaperProgram();
			 
			setWallpaperListener(app); 
			prefs = getSharedPreferences(
					Prefs.PREFERENCES_NAME, Activity.MODE_PRIVATE);
			prefs.registerOnSharedPreferenceChangeListener(app);
			prefs.registerOnSharedPreferenceChangeListener(this);
			androidApplicationLW.initialize(app, false);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			//comment out if you dont want live preview.
			if(this.isPreview())
				onResume();
			
		}	
	}
}
	

