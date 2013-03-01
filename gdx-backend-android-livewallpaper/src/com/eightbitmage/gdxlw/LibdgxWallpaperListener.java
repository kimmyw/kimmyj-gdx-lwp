package com.eightbitmage.gdxlw;


public interface LibdgxWallpaperListener {
	
	public void offsetChange (float xOffset, float yOffset,
			float xOffsetStep, float yOffsetStep, int xPixelOffset,
			int yPixelOffset);
	
	public void setIsPreview(boolean isPreview);

}
