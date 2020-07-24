package com.onycom.test.threads;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import com.onycom.test.IVPSCommon;
import com.onycom.test.TFDisplayView;
import com.onycom.test.TFSubDisplayView;
import com.onycom.test.models.MediaDataQueue;
import com.onycom.test.models.VideoData;

public class ReceiveThread extends Thread{
    private BufferedInputStream bis = null;
    public  MediaDataQueue recvQue = null;
    public boolean Running = true;
    private int command = 0;
    private Rectangle rectVideo = null;
    private TFDisplayView imageView = null;
//    private TFSubDisplayView subImageView = null;

	private boolean isReconnected = false;
	
	private boolean isKeyFrame = false;
	int count = 0;
	
	private ITFRecordStopListener m_recordStopListener;
	
	public interface ITFRecordStopListener {
		
		abstract void onRecordStop();
		
	}
	
	public ReceiveThread(BufferedInputStream byteIS, MediaDataQueue recvQue, TFDisplayView imageView, TFSubDisplayView subImageView) {
		this.recvQue = recvQue;
		this.bis = byteIS;
		this.Running = true;
		this.rectVideo = new Rectangle();
		this.imageView = imageView;
//		this.subImageView = subImageView;
	}
	
	public void setRecordStopListener(ITFRecordStopListener listener) {
		m_recordStopListener = listener;
	}

	public void setReconnection(BufferedInputStream bis) {
		if (bis == null) {
			this.isReconnected = false;
		} else {
			this.isReconnected = true;
		}
		this.bis = bis;
    }

    

    public void doStop() 
	{ 
    	Running = false; 
	}
    
    public enum PacketPos {
		RX_PACKET_POS_START,
		RX_PACKET_POS_HEAD,
		RX_PACKET_POS_DATA,
		RX_PACKET_POS_TAIL;
	}
    
