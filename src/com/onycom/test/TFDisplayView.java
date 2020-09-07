package com.onycom.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.DataOutputStream;

import javax.swing.JPanel;

import com.onycom.test.models.BlockingQueue;
import com.onycom.test.models.VideoData;

@SuppressWarnings("serial")
public class TFDisplayView extends JPanel implements Runnable {
	
	private TFMirroringImageView imageView = null;
	
	BlockingQueue<VideoData> videoQue = null;
	boolean running = false;
	
	public TFDisplayView(final int nHpNo) {
		// TODO Auto-generated constructor stub
		setLayout( new BorderLayout() );
		setBackground(Color.black);
		
		imageView = new TFMirroringImageView(nHpNo, false, 1440, 2960);
		
		add(imageView, BorderLayout.CENTER);
		
		videoQue = new BlockingQueue<VideoData>();
		
		running = true;
		new Thread(this).start();
	}
	
	public JPanel getImageView() {
		return imageView;
	}
	
	public void setVideoData(VideoData data) {
		videoQue.enqueue(data);
	}
	
	public void stopMirroring() {
		imageView.stopMirroring();
	}
	
	public void setDataOutputStream(DataOutputStream bos) {
		imageView.setDataOutputStream(bos);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(running) {
			try {
				VideoData data = videoQue.dequeue();
				
				imageView.setVideoData(data);
				
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
