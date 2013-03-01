/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.kimmyj.lwp.example;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eightbitmage.gdxlw.LibdgxWallpaperApp;

public class LiveWallpaperProgram extends LibdgxWallpaperApp implements OnSharedPreferenceChangeListener{
	protected SpriteBatch mBatch;
	protected OrthographicCamera mCamera;
	FPSLogger mLogger = new FPSLogger();
	protected ParticleEffect mEffect;
	protected int width, height;
	private boolean triggerUpdateScreen = false;
	@Override
	public void create () {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		mCamera = new OrthographicCamera(width, height);
		mEffect = new ParticleEffect();
		mEffect.load(Gdx.files.internal("data/stars"), Gdx.files.internal("data"));
		mEffect.setPosition(width/2, height/2);
		mBatch = new SpriteBatch();
		mBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);	
		mBatch.setProjectionMatrix(mCamera.combined);
	}
	
	
	public void update(){
		// TODO Auto-generated method stub
		
	}
	public void render () {	
		update();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL10.GL_BLEND);   
		Gdx.gl.glViewport(0, 0, width,height);
		if(triggerUpdateScreen){
			triggerUpdateScreen = false;
			mCamera.setToOrtho(false,width, height) ;
			mBatch.setProjectionMatrix(mCamera.combined);
			mEffect.setPosition(width/2, height/2);
		}
		mCamera.update();
		mEffect.update(Gdx.graphics.getDeltaTime()/1.6f);
		mBatch.begin();
		mEffect.draw(mBatch);
		mBatch.end();
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub	
	}
	
	public void onSurfaceChanged(int width, int height) {
		this.width = width;
		this.height = height;
		triggerUpdateScreen  = true;
		
	}
	
	@Override
	public void resize(int width, int height) {

	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void offsetChange(float xOffset, float yOffset,float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
	}
}