    public void run() {
    	long tickCount = System.currentTimeMillis();  
     	byte[] mediaData = null;
     	boolean firstFrame = false;
     	final int frameTimeout = 20000; 
     	
     	int type = 1;
     	
     	if(type == 1) {     	
	     	int packetPos = 0, readPos = 0, write = 0, dataSize = 0, cmd = 0;
	     	PacketPos rxStreamOrder = PacketPos.RX_PACKET_POS_START;
	     	byte [] head = new byte [8];
			byte [] tail = new byte [3];
			ByteBuffer pRcvBuf = null;
	     	
	     	while (Running) {
	     		
	     		try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					break;
				}
				
				int ret;
				try {
					ret = bis.available();
					if(ret < 1) {
						continue;
					}
					
					byte[] buffer = new byte[ret];
					bis.read(buffer);
					
					ByteBuffer msg = ByteBuffer.wrap(buffer);
					
					readPos = 0;
					
					while( readPos < msg.capacity() ) {
						if(rxStreamOrder == PacketPos.RX_PACKET_POS_START) {
							for(; readPos < msg.capacity(); ++ readPos) {
								if(msg.get(readPos) == 0x7f) {
									rxStreamOrder = PacketPos.RX_PACKET_POS_HEAD;
									packetPos = 0;
									break;
								}
							}
						}
						
						// head
						if(rxStreamOrder == PacketPos.RX_PACKET_POS_HEAD) {
							write = Math.min( 8-packetPos, msg.capacity()-readPos );
							
							System.arraycopy( msg.array(), readPos, head, packetPos, write );
							
							packetPos += write;
							readPos += write;
							
							if(packetPos == 8) {
								cmd = ByteBuffer.wrap(head, 5, 2).getShort();
								dataSize = ByteBuffer.wrap(head, 1, 4).getInt();
								
								pRcvBuf = ByteBuffer.allocate(8 + dataSize + 3);
								
								pRcvBuf.put(head);
								
								rxStreamOrder = PacketPos.RX_PACKET_POS_DATA;
								packetPos = 0;
							}
						}
						
						// data
						if(rxStreamOrder == PacketPos.RX_PACKET_POS_DATA) {
							write = Math.min( dataSize-packetPos, msg.capacity()-readPos );
							
							System.arraycopy( msg.array(), readPos, pRcvBuf.array(), 8 + packetPos, write );
							
							packetPos += write;
							readPos += write;
							
							if(packetPos == dataSize) {
								rxStreamOrder = PacketPos.RX_PACKET_POS_TAIL;
								packetPos = 0;
							}
						}
						
						// tail
						if(rxStreamOrder == PacketPos.RX_PACKET_POS_TAIL) {
							write = Math.min( 3-packetPos, msg.capacity()-readPos );
							
							System.arraycopy( msg.array(), readPos, tail, packetPos, write );
							
							packetPos += write;
							readPos += write;
							
							if(packetPos == 3) {
								pRcvBuf.put(tail);
								
					     			switch(cmd) {
					     			case IVPSCommon.CMD_STOP_RECORDING:
					     				m_recordStopListener.onRecordStop();
					     				break;
					     				
					     			case IVPSCommon.CMD_CMD_ACK: 
					     				break;
					     				
					     			case IVPSCommon.CMD_CMD_LOGCAT: 
						     			break;
						     			
					     			case IVPSCommon.CMD_GUEST_UPDATED:
					     				System.out.println("Guest Updated");
					     				break;
					     				
					     			case 32000:
	//				     				System.out.println("VPS HearBeat");
					     				break;
					     				
					     			case 20014:
					     			case 20015:
					     			{
					     				VideoData video = new VideoData();
										
										byte[] pMirroringPacket = pRcvBuf.array();
										
										rectVideo.x = ByteBuffer.wrap(pMirroringPacket, 16, 2).getShort();
										rectVideo.y = ByteBuffer.wrap(pMirroringPacket, 18, 2).getShort();
										rectVideo.width = ByteBuffer.wrap(pMirroringPacket, 20, 2).getShort();
										rectVideo.height = ByteBuffer.wrap(pMirroringPacket, 22, 2).getShort();
										video.isKeyFrame = ByteBuffer.wrap(pMirroringPacket, 24, 1).get() == 1 ? true : false;
										
										int size = ByteBuffer.wrap(pMirroringPacket, 1, 4).getInt() - 17;
										byte[] imgData = new byte[size];
										System.arraycopy(pMirroringPacket, 25, imgData, 0, size);
										
										video.rc.setBounds(rectVideo);
										video.setData(imgData);
										
//										if(subImageView != null) {
//											subImageView.setVideoData(video);
//										}
					     			}
					     			break;
					     			
					     			case 20004:	
					     			case 20005:
					     			case 20006:
					     			case 20007:
					     			{
					     				VideoData video = new VideoData();
										
										byte[] pMirroringPacket = pRcvBuf.array();
										
										rectVideo.x = ByteBuffer.wrap(pMirroringPacket, 16, 2).getShort();
										rectVideo.y = ByteBuffer.wrap(pMirroringPacket, 18, 2).getShort();
										rectVideo.width = ByteBuffer.wrap(pMirroringPacket, 20, 2).getShort();
										rectVideo.height = ByteBuffer.wrap(pMirroringPacket, 22, 2).getShort();
										video.isKeyFrame = ByteBuffer.wrap(pMirroringPacket, 24, 1).get() == 1 ? true : false;
										
										int size = ByteBuffer.wrap(pMirroringPacket, 1, 4).getInt() - 17;
										byte[] imgData = new byte[size];
										System.arraycopy(pMirroringPacket, 25, imgData, 0, size);
										
										video.rc.setBounds(rectVideo);
										video.setData(imgData);
										
										imageView.setVideoData(video);
					     			}
					     			break;
					     			
					     			default:
					     				System.out.println("Received Unknown Packet : " + cmd);
					     				break;
					     		}
								
								rxStreamOrder = PacketPos.RX_PACKET_POS_START;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
	     	}
     	}
	     	
	    else {     		
	     	while (Running) {
	     		
	     		mediaData = parseDataFormServer_FixedHeader();
	     		
	     		if(mediaData != null) {     			
	     			switch(command) {
	     			case IVPSCommon.CMD_STOP_RECORDING:
	     				break;
	     				
	     			case IVPSCommon.CMD_CMD_ACK: 
	     				break;
	     				
	     			case IVPSCommon.CMD_CMD_LOGCAT: 
		     			break;
		     			
	     			case IVPSCommon.CMD_GUEST_UPDATED:
	     				break;
	     			
	     			case 20002:
	     			case 20004:	
	     			case 20005:
	     			case 20006:
	     			case 20007:
	     				if(command == 20004){
	     				}
	     				else if(command == 20005){
	     				}
	     				else if(command == 20006){
	     				}
	     				else if(command == 20007){
	     				}
	     				VideoData video = new VideoData();
						video.rc.setBounds(rectVideo);
						video.setData(mediaData);
						video.isKeyFrame = isKeyFrame;
						imageView.setVideoData(video);
		     			break;
	     			}
	     			
	     			try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						Running = false;
						e.printStackTrace();
					}
	         		continue;
	     		}
	     	}
	     }
    }
    
    @SuppressWarnings("unused")
	public byte[] parseDataFormServer_FixedHeader(){
    	byte[] mediaData = null;

    	boolean isSocketExceptionOccured = false;
     	int read = 0, sizeData = 0, sizeMediaData = 0, sizeSkip = 0;
     	int sizeSubHeadVideo = 17, sizeSubHeadAudio = 8; 
     	byte[] readByte = new byte[1];
     	byte[] readHeader = new byte[7];
     	ByteBuffer bb2Byte = ByteBuffer.wrap(new byte[2]);
     	ByteBuffer bb4Byte = ByteBuffer.wrap(new byte[4]);
     	byte deviceNo = 0;
     	
     	if (bis == null) {
     		return null;
     	}
     	
    	try{
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
         			
         			switch(command) {
         			case IVPSCommon.CMD_RESOURCE_USAGE_NETWORK:
         			case IVPSCommon.CMD_RESOURCE_USAGE_CPU:
         			case IVPSCommon.CMD_RESOURCE_USAGE_MEMORY:
         				sizeMediaData = sizeData;
         				sizeSkip = 0;
         				break;
         				
         			case IVPSCommon.CMD_STOP_RECORDING:
         				sizeMediaData = sizeData;
         				sizeSkip = 0;
         				mediaData = new byte[sizeMediaData];
         				break;
         				
         			case IVPSCommon.CMD_CMD_ACK:
         				sizeMediaData = sizeData - 3; 
         				sizeSkip = 3;
         				break;
         				
         			case IVPSCommon.CMD_CMD_LOGCAT:
         				sizeMediaData = sizeData;
         				sizeSkip = 0;
         				break;
         				
         			case IVPSCommon.CMD_AUDIO_AAC_DATA:
         				sizeMediaData = sizeData - sizeSubHeadAudio;
         				sizeSkip = sizeSubHeadAudio;
         				break;
         				
         			case 20002:
         			case 20004:
         			case 20005:
         			case 20006:
         			case 20007:
         				sizeMediaData = sizeData - sizeSubHeadVideo;
         				sizeSkip = sizeSubHeadVideo;
         				break;
         				
         			case IVPSCommon.CMD_DEVICE_DISCONNECTED:
         				sizeMediaData = sizeData;
         				sizeSkip = 0;
         				mediaData = new byte[sizeMediaData];
         				break;
         				
//         			case IVPSCommon.VPS_DISCONNECT_GUEST:
//         				sizeMediaData = sizeData;
//         				sizeSkip = 0;
//         				break;
         				
         			case IVPSCommon.CMD_GUEST_UPDATED:
         				System.out.println("Guest Updated");
         				sizeMediaData = sizeData;
         				sizeSkip = 0;
         				mediaData = new byte[sizeMediaData];
         				break;
         				
         			case IVPSCommon.CMD_DUPLICATED_CLIENT:
         				System.out.println("Duplicated Client");
         				break;
         				
//         			case IVPSCommon.CMD_JPEG_CAPTURE_FAILED:
//         				sizeMediaData = sizeData;
//         				sizeSkip = 0;
//         				break;
         				
         			case IVPSCommon.CMD_VPS_VERSION:
         				sizeMediaData = sizeData;
         				sizeSkip = 0;
         				break;
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
         			if(command == 20004 || command == 20005
         					|| command == 20006 || command == 20007 || command == 20002){
         				bb2Byte.clear();
             			bb2Byte.put(tempBuffer, 8, bb2Byte.limit());
             			rectVideo.x = bb2Byte.getShort(0);
             			
             			bb2Byte.clear();
             			bb2Byte.put(tempBuffer, 10, bb2Byte.limit());
             			rectVideo.y = bb2Byte.getShort(0);
             			
             			bb2Byte.clear();
             			bb2Byte.put(tempBuffer, 12, bb2Byte.limit());
             			rectVideo.width = bb2Byte.getShort(0) - rectVideo.x;
             			
             			bb2Byte.clear();
             			bb2Byte.put(tempBuffer, 14, bb2Byte.limit());
						rectVideo.height = bb2Byte.getShort(0) - rectVideo.y;
						
						isKeyFrame = tempBuffer[16] == 1 ? true : false;
         			}
         			else if(command == IVPSCommon.CMD_CMD_ACK) {
         				bb2Byte.clear();
             			bb2Byte.put(tempBuffer, 0, bb2Byte.limit());
             			
             			bb2Byte.clear();
         			}
         			
         			System.arraycopy(tempBuffer, sizeSkip, mediaData, 0, sizeMediaData);
         		}
            }
         	         	
         	while ((read = bis.read(readByte)) != -1) {
         		
         		if((readByte[0] & 0xFF) == 0xEF)
         		{
         			break;
         		}
         	}

			if (command != IVPSCommon.CMD_DUPLICATED_CLIENT) {
				return mediaData;
			}
        }
    	catch (SocketTimeoutException e) {
        }
    	catch (SocketException e) {
    		isSocketExceptionOccured = true;
    	}
    	catch(Exception e){
        }

     	if (isSocketExceptionOccured || read == -1) {
     		if (isReconnected) {
     			isReconnected = false;
     			return null;
			} else {
				doStop();
			}
     	}

		return null;
    }
   
 }
