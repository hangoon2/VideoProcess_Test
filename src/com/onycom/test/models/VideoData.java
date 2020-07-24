package com.onycom.test.models;

import java.awt.Rectangle;

public class VideoData {
	
	public byte[] videoData = null;
	public Rectangle rc = new Rectangle();
	public boolean isKeyFrame = false;
	
	public VideoData() {
		
	}
	
	public void setData(byte[] data) {
		videoData = new byte[data.length];
		System.arraycopy(data, 0, videoData, 0, data.length);
	}

}
