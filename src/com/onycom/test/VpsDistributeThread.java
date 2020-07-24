/*
 * Copyright Onycom. All Rights Reserved. 
 * 
 * This software is the proprietary information of Onycom. 
 * Use is subject to license terms. 
 */

package com.onycom.test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VpsDistributeThread {

	private LinkProtocol protocol = null;
	private List<LinkPacket> responseList = Collections.synchronizedList(new ArrayList<LinkPacket>());
	private Thread thread = null;
	private boolean bThread = false;
	private long vpsAckTime = 0;
	private Timer vpsAckTimer = null;
	private final long VPS_ACK_INTERVAL = 30000;
	private boolean vpsAck = false; 	
	
	
	private OutputStream writer = null;
	
	public VpsDistributeThread(OutputStream writer) {
		this.writer = writer;
	}
	
	private final byte[] makeOnyPacketStartDevice(int nHpNo, boolean start) {
		try {
			String videoType = "usb";
			int size = (4 + videoType.getBytes().length);
			ByteBuffer ret = ByteBuffer.allocate(8 + size + 3);
			
			// start flag
			ret.put(VPSTest.START_FLAG);
			// data size
			ret.putInt(size);
			// command code
			ret.putShort(start ? (short)1 : (short)2);
			// device no
			ret.put((byte)nHpNo);
			
			ret.putShort((short)540);
			ret.putShort((short)960);
			ret.put(videoType.getBytes());
			
			long sum=0;
			ret.position(1);
			//sum short values to data
			while (ret.position() < ret.capacity() -4)
				sum += (long)ret.getShort();
			//sum last byte
			if (ret.position() != ret.capacity() -3)
				sum += (long)(ret.get() & 0xff);
			
			sum = (sum >> 16) + (sum & 0xffff);
			sum += (sum >> 16);
	
			//checksum
			ret.putShort((short) ~sum);
			//end flag
			ret.put(VPSTest.END_FLAG);
			
			return ret.array();
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	
	public void localStart() {
		stop();
		
		responseList.clear();
		
		protocol = new LinkProtocol();
		protocol.start();		
	
			thread = new Thread(new Runnable() {
				
				@Override
				public void run() {		
					bThread = true;
									
					while (bThread) {
						//sleep 1ms
						try {
							Thread.sleep(1);//throw, exit
						} catch (InterruptedException e) {
							break;
						}
						
						//get packet
						final LinkPacket p = protocol.getPacket();
						//check null
						if (p == null)
							continue;	
						
						System.out.println("Receive Data From VPS : " + p.code);
						
						switch (p.code) {						
							//response
						case LinkProtocol.PACKET_FILE:
							responseList.add(p);
							break;
							
						default:
							break;
						}//switch : command code						
					}//while : interrupt
				}//run
			});//thread
			
			thread.start();
	}//start

	public void remoteStart() {
		stop();
		
		responseList.clear();
		
		protocol = new LinkProtocol();
		protocol.start();		
			
		//vps-ack timer
		vpsAckTimer = new Timer();
		vpsAckTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//check vps time
				if (System.currentTimeMillis() - vpsAckTime > VPS_ACK_INTERVAL) {						
					//check VPS-state
					if (vpsAck) {
						vpsAck = false;
						try {
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}

				}
			}}, VPS_ACK_INTERVAL, VPS_ACK_INTERVAL);
		
		thread = new Thread(new Runnable() {
	
			@Override
			public void run() {		
				vpsAck = true;
				try {
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}

				bThread = true;
								
				while (bThread) {
					//sleep 1ms
					try {
						Thread.sleep(1);//throw, exit
					} catch (InterruptedException e) {
						break;
					}
					
					//get packet
					final LinkPacket p = protocol.getPacket();
					//check null
					if (p == null)
						continue;	
					
					switch (p.code) {						
					case LinkProtocol.PACKET_CLEAR:
						final int devNo = p.deviceNo;
						
						new Thread() {
							@Override
							public void run() {								
								// TODO Auto-generated method stub
								//make response
								final byte [] temp = LinkProtocol.makeResponseMessage(devNo, LinkProtocol.PACKET_CLEAR, (byte) 0, "Success");
								//send response
//								VpsConnector.getDefault().sendMessage(temp.length, temp);
							}}.start();
							//thread							
						break;
						//start log
					case LinkProtocol.PACKET_START_LOG:
						break;
						
					case LinkProtocol.PACKET_STOP_LOG:
						break;
						
					//start monitor resource
					case LinkProtocol.PACKET_MONITOR_RESOURCE:
						break;
						
						//stop monitor resource
					case LinkProtocol.PACKET_STOP_MONITOR_RESOURCE:
						break;
						
					case LinkProtocol.PACKET_VPS_ACK:
						break;
							
					case LinkProtocol.PACKET_VPS_IMAGE:
						break;

					case LinkProtocol.PACKET_VPS_POWER:
						break;

					case LinkProtocol.PACKET_TOUCH_DOWN:
					case LinkProtocol.PACKET_TOUCH_UP:
					case LinkProtocol.PACKET_TOUCH_MOVE:
					{
						int x = (int)ByteBuffer.wrap(p.data, 0, 2).getShort();
						int y = (int)ByteBuffer.wrap(p.data, 2, 2).getShort();
						
						System.out.println( String.format("[%d] - %d : %d, %d", p.deviceNo, p.code, x, y) );
					}
						break;
				
					case LinkProtocol.PACKET_FILE:
						String strData = new String( p.data );
	     				System.out.println("Record Success : " + strData);
						break;
						
					case LinkProtocol.PACKET_STOP_SCREEN_MONITOR:
						break;
						
					case 1007:
						System.out.println("Recording Stop");
						break;
						
					case 32401:
					{
						short errCode = 0;
						if(p.dataSize == 2) {
							errCode = ByteBuffer.wrap(p.data).getShort();
						}
							
						System.out.println("The socket is abnormally closed - " + errCode);
						
						try {
							Thread.sleep(20000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
//						System.out.println("Device Restart");
//						VPSTest.startDevice(1);
//						ByteBuffer packet = ByteBuffer.wrap( makeOnyPacketStartDevice(1, true) );
//						try {
//							writer.write(packet.array());
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					}	
						break;
						
					default:
						break;
					}//switch : command code						
				}//while : interrupt
			}});
		
		thread.start();
	}//start
	
	public void addData(final byte [] data) {
		//check null
		if (protocol != null)
			//add data
			protocol.putRawData(data);
	}
	
//	public void addPacket(LinkPacket packet) {
//		protocol.putPacket(packet);
//	}
	
	public LinkPacket getResponse(int deviceNo, int code, long fromTime, long timeout) {
		
		LinkPacket ret = null;
		
		while (System.currentTimeMillis()-fromTime < timeout) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return null;
			}
			//try - catch 
			
			synchronized (responseList) {
				for (int i=0; i< responseList.size(); ++i) {
					ret = responseList.get(i);
					
					if (ret.deviceNo == deviceNo && ret.code == code) {
						if (ret.time > fromTime) {
							responseList.remove(i);
							return ret;
						}
						//if : check time
					}
					//if : check code						
				}
				//for : response list				
			}
			//sync : response list
		}
		//while : timeout
		
		return null;
	}
	//getResponse
	
	public void stop() {
		if (vpsAckTimer != null) {
			//kill timer
			vpsAckTimer.cancel();
			vpsAckTimer = null;
		}
		
		if (thread != null) {
			bThread = false;
			thread.interrupt();		
			
			while (thread.isAlive()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
			
			thread = null;
		}
			 
		if (protocol != null) {
			protocol.stop();
			protocol = null;
		}
	}//stop
	
}
