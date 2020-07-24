package com.onycom.test.audio;

public class AudioManager {
	
	private BlockingQueue<byte[]> audioQue = new BlockingQueue<byte[]>();
	
	private JAacPlayer audioPlayerThread = null;
	
	public AudioManager() {
		// TODO Auto-generated constructor stub
		audioPlayerThread = new JAacPlayer(audioQue);
		audioPlayerThread.setPriority(Thread.MAX_PRIORITY);
		audioPlayerThread.start();
	}
	
	public void enQueue(byte[] data) {
		audioQue.enqueue(data);
	}
	
	public void doStop() {
		if(audioPlayerThread != null) {
			audioPlayerThread.doStop();
			audioPlayerThread = null;
		}
	}

}
