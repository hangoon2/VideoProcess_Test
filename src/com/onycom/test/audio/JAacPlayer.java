package com.onycom.test.audio;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;

public class JAacPlayer extends Thread {
	
	private BlockingQueue<byte[]> audioQue = null;
	
	private InputStream audioIn = null;
	private VDPipedInputStream pin = null;
	private VDPipedOutputStream pout = null;
	
	public boolean running = true;
	
	public JAacPlayer(BlockingQueue<byte[]> audioQue) {
		// TODO Auto-generated constructor stub
		this.audioQue = audioQue;
		
		pin = new VDPipedInputStream(1280);
		try {
			pout = new VDPipedOutputStream(pin);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		audioIn = new DataInputStream(pin);
	}
	
	public void doStop() {
		running = false;
	}
	
	private static boolean formatChanged(AudioFormat af, SampleBuffer buf) {
		return af.getSampleRate() != buf.getSampleRate()
				|| af.getChannels() != buf.getChannels()
				|| af.getSampleSizeInBits() != buf.getBitsPerSample()
				|| af.isBigEndian() != buf.isBigEndian();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean buffering = true;
		
		final SampleBuffer buf = new SampleBuffer();
		SourceDataLine line = null;
		ADTSDemultiplexer adts = null;
		AudioFormat aufmt = null;
		Decoder dec = null;
		int bufferCount = 1;
		
		try {
			while(running) {
				if(buffering && this.audioQue.getCount() < bufferCount) {
					Thread.sleep(1);
					continue;
				}
				
				buffering = false;
				
				byte[] bbs = this.audioQue.dequeue();
				if(bbs != null && bbs.length > 0) {
					if(aufmt == null) {
						aufmt = new AudioFormat(44100, 16, 2, true, false);
					}
					
					if(line == null) {
						line = AudioSystem.getSourceDataLine(aufmt);
						line.open();
						line.start();
					}
					
					line.write(bbs, 0, bbs.length);
					
					
//					baos.write(bbs);
//					baos.flush();
//					
//					this.pout.write(baos.toByteArray());
//					this.pout.flush();
//					baos.reset();
//					
//					if(adts == null) {
//						adts = new ADTSDemultiplexer(this.audioIn);
//						dec = new Decoder(adts.getDecoderSpecificInfo());
//					}
//					
//					byte[] aacFrame = null;
//					try {
//						aacFrame = adts.readNextFrame();
//					} catch(IOException e) {
//						e.printStackTrace();
//					}
//					
//					if(aacFrame != null && aacFrame.length > 0) {
//						dec.decodeFrame(aacFrame, buf);
//						
//						if(aufmt == null) {
//							aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
//							
//							if(buf.getSampleRate() == 8000) {
//								System.out.println("Audio Type Oyto");
//							} else {
//								System.out.println("Audio Type Envi Logic");
//							}
//						}
//						
//						if(line != null && formatChanged(line.getFormat(), buf)) {
//							line.stop();
//							line.close();
//							line = null;
//							
//							aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
//							
//							if(buf.getSampleRate() == 8000) {
//								System.out.println("Audio Type Oyto");
//							} else {
//								System.out.println("Audio Type Envi Logic");
//							}
//						}
//						
//						try {
//							if(line == null) {
//								line = AudioSystem.getSourceDataLine(aufmt);
//								line.open();
//								line.start();
//							}
//							
//							byte[] decodedFrame = buf.getData();
//							line.write(decodedFrame, 0, decodedFrame.length);
//						} catch(IllegalArgumentException e) {
//							e.printStackTrace();
//						} catch(LineUnavailableException e) {
//							e.printStackTrace();
//						}
//					} // end of if
				}
				
				if(this.audioQue.getCount() == 0) {
					buffering = true;
				} else {
					int audioQueCount = this.audioQue.getCount();
					
					while(audioQueCount > 1) {
						try {
							--audioQueCount;
							this.audioQue.dequeue();
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} // end of while
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(line != null) {
				line.stop();
				line.close();
			}
		}
		
		while(this.audioQue.getCount() > 0) {
			try {
				this.audioQue.dequeue();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
