/*
 * Copyright Onycom. All Rights Reserved. 
 * 
 * This software is the proprietary information of Onycom. 
 * Use is subject to license terms. 
 */

/**
 * @file
 * Audio 梨꾨꼸�쓣 �넻�빐 Audio 硫붿떆吏�瑜� 諛쏄퀬, �뙆�떛 �썑, Audio Buffer 濡� 異붽�
 * 
 * Video 梨꾨꼸怨쇰뒗 �떎瑜� 蹂꾨룄�쓽 Audio 梨꾨꼸�쓣 �넻�빐 Audio 硫붿떆吏�瑜� 諛쏄퀬, �뙆�떛 �썑, Audio Queue 濡� Enqueue �븳�떎.\n
 * Audio �옱�떛�� JAacPlayer �겢�옒�뒪�뿉�꽌 �븳�떎.
 *  
 * @author ByeongEop Son (bson@onycom.com)
 * @date 2014-09-23
 * @copyright Copyright (c) 2014 Onycom, Inc. All rights reserved.
 */

package com.onycom.test.threads;

import java.io.BufferedInputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import com.onycom.test.audio.AudioManager;
import com.onycom.test.models.BlockingQueue;

/**
 * Receive form VPS interface
 * 
 * @author bson
 */
public class AudioReceiveThread extends Thread{
    private BufferedInputStream bis = null;
    private AudioManager audioManager = null;
    public boolean Running = true;
    private int command = 0;

	/**
	 * Constructor for AudioReceiveThread.
	 * 
	 * @param byteIS
	 *            BufferedInputStream
	 * @param audioQue
	 *            BlockingQueue<byte[]>
	 */
    public AudioReceiveThread(BufferedInputStream byteIS, AudioManager audioManager){
		this.audioManager = audioManager;
		this.bis = byteIS;
		this.Running = true;
    }

	/**
	 * Parse data form VPS �뙆�떛�맂 �뜲�씠�꽣瑜� �궡遺� �걧�뿉 �떞�뒗�떎.
	 * 
	 * @return byte[]
	 */
    @SuppressWarnings("unused")
	public byte[] parseDataFormServer_FixedHeader(){
    	try{
           	byte[] mediaData = null;
         	
         	int read = 0, sizeData = 0, sizeMediaData = 0, sizeSkip = 0;
         	int sizeSubHeadAudio = 8;
         	byte[] readByte = new byte[1];
         	byte[] readHeader = new byte[7];
         	ByteBuffer bb2Byte = ByteBuffer.wrap(new byte[2]);
         	ByteBuffer bb4Byte = ByteBuffer.wrap(new byte[4]);
         	byte deviceNo = 0;

         	while ((read = bis.read(readByte)) != -1) {
         		

         		if(readByte[0] == 0x7F)
         		{
         			if((read = bis.read(readHeader, 0, readHeader.length)) == -1){
         				break;
         			}
         			bb4Byte.clear();
         			bb4Byte.put(readHeader, 0, bb4Byte.limit());
         			
         			sizeData = bb4Byte.getInt(0);
         			
         			bb2Byte.clear();
         			bb2Byte.put(readHeader, 4, bb2Byte.limit());
         			
         			command = bb2Byte.getShort(0);
         			
         			deviceNo = readHeader[readHeader.length-1];

         			if(command == 20013){ // CMD_AUDIO_LITTLE_DATA
         				sizeMediaData = sizeData - sizeSubHeadAudio;
         				sizeSkip = sizeSubHeadAudio;
         			}

         			break;
         		}
         	}
         	         	
         	if(sizeData > 0 && sizeData < 1024*1024){
         		boolean readOK = true;
 	        	int readTotalLength = 0;	
 	        	mediaData = new byte[sizeMediaData];
         		byte[] readBuffer = new byte[sizeData];
         		byte[] tempBuffer = new byte[sizeData];
         		
         		while (sizeData > 0) {
         			if((read = bis.read(readBuffer, 0, sizeData)) == -1){
         				readOK = false;
         				break;
         			}

         			System.arraycopy(readBuffer, 0, tempBuffer, readTotalLength, read);
	
         			readTotalLength += read;
         			sizeData -= read;	
         		}
         		
         		if(readOK){
         			System.arraycopy(tempBuffer, sizeSkip, mediaData, 0, sizeMediaData);
         		}
            }
         	/**
         	 * calculate checksum coding skip
         	 * 
         	 */
         	
         	while ((read = bis.read(readByte)) != -1) {
         		
         		if((readByte[0] & 0xFF) == 0xEF)
         		{
         			//System.out.println("Header End : " + header);
         			break;
         		}
         	}

         	if (read == -1) {
         		doStop();
         	}

         	return mediaData;
        }
    	catch (SocketTimeoutException e) {
            // timeout exception.
    		System.out.println("AudioReceiverThread[parse] : " + e.getMessage());
        }
    	catch(Exception e){
    		System.out.println("AudioReceiverThread[parse] : " + e.getMessage());
        }
    	
    	System.out.println("AudioReceiverThread[parse] : media data null");
		return null;
    }

    /**
	 * Method doStop.
	 * 
	 */
    public void doStop() 
	{ 
	    // will cause thread to stop looping 
    	Running = false; 
	}

    /**
	 * Method run.
	 * 
	 * @see java.lang.Runnable#run()
	 */
    public void run(){
     	byte[] mediaData = null;

     	while (Running) {
     		mediaData = parseDataFormServer_FixedHeader();
     		
     		if(mediaData != null){
     			
	     			switch(command){
	     			case 20013: // CMD_AUDIO_LITTLE_DATA

	     				//System.out.println("Audio Data : " + mediaData.length);
	     				audioManager.enQueue(mediaData);
	     				//System.out.println("recvQue.audioQue.enqueue");

	     				try {
	     					// JAacPlayer �뒪�젅�뱶媛� �옱�깮�븷 �닔 �엳�룄濡� sleep.
							Thread.sleep(0);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

	     				break;
	     			}	
	     			
     		}else{

     			try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("AudioReceiverThread[run] : " + e.getMessage());
					Running = false;
					e.printStackTrace();
				}

     		}
     	}

     	System.out.println("AudioReceiverThread : Stopped");
    }
    
 }