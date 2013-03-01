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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Pool;

/**
 * An implementation of the {@link Input} interface for Android.
 * 
 * @author mzechner
 * 
 */
public final class AndroidInputLW implements Input, SensorEventListener {

	class TouchEvent {
		static final int TOUCH_DOWN = 0;
		static final int TOUCH_UP = 1;
		static final int TOUCH_DRAGGED = 2;

		static final int TOUCH_TAP = 3;
		static final int TOUCH_DROP = 4;

		long timeStamp;
		int type;
		int x;
		int y;
		int pointer;
	}

	Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {
		protected TouchEvent newObject() {
			return new TouchEvent();
		}
	};

	ArrayList<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	int[] touchX = new int[20];
	int[] touchY = new int[20];
	boolean[] touched = new boolean[20];
	int[] realId = new int[0];
	// final boolean hasMultitouch;
	private SensorManager manager;
	public boolean accelerometerAvailable = false;
	private final float[] accelerometerValues = new float[3];
	final AndroidApplicationLW app;
	private final AndroidTouchHandlerLW touchHandler;
	private int sleepTime = 0;
	private boolean catchBack = false;
	private final Vibrator vibrator;
	private boolean compassAvailable = false;
	boolean keyboardAvailable;
	private final float[] magneticFieldValues = new float[3];
	private float azimuth = 0;
	private float pitch = 0;
	private float roll = 0;
	// private float inclination = 0;
	private boolean justTouched = false;
	private InputProcessor processor;
	private final AndroidApplicationConfiguration config;

	public AndroidInputLW(AndroidApplicationLW activity,
			AndroidApplicationConfiguration config) {
		this.config = config;

		for (int i = 0; i < realId.length; i++)
			realId[i] = -1;
		this.app = activity;
		this.sleepTime = config.touchSleepTime;

		touchHandler = new AndroidSingleTouchHandlerLW();

		vibrator = (Vibrator) activity.getService().getSystemService(
				Context.VIBRATOR_SERVICE);

	}

	@Override
	public float getAccelerometerX() {
		return accelerometerValues[0];
	}

	@Override
	public float getAccelerometerY() {
		return accelerometerValues[1];
	}

	@Override
	public float getAccelerometerZ() {
		return accelerometerValues[2];
	}

	@Override
	public int getX() {
		synchronized (this) {
			return touchX[0];
		}
	}

	@Override
	public int getY() {
		synchronized (this) {
			return touchY[0];
		}
	}

	@Override
	public int getX(int pointer) {
		synchronized (this) {
			return touchX[pointer];
		}
	}

	@Override
	public int getY(int pointer) {
		synchronized (this) {
			return touchY[pointer];
		}
	}

	public boolean isTouched(int pointer) {
		synchronized (this) {
			return touched[pointer];
		}
	}

	@Override
	public boolean isTouched() {
		synchronized (this) {
			return touched[0];
		}
	}

	@Override
	public void setInputProcessor(InputProcessor processor) {
		synchronized (this) {
			this.processor = (InputProcessor) processor;
		}
	}

	void processEvents() {
		synchronized (this) {
			justTouched = false;

			if (processor != null) {

				if (this.processor instanceof InputProcessorLW) {
					final InputProcessorLW processor = (InputProcessorLW)this.processor;
					
					int len = touchEvents.size();
					for (int i = 0; i < len; i++) {
						
						TouchEvent e = touchEvents.get(i);
						switch (e.type) {
						case TouchEvent.TOUCH_DOWN:
							processor.touchDown(e.x, e.y, e.pointer, Buttons.LEFT);
							justTouched = true;
							break;
						case TouchEvent.TOUCH_UP:
							processor.touchUp(e.x, e.y, e.pointer, Buttons.LEFT);
							break;
						case TouchEvent.TOUCH_DRAGGED:
							processor.touchDragged(e.x, e.y, e.pointer);
						case TouchEvent.TOUCH_TAP:
							processor.touchTap(e.x, e.y);
							justTouched = true;
							break;
						case TouchEvent.TOUCH_DROP:
							processor.touchDrop(e.x, e.y);
							justTouched = true;
							break;
						}
						usedTouchEvents.free(e);
					}
				}
			} else {
				int len = touchEvents.size();
				for (int i = 0; i < len; i++) {
					TouchEvent e = touchEvents.get(i);
					if (e.type == TouchEvent.TOUCH_TAP)
						justTouched = true;
					usedTouchEvents.free(e);
				}

			}

			touchEvents.clear();
		}
	}

