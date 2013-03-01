package com.eightbitmage.gdxlw;

import com.badlogic.gdx.ApplicationListener;

public abstract class LibdgxWallpaperApp implements ApplicationListener,
		LibdgxWallpaperListener {
	
	
	protected boolean isPreview;

	@Override
	public void setIsPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

}
