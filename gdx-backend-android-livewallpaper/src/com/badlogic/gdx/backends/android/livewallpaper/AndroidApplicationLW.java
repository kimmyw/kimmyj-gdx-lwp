/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 * Modified by Elijah Cornell
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */


package com.badlogic.gdx.backends.android.livewallpaper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.Log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;

/**
 * An implementation of the {@link Application} interface for Android. Create an
 * {@link Activity} that derives from this class. In the
 * {@link Activity#onCreate(Bundle)} method call the
 * {@link #initialize(ApplicationListener, boolean)} method specifying the
 * configuration for the GLSurfaceView.
 * 
 * @author mzechner
 */
public class AndroidApplicationLW implements Application {
	static {
		GdxNativesLoader.load();
	}

	 

	public AndroidApplicationLW(WallpaperService service, Engine engine) {
		this.service = service;
		this.engine = engine;
	}

	protected WallpaperService service;
	private Engine engine;

	protected AndroidGraphicsLW graphics;
	protected AndroidInputLW input;
	protected AndroidAudioLW audio;
	protected AndroidFiles files;
	private ApplicationListener listener;
	protected Handler handler;
	protected final List<Runnable> runnables = new ArrayList<Runnable>();
    protected boolean firstResume = true;

	/**
	 * This method has to be called in the {@link Activity#onCreate(Bundle)}
	 * method. It sets up all the things necessary to get input, render via
	 * OpenGL and so on. If useGL20IfAvailable is set the AndroidApplication
	 * will try to create an OpenGL ES 2.0 context which can then be used via
	 * {@link Graphics#getGL20()}. The {@link GL10} and {@link GL11} interfaces
	 * should not be used when OpenGL ES 2.0 is enabled. To query whether
	 * enabling OpenGL ES 2.0 was successful use the
	 * {@link Graphics#isGL20Available()} method. Sleep time in touch event
	 * handler is 0, so no sleeping is performed.
	 * 
	 * @param listener
	 *            the {@link ApplicationListener} implementing the program logic
	 * @param useGL2IfAvailable
	 *            whether to use OpenGL ES 2.0 if its available.
	 */
	public void initialize(ApplicationListener listener,
			boolean useGL2IfAvailable) {
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
	   	config.useGL20 = useGL2IfAvailable;
		
		initialize(listener, config);
	}

	/**
	 * This method has to be called in the {@link Activity#onCreate(Bundle)}
	 * method. It sets up all the things necessary to get input, render via
	 * OpenGL and so on. If useGL20IfAvailable is set the AndroidApplication
	 * will try to create an OpenGL ES 2.0 context which can then be used via
	 * {@link Graphics#getGL20()}. The {@link GL10} and {@link GL11} interfaces
	 * should not be used when OpenGL ES 2.0 is enabled. To query whether
	 * enabling OpenGL ES 2.0 was successful use the
	 * {@link Graphics#isGL20Available()} method. sleepTime specifies the number
	 * of milliseconds to sleep in the touch event handler. This may be used on
	 * <= 1.6 Android devices. Note that it will not solve the CPU usage problem
	 * of the event handler of the Android system. Things will still slow down.
	 * 
	 * @param listener
	 *            the {@link ApplicationListener} implementing the program logic
	 * @param useGL2IfAvailable
	 *            whether to use OpenGL ES 2.0 if its available.
	 * @param resolutionStrategy
	 *            will position and size the GLSurfaceView by the given strategy
	 * @param sleepTime
	 *            specifies the number of milliseconds to sleep in the touch
	 *            event handler
	 */
	public void initialize(ApplicationListener listener,
			AndroidApplicationConfiguration config) {

//		graphics = new AndroidGraphicsLW(this, useGL2IfAvailable,
//				resolutionStrategy);
//
//		input = new AndroidInputLW(this, sleepTime);
//
//		audio = new AndroidAudioLW(this.getService());
//		files = new AndroidFiles(this.getService().getAssets());
//		this.listener = listener;
//		this.handler = new Handler();
//
//		Gdx.app = this;
//		Gdx.input = this.getInput();
//		Gdx.audio = this.getAudio();
//		Gdx.files = this.getFiles();
//		Gdx.graphics = this.getGraphics();

		//
		
		graphics = new AndroidGraphicsLW(this, config.useGL20, config.resolutionStrategy==null?new FillResolutionStrategy():config.resolutionStrategy);
		input = new AndroidInputLW(this, config);
		audio = new AndroidAudioLW(this.getService());
		files = new AndroidFiles(this.getService().getAssets());
		this.listener = listener;
		this.handler = new Handler();
		
		Gdx.app = this;
		Gdx.input = this.getInput();
		Gdx.audio = this.getAudio();
		Gdx.files = this.getFiles();
		Gdx.graphics = this.getGraphics();
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		setContentView(graphics.getView(), createLayoutParams());
//		createWakeLock(config);
		
	}

	public void onPause() {
		graphics.pause();

		if (audio != null) audio.pause();
		
		//
		
		input.unregisterSensorListeners();
	}

	public void onResume() {
		Gdx.app = this;
		Gdx.input = this.getInput();
		Gdx.audio = this.getAudio();
		Gdx.files = this.getFiles();
		Gdx.graphics = this.getGraphics();

		((AndroidInputLW)getInput()).registerSensorListeners();
		
		if (audio != null) audio.resume();		
		if (!firstResume)
			graphics.resume();
		else
			firstResume = false;
	}

	public void onDestroy() {
		if (audio != null) {
			audio.dispose();
		}		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Audio getAudio() {
		return audio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Files getFiles() {
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Graphics getGraphics() {
		return graphics;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Input getInput() {
		return input;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void log(String tag, String message) {
		Log.d(tag, message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ApplicationType getType() {
		return ApplicationType.Android;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVersion() {
		return Build.VERSION.SDK.charAt(0) - '0';
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return Debug.getNativeHeapAllocatedSize();
	}

	public WallpaperService getService() {
		return service;
	}

	public Engine getEngine() {
		return engine;
	}

	public ApplicationListener getListener() {
		return listener;
	}

	@Override public Preferences getPreferences (String name) {
		return new AndroidPreferencesLW(this.getService().getSharedPreferences(name, Context.MODE_PRIVATE));
	}

	@Override public void postRunnable (Runnable runnable) {
		synchronized(runnables) {
			runnables.add(runnable);
		}
	}
	
//	@Override public void onConfigurationChanged(Configuration config) {
//		super.onConfigurationChanged(config);
//		boolean keyboardAvailable = false;
//		if(config.keyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) keyboardAvailable = true;
//		input.keyboardAvailable = keyboardAvailable;		
//	}

	@Override public void log (String tag, String message, Exception exception) {
		Log.d(tag, message, exception);
	}

	@Override
	public void error(String tag, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String tag, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLogLevel(int logLevel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Net getNet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clipboard getClipboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

}