	boolean requestFocus = true;

	
	public boolean onDown(int pX, int pY) {

		// synchronized in handler.postTouchEvent()
		touchHandler.onDown(pX, pY, this);

		if (sleepTime != 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		return true;
	}
	public boolean onDragged(int pX, int pY) {

		// synchronized in handler.postTouchEvent()
		touchHandler.onDragged(pX, pY, this);
		if (sleepTime != 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		return true;
	}
	public boolean onTap(int pX, int pY) {

		// synchronized in handler.postTouchEvent()
		touchHandler.onTap(pX, pY, this);

		if (sleepTime != 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		return true;
	}

	public boolean onDrop(int pX, int pY) {

		// synchronized in handler.postTouchEvent()
		touchHandler.onDrop(pX, pY, this);

		if (sleepTime != 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelerometerValues, 0,
					accelerometerValues.length);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magneticFieldValues, 0,
					magneticFieldValues.length);
		}
	}

	@Override
	public void setCatchBackKey(boolean catchBack) {
		this.catchBack = catchBack;
	}

	@Override
	public void vibrate(int milliseconds) {
		vibrator.vibrate(milliseconds);
	}

	@Override
	public void vibrate(long[] pattern, int repeat) {
		vibrator.vibrate(pattern, repeat);
	}

	@Override
	public void cancelVibrate() {
		vibrator.cancel();
	}

	@Override
	public boolean justTouched() {
		return justTouched;
	}

	@Override
	public boolean isButtonPressed(int button) {
		if (button == Buttons.LEFT)
			return isTouched();
		else
			return false;
	}

	final float[] R = new float[9];
	final float[] orientation = new float[3];

	private void updateOrientation() {
		if (SensorManager.getRotationMatrix(R, null, accelerometerValues,
				magneticFieldValues)) {
			SensorManager.getOrientation(R, orientation);
			azimuth = (float) Math.toDegrees(orientation[0]);
			pitch = (float) Math.toDegrees(orientation[1]);
			roll = (float) Math.toDegrees(orientation[2]);
		}
	}

	@Override
	public float getAzimuth() {
		if (!compassAvailable)
			return 0;

		updateOrientation();
		return azimuth;
	}

	@Override
	public float getPitch() {
		if (!compassAvailable)
			return 0;

		updateOrientation();
		return pitch;
	}

	@Override
	public float getRoll() {
		if (!compassAvailable)
			return 0;

		updateOrientation();
		return roll;
	}

	void registerSensorListeners() {
		if (config.useAccelerometer) {
			manager = (SensorManager) app.getService().getSystemService(
					Context.SENSOR_SERVICE);
			if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
				accelerometerAvailable = false;
			} else {
				Sensor accelerometer = manager.getSensorList(
						Sensor.TYPE_ACCELEROMETER).get(0);
				accelerometerAvailable = manager.registerListener(this,
						accelerometer, SensorManager.SENSOR_DELAY_GAME);
			}
		} else
			accelerometerAvailable = false;

		if (config.useCompass) {
			if (manager == null)
				manager = (SensorManager) app.getService().getSystemService(
						Context.SENSOR_SERVICE);
			Sensor sensor = manager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			if (sensor != null) {
				compassAvailable = accelerometerAvailable;
				if (compassAvailable) {
					compassAvailable = manager.registerListener(this, sensor,
							SensorManager.SENSOR_DELAY_GAME);
				}
			} else {
				compassAvailable = false;
			}
		} else
			compassAvailable = false;
		Gdx.app.log("AndroidInput", "sensor listener setup");
	}

	void unregisterSensorListeners() {
		if (manager != null) {
			manager.unregisterListener(this);
			manager = null;
		}
		Gdx.app.log("AndroidInput", "sensor listener tear down");
	}

	@Override
	public InputProcessor getInputProcessor() {
		return this.processor;
	}

	@Override
	public boolean isPeripheralAvailable(Peripheral peripheral) {
		if (peripheral == Peripheral.Accelerometer)
			return accelerometerAvailable;
		if (peripheral == Peripheral.Compass)
			return compassAvailable;
		if (peripheral == Peripheral.HardwareKeyboard)
			return keyboardAvailable;
		if (peripheral == Peripheral.OnscreenKeyboard)
			return true;
		if (peripheral == Peripheral.Vibrator)
			return vibrator != null;
		// if(peripheral == Peripheral.MultitouchScreen) return hasMultitouch;
		return false;
	}

	public int getFreePointerIndex() {
		int len = realId.length;
		for (int i = 0; i < len; i++) {
			if (realId[i] == -1)
				return i;
		}

		int[] tmp = new int[realId.length + 1];
		System.arraycopy(realId, 0, tmp, 0, realId.length);
		realId = tmp;
		return tmp.length - 1;
	}

	public int lookUpPointerIndex(int pointerId) {
		int len = realId.length;
		for (int i = 0; i < len; i++) {
			if (realId[i] == pointerId)
				return i;
		}

		return -1;
	}

	//

	@Override
	public void getTextInput(TextInputListener arg0, String arg1, String arg2) {
	}

	@Override
	public boolean isKeyPressed(int arg0) {
		return false;
	}

	@Override
	public void setOnscreenKeyboardVisible(boolean arg0) {
	}

	@Override
	public int getDeltaX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDeltaX(int pointer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDeltaY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDeltaY(int pointer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getPlaceholderTextInput(TextInputListener listener,
			String title, String placeholder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getRotationMatrix(float[] matrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getCurrentEventTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCatchMenuKey(boolean catchMenu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Orientation getNativeOrientation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCursorCatched(boolean catched) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCursorCatched() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCursorPosition(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
