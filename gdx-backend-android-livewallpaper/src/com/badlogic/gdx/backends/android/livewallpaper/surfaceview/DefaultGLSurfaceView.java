package com.badlogic.gdx.backends.android.livewallpaper.surfaceview;

import android.service.wallpaper.WallpaperService.Engine;
import android.util.AttributeSet;

import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

public class DefaultGLSurfaceView extends GLBaseSurfaceView {

	final ResolutionStrategy resolutionStrategy;

	public DefaultGLSurfaceView(Engine engine,
			ResolutionStrategy resolutionStrategy) {
		super(engine);
		this.resolutionStrategy = resolutionStrategy;
	}

	public DefaultGLSurfaceView(Engine engine, AttributeSet attrs,
			ResolutionStrategy resolutionStrategy) {
		super(engine, attrs);
		this.resolutionStrategy = resolutionStrategy;
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// ResolutionStrategy.MeasuredDimension measures =
	// resolutionStrategy.calcMeasures(widthMeasureSpec, heightMeasureSpec);
	// setMeasuredDimension(measures.width, measures.height);
	// }

}
