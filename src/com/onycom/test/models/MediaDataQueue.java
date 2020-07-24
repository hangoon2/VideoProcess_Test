package com.onycom.test.models;

public class MediaDataQueue {

	public BlockingQueue<VideoData> videoQue = null;
	 public BlockingQueue<byte[]> audioQue = null;
	 public BlockingQueue<byte[]> logCatQue = null;
	 
	 public MediaDataQueue(){
		 videoQue = new BlockingQueue<VideoData>();
	     audioQue = new BlockingQueue<byte[]>();
	     logCatQue = new BlockingQueue<byte[]>();
	 }
	 
}
