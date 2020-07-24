/*
 * Copyright Onycom. All Rights Reserved. 
 * 
 * This software is the proprietary information of Onycom. 
 * Use is subject to license terms. 
 */

/**

 * @file
 * define communication protocol.
 *   
 * @author KMG (bigsleep@onycom.com)
 * @date 2013-11-12
 * @version 1.0.0
 */

package com.onycom.test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * communication protocol.
 * 
 */
public class LinkProtocol {	
				
	/**
	 * order of packet content.
	 * 
	 */	 
	private enum PacketPos {
		RX_PACKET_POS_START,
		RX_PACKET_POS_HEAD,
		RX_PACKET_POS_DATA,
		RX_PACKET_POS_TAIL;
	}
	
	public final static byte RESPONSE_RESULT_SUCCESS = 0;
	public final static byte RESPONSE_RESULT_FAIL = 1;
	
	///command code-start
	public final static short PACKET_START = 1;
	///command code-stop
	public final static short PACKET_STOP = 2;
	///command code-request connection status
	public final static short PACKET_REQ_STATUS = 3;
	///command code-input text
	public final static short PACKET_TEXT = 4;
	///command code-start logcat
	public final static short PACKET_START_LOG = 5;
	///command code-install apk
	public final static short PACKET_INSTALL = 6;
	///command code-stop logcat
	public final static short PACKET_STOP_LOG = 7;
	///command code-wake up device
	public final static short PACKET_UNLOCK = 8;
	///command code-reboot device
	public final static short PACKET_REBOOT = 9;
	///command code-press hard-keypad
	public final static short PACKET_KEY = 10;
	///command code-request logcat data
	public final static short PACKET_REQ_LOG = 11;
	///command code-open URL
	public final static short PACKET_URL = 12;
	///command code-initialize device
	public final static short PACKET_CLEAR = 13;
	
	public final static short PACKET_MONITOR_RESOURCE = 14;	
	public final static short PACKET_STOP_MONITOR_RESOURCE = 15;	
	public final static short PACKET_RESOURCE_NETWORK = 16;
	public final static short PACKET_RESOURCE_CPU = 17;	
	public final static short PACKET_RESOURCE_MEMORY = 18;	
	///command code-press keypad
	public final static short PACKET_REMOTEKEY = 19;	
	///command code-device setting
	public final static short PACKET_SETTING = 20;	
	///command code-request new package list
	public final static short PACKET_NEW_PACKAGES = 21;	
	public final static short PACKET_REQUEST_RESOURCE = 22;	
	public final static short PACKET_MIRACAST = 23;
	///command code
	public final static short PACKET_LOCK_SETTING = 24;
	
		
	///command code-tap screen
	public final static short PACKET_TAP = 51;
	///command code-touch down
	public final static short PACKET_TOUCH_DOWN = 52;
	///command code-touch up
	public final static short PACKET_TOUCH_UP = 53;
	///command code-drag screen
	public final static short PACKET_TOUCH_MOVE = 54;
	///command code-swipe screen
	public final static short PACKET_SWIPE = 55;
	///command code-swipe screen
	public final static short PACKET_MULTI_DOWN = 56;
	public final static short PACKET_MULTI_UP = 57;	
	public final static short PACKET_MULTI_MOVE = 58;
	
	public final static short PACKET_BACKUP = 59;
	public final static short PACKET_RESTORE = 60;
	
	public final static short PACKET_AUTO_START_DEVICE = 101;

	public final static short PACKET_RECENT_APP = 300;
	///command code-auto test
	public final static short PACKET_AUTOTEST_START = 301;
	public final static short PACKET_AUTOTEST_STOP = 302;
	public final static short PACKET_AUTOTEST_SELECT = 303;
	public final static short PACKET_AUTOTEST_SEARCH = 304;
	public final static short PACKET_AUTOTEST_DUMP_SCENE = 305;
	public final static short PACKET_AUTOTEST_REQ_APP_LIST = 306;
	public final static short PACKET_AUTOTEST_TEXT = 307;
	public final static short PACKET_AUTOTEST_START_SCRIPT = 308;
	public final static short PACKET_AUTOTEST_RESPONSE = 309;
	public final static short PACKET_AUTOTEST_KEYBOARD = 310;
	public final static short PACKET_AUTOTEST_APP_LIST = 311;
	public final static short PACKET_AUTOTEST_REQ_TOP_APP = 312;
	public final static short PACKET_AUTOTEST_TOP_APP = 313;
	public final static short PACKET_AUTOTEST_REQ_ACTIVITY = 314;
	public final static short PACKET_AUTOTEST_ACTIVITY = 315;
	public final static short PACKET_AUTOTEST_RUN_APP = 316;
	public final static short PACKET_AUTOTEST_OCR = 317;
	
	public final static short PACKET_AUTOTEST_START_EVENT_WITH_NO = 318;
	public final static short PACKET_AUTOTEST_START_EVENT_WITH_PATH = 319;
	public final static short PACKET_AUTOTEST_EVENT_RESULT_WITH_PATH = 320;
	
	public final static short PACKET_AUTOTEST_DISABLE_POPUP = 321;
	public final static short PACKET_AUTOTEST_ENABLE_POPUP = 322;
	
	public final static short PACKET_AUTOTEST_SECURE_ACCOUNT_SET = 328;
	
	//command stand alone
	public final static short PACKET_STANDALONE_REQ_DEVICE_LIST = 401;
	public final static short PACKET_STANDALONE_DEVICE_LIST_DATA = 402;
	public final static short PACKET_STANDALONE_DEVICE_CHANGE = 403;
	public final static short PACKET_STANDALONE_TEST_RESULT = 404;
	public final static short PACKET_STANDALONE_TEST_FINISH = 405;
	
	public final static short PACKET_STANDALONE_DUMP_SCENE = 406;
	public final static short PACKET_STANDALONE_SCENE_FILE = 407;
	public final static short PACKET_STANDALONE_INSTALL = 408;
	
	public final static short PACKET_STANDALONE_ADB_VERSION = 409;
	public final static short PACKET_STANDALONE_ANDROID_HOME = 410;
	public final static short PACKET_STANDALONE_TERMINATE = 411;
	
	public final static short PACKET_STANDALONE_VPS_CLOSE = 32761;
	
	///command code - rotate screen to portrait
	public final static short PACKET_PORTRAIT = 1003;
	///command code - rotate screen to landscape
	public final static short PACKET_LANDSCAPE = 1004;
	///command code-video record
	public final static short PACKET_RECORD = 1005;
	///command code-capture screen
	public final static short PACKET_CAPTURE = 1006;
	///command code-switch between USB on/off
	public final static short PACKET_USB_SWITCH = 1009;
	
	///command code-common response
	public final static short PACKET_RESPONSE = 10001;
	///command code-device connection status
	public final static short PACKET_STATUS = 10002;
	///command code-logcat data
	public final static short PACKET_LOG = 10003;
	///command code-file
	public final static short PACKET_FILE = 10004;

	public final static short PACKET_RESOURCE_BOUND = 10005;

	public final static short PACKET_BATTERY = 10006;
			

	public final static short PACKET_START_SCREEN_MONITOR = 22001;
	public final static short PACKET_STOP_SCREEN_MONITOR = 22002;
	///command code - device is disconnected
	public final static short PACKET_DEVICE_DISCONNECTED = 22003;
					
	///command code-send Device Controller's ID to VPS
	public final static short PACKET_CONNECT_VPS = 30000;
	///command code - control converter
	public final static short PACKET_CONVERTER = 30006;
	///command code - VPS ack
	public final static short PACKET_VPS_ACK = 32000;	
	//
	public final static short PACKET_VPS_IMAGE = 32001;	
	// 
	public final static short PACKET_VPS_POWER = 32002;	
	
	///head size of packet
	public static final int PACKET_HEAD_SIZE = 8;
	
	///start flag of packet
	static final byte START_FLAG = 0x7F;
	///end flag of packet
	static final byte END_FLAG = (byte) 0xEF;
	
	///position of size within packet
	static final int MSG_HEAD_SIZE_POS = 1;
	///position of type within packet
	static final int MSG_HEAD_TYPE_POS = 5;
	///position of number within packet
	static final int MSG_HEAD_NO_POS = 7;

	///device connection status-available
	public static final byte DEVICE_STATE_AVAILABLE = 0;
	///device connection status-offline
	public static final byte DEVICE_STATE_OFFLINE = 1;
	///device connection status-use
	public static final byte DEVICE_STATE_USE = 2;
	///device connection status-undefined
	public static final byte DEVICE_STATE_UNDEFINE = 3;
	///device connection status-monitor
	//public static final byte DEVICE_STATE_MONITOR = 4;
	
	///parsing position of packet 
	private PacketPos rxStreamOrder = PacketPos.RX_PACKET_POS_START;
	///list of data received 
	private List<ByteBuffer> rxAry = Collections.synchronizedList(new ArrayList<ByteBuffer>());//rx packet array
	///list of packet received
	private List<LinkPacket> packets = Collections.synchronizedList(new ArrayList<LinkPacket>());//rx packet array
	//
	private Thread thread = null;
	
	private int fieldPos = 0;
	private LinkPacket curPacket=null;
	private byte [] headBuf = new byte [PACKET_HEAD_SIZE];
	private byte [] trailBuf = new byte [3];

	/**
	 * create module to handle communication data.
	 * 
	 */
	public LinkProtocol () {
	}

	/**
	 * make common response packet.
	 * 
	 * @param deviceNo
	 * 			device number
	 * @param request
	 * 			command code of request
	 * @param status
	 * 			result flag
	 * @param desc
	 * 			result message
	 * @return response packet
	 */
	public static final byte[] makeResponseMessage(int deviceNo, short request, byte status, String desc) {
		byte [] body = encodeResponseBodyPart(request, status, desc);
				
		return encodePacket(deviceNo, PACKET_RESPONSE, body.length, body);
	}//	makeResponseMessage

	public static final byte[] encodeResponseBodyPart(short request, byte result, String message) {		
		try {
			byte [] temp = null;
			if (message != null)
				temp = message.getBytes(Charset.forName("UTF-8"));
			else
				temp = new byte [0];
			
			//request + result + message
			ByteBuffer buf = ByteBuffer.allocate(3 + temp.length);
			buf.putShort(request);
			buf.put(result);
			buf.put(temp);
			
			return buf.array();
		} catch (Exception e) {
			return null;
		}				
	}//	makeResponseMessage
	
	public static String getResponseMessage(LinkPacket responsePacket) {
		if (responsePacket.data.length > 3)
			try {
				return new String (responsePacket.data, 3, responsePacket.data.length -3, Charset.forName("UTF-8"));
			} catch (IndexOutOfBoundsException e) {}
		
		return "";
	}
	
	public static byte getResponseResult(LinkPacket responsePacket) {
		if (responsePacket.data.length >= 3)
			return responsePacket.data[2];
		
		return RESPONSE_RESULT_FAIL;
	}
		
	public static final LinkPacket decodePacket(final byte [] data, int size) {
		//check header size
		if (size < LinkProtocol.PACKET_HEAD_SIZE) 
			return null;
		//check start flag
		if (data[0] != START_FLAG)
			return null;
			
		//create packet
		LinkPacket packet = new LinkPacket();
		//command code
		packet.code = ByteBuffer.wrap(data, MSG_HEAD_TYPE_POS, 2).getShort();
		//device number
		packet.deviceNo = data[MSG_HEAD_NO_POS];
		//data size
		packet.dataSize = ByteBuffer.wrap(data, MSG_HEAD_SIZE_POS, 4).getInt();
		//data buffer 
		packet.data = new byte [packet.dataSize];
		//data
		System.arraycopy(data, LinkProtocol.PACKET_HEAD_SIZE, packet.data, 0, 
				Math.min(packet.dataSize, size-LinkProtocol.PACKET_HEAD_SIZE));
		
		return packet;
	}//decodePacket
	
	public static final byte [] encodePacket(final LinkPacket packet) {
		//head + data + tail(checksum + end)
		ByteBuffer ret = ByteBuffer.allocate(PACKET_HEAD_SIZE + packet.dataSize + 3);
		//start flag
		ret.put(START_FLAG);
		//data size
		ret.putInt(packet.dataSize);
		//command code
		ret.putShort(packet.code);
		//device no
		ret.put(packet.deviceNo);
		//data
		ret.put(packet.data, 0, packet.dataSize);
			
		//checksum
		ret.putShort((short) 0);
		//end flag
		ret.put(END_FLAG);
					
		return ret.array();		
	}
		
	/**
	 * make packet.
	 * 
	 * @param deviceNo
	 * 			device number
	 * @param code
	 * 			command code
	 * @param dataSize
	 * 			data size to send
	 * @param data
	 * @return packet
	 */
	public static final byte[] encodePacket(int deviceNo, short code, int dataSize, final byte [] data) {
		//head + data + tail(checksum + end)
		ByteBuffer ret = ByteBuffer.allocate(PACKET_HEAD_SIZE + dataSize + 3);
		//start flag
		ret.put(START_FLAG);
		//data size
		ret.putInt(dataSize);
		//command code
		ret.putShort(code);
		//device no
		ret.put((byte) deviceNo);
		//data
		ret.put(data, 0, dataSize);
		
		//exclude start flag, tail
/*		long sum=0;
		//start from data size
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
*/
		//checksum
		ret.putShort((short) 0);
		//end flag
		ret.put(END_FLAG);
				
		return ret.array();
	}//	makeResponseMessage	
	
	/**
	 * add data to parsing list.
	 * 
	 * @param data
	 */
	public void putRawData(final byte [] data) {
		rxAry.add(ByteBuffer.wrap(data));
	}
	
	/**
	 * return a parsed packet in list. 
	 * 
	 * @return packet
	 */
	public final LinkPacket getPacket() {
		LinkPacket p = null;
		
		synchronized (packets) {
			//check packet list
			if (!packets.isEmpty()) {
				//get first packet
				p = packets.get(0);
				//delete packet from list
				packets.remove(0);
			}
		}//sync : list
		return p;
	}//getPacket
	
	public List<LinkPacket> getPacketList() {
		return packets;
	}

	/**
	 * return a parsed packet that match code in list. 
	 * 
	 * @param code
	 * 			desired command code
	 * @return packet
	 */
	public final LinkPacket getPacket(short code) {		
		synchronized (packets) {
			//search list
			for (LinkPacket p : packets) {
				//check command code
				if (p.code == code) {
					//delete packet from list
					packets.remove(p);
					//return packet
					return p;
				}
			}//for : search list
		}//sync : list
		return null;
	}//getPacket
	
	public void putPacket(LinkPacket packet) {
		packets.add(packet);
	}
	
	public void insertPacket(int index, LinkPacket packet) {
		packets.add(index, packet);
	}
	
	public void clearPacket() {
		rxAry.clear();
		packets.clear();
	}
	
	public void start() {
		stop();
		
		//data parsing thread
		thread = new Thread(new Runnable() {
	
			@Override
			public void run() {
				//header data
				byte [] head = new byte [PACKET_HEAD_SIZE];
				//footer data
				byte [] tail = new byte [3];
				//part position
				int p=0;
				//size to read
				int iWrite=0;
				//checksum buffer index
				int j=0;
				//data position
				int i=0;
				//checksum
				short checksum = 0;
				//data sum value
				long sum = 0;
				//packet 
				LinkPacket packet = null;
				//data buffer
				ByteBuffer msg = null;
				//checksum buffer
				ByteBuffer tmpbuf = ByteBuffer.allocate(1024);
				
				//initialize part
				rxStreamOrder = PacketPos.RX_PACKET_POS_START;
				
				//loop
				while (true) {
					try {
						//sleep 1msec
						Thread.sleep(1);//throw, exit
					} catch (InterruptedException e) {
						//error
						break;
					}
					
					//initialize buffer
					msg = null;
					
					synchronized(rxAry) {
						//check data list
						if (!rxAry.isEmpty()) {
							//get first data
							msg = rxAry.get(0);//throw
							//delete data from list
							rxAry.remove(0);//throw
						}
					}//sync : data list
					
					//no data
					if (msg == null) 
						continue;
					
					try {
						//initialize data position
						i = 0;
						//scan data
						while (i < msg.capacity()) {
							//start part
							if (rxStreamOrder == PacketPos.RX_PACKET_POS_START) {
								//find start flag
								for (; i<msg.capacity(); ++i) {
									//check 
									if (msg.get(i) == LinkProtocol.START_FLAG) {
										//next - header part
										rxStreamOrder = PacketPos.RX_PACKET_POS_HEAD;
										//initialize part position
										p = 0;
										break;
									}
								}//for : find start flag		
							}//if : start part
			
							//header part
							if (rxStreamOrder == PacketPos.RX_PACKET_POS_HEAD) {
								//?�신 ?�이?�의 ?��? ?�기???�더???��? ?�기 중에???��? 쪽을 ?�택 
								iWrite = Math.min(LinkProtocol.PACKET_HEAD_SIZE -p, msg.capacity() -i);
			
								//read!
								//tmpbuf.put(msg.array(), i, iWrite);
								System.arraycopy(msg.array(), i, head, p, iWrite);
								//shift position
								p += iWrite;
								i += iWrite;
			
								//check header size
								if (p == LinkProtocol.PACKET_HEAD_SIZE) {
									//create packet
									packet = new LinkPacket();
									//System.arraycopy(head, MSG_HEAD_TYPE_POS, buf4byte, 0, 2);
									//command code
									packet.code = ByteBuffer.wrap(head, MSG_HEAD_TYPE_POS, 2).getShort();//throw
									//device number
									packet.deviceNo = head[MSG_HEAD_NO_POS];// tmpbuf.getShort(MSG_HEAD_TYPE_POS);
									//System.arraycopy(head, MSG_HEAD_SIZE_POS, buf4byte, 0, 4);
									//data size
									packet.dataSize = ByteBuffer.wrap(head, MSG_HEAD_SIZE_POS, 4).getInt();//throw
									//data buffer 
									packet.data = new byte [packet.dataSize];
									
									//initialize part position
									p = 0;
									//next - data part
									rxStreamOrder = PacketPos.RX_PACKET_POS_DATA;
								}//if : check header size
							}//if : header part
			
							//data part
							if (rxStreamOrder == PacketPos.RX_PACKET_POS_DATA) {
								//?�신 ?�이?�의 ?��? ?�기???�이?�의 ?��? ?�기 중에???��? 쪽을 ?�택 
								iWrite = Math.min(packet.dataSize -p, msg.capacity()-i);
			
								//store data
								System.arraycopy(msg.array(), i, packet.data, p, iWrite);//throw
								//shift position
								p += iWrite;
								i += iWrite;
			
								//check data size
								if (p == packet.dataSize) {
									//next - footer part
									rxStreamOrder = PacketPos.RX_PACKET_POS_TAIL;
									//initialize part position
									p=0;
								}
							}//if : data part
			
							//footer part
							if (rxStreamOrder == PacketPos.RX_PACKET_POS_TAIL) {
								//?�신 ?�이?�의 ?��? ?�기??꼬리???��? ?�기 중에???��? 쪽을 ?�택
								iWrite = Math.min(3-p, msg.capacity() -i);
								//read!
								System.arraycopy(msg.array(), i, tail, p, iWrite);//throw
								//shift position
								p += iWrite;
								i += iWrite;
			
								//check footer size
								if (p == 3) {	
									//checksum
/*									checksum = ByteBuffer.wrap(tail, 0, 2).getShort();//throw
									
									//check buffer size (header + data - start flag) 
									if (tmpbuf.capacity() < head.length -1 + packet.dataSize)
										tmpbuf = ByteBuffer.allocate(head.length -1 + packet.dataSize);
									tmpbuf.limit(head.length -1 + packet.dataSize);
									//initialize buffer position
									tmpbuf.position(0);
									//put header
									tmpbuf.put(head, 0, head.length -1);//throw
									//put data
									tmpbuf.put(packet.data);//throw
									//initialize buffer position
									tmpbuf.position(0);
									
									//sum short values
									sum=0;
									for (j=0; j< tmpbuf.limit() -1; j+=2)
										sum += (long)tmpbuf.getShort(j);//throw
									//sum last byte
									if (j != tmpbuf.limit())
										sum += (long)(tmpbuf.get(j) & 0xff);//throw
									
									//calculate checksum
									sum = (sum >> 16) + (sum & 0xffff);
									sum += (sum >> 16);
									
									if (checksum == (short)(~sum))
*/										//store packet time
										packet.time = System.currentTimeMillis();
										//add packet to list
										packets.add(packet);
									
									//initialize part
									rxStreamOrder = PacketPos.RX_PACKET_POS_START;
								}//if : check footer size
							}//if : footer part
						}//while : scan data
					} catch (Exception e) {
						//next data
						rxStreamOrder = PacketPos.RX_PACKET_POS_START;
					}
				}//while : interrupt				
			}//run
		});
		
		thread.start();
	}//start
	
	public void setRawData(final byte [] msg, int size) {
		//checksum buffer index
//		int j=0;
		//checksum
//		short checksum = 0;
		//data sum value
//		long sum = 0;
		//checksum buffer
//		ByteBuffer tmpbuf = ByteBuffer.allocate(1024);
							
		//no data
		if (msg == null) 
			return;
			
		try {
			//size to read
			int iWrite=0;
			//initialize data position
			int i = 0;
			
			//scan data
			while (i < size) {
				//start part
				if (rxStreamOrder == PacketPos.RX_PACKET_POS_START) {
					//find start flag
					for (; i<size; ++i) {
						//check 
						if (msg[i] == LinkProtocol.START_FLAG) {
							//next - header part
							rxStreamOrder = PacketPos.RX_PACKET_POS_HEAD;
							//initialize part position
							fieldPos = 0;
							break;
						}
					}//for : find start flag		
				}//if : start part

				//header part
				if (rxStreamOrder == PacketPos.RX_PACKET_POS_HEAD) {
 
					iWrite = Math.min(LinkProtocol.PACKET_HEAD_SIZE -fieldPos, size -i);

					//read!
					//tmpbuf.put(msg.array(), i, iWrite);
					System.arraycopy(msg, i, headBuf, fieldPos, iWrite);//throw
					//shift position
					fieldPos += iWrite;
					i += iWrite;

					//check header size
					if (fieldPos == LinkProtocol.PACKET_HEAD_SIZE) {
						//create packet
						curPacket = new LinkPacket();
						//System.arraycopy(head, MSG_HEAD_TYPE_POS, buf4byte, 0, 2);
						//command code
						curPacket.code = ByteBuffer.wrap(headBuf, MSG_HEAD_TYPE_POS, 2).getShort();//throw
						//device number
						curPacket.deviceNo = headBuf[MSG_HEAD_NO_POS];// tmpbuf.getShort(MSG_HEAD_TYPE_POS);
						//System.arraycopy(head, MSG_HEAD_SIZE_POS, buf4byte, 0, 4);
						//data size
						curPacket.dataSize = ByteBuffer.wrap(headBuf, MSG_HEAD_SIZE_POS, 4).getInt();//throw
						//data buffer 
						curPacket.data = new byte [curPacket.dataSize];
						
						//initialize part position
						fieldPos = 0;
						//next - data part
						rxStreamOrder = PacketPos.RX_PACKET_POS_DATA;
					}//if : check header size
				}//if : header part

				//data part
				if (rxStreamOrder == PacketPos.RX_PACKET_POS_DATA) {
					//
					iWrite = Math.min(curPacket.dataSize -fieldPos, size-i);

					//store data
					System.arraycopy(msg, i, curPacket.data, fieldPos, iWrite);//throw
					//shift position
					fieldPos += iWrite;
					i += iWrite;

					//check data size
					if (fieldPos == curPacket.dataSize) {
						//next - footer part
						rxStreamOrder = PacketPos.RX_PACKET_POS_TAIL;
						//initialize part position
						fieldPos=0;
					}
				}//if : data part

				//footer part
				if (rxStreamOrder == PacketPos.RX_PACKET_POS_TAIL) {
					//
					iWrite = Math.min(3-fieldPos, size -i);
					//read!
					System.arraycopy(msg, i, trailBuf, fieldPos, iWrite);//throw
					//shift position
					fieldPos += iWrite;
					i += iWrite;

					//check footer size
					if (fieldPos == 3) {	
						//checksum
/*						checksum = ByteBuffer.wrap(trailBuf, 0, 2).getShort();//throw
						
						//check buffer size (header + data - start flag) 
						if (tmpbuf.capacity() < headBuf.length -1 + curPacket.dataSize)
							tmpbuf = ByteBuffer.allocate(headBuf.length -1 + curPacket.dataSize);//throw
						tmpbuf.limit(headBuf.length -1 + curPacket.dataSize);//throw
						//initialize buffer position
						tmpbuf.position(0);//throw
						//put header
						tmpbuf.put(headBuf, 0, headBuf.length -1);//throw
						//put data
						tmpbuf.put(curPacket.data);//throw
						//initialize buffer position
						tmpbuf.position(0);//throw
						
						//sum short values
						sum=0;
						for (j=0; j< tmpbuf.limit() -1; j+=2)
							sum += (long)tmpbuf.getShort(j);//throw
						//sum last byte
						if (j != tmpbuf.limit())
							sum += (long)(tmpbuf.get(j) & 0xff);//throw
						
						//calculate checksum
						sum = (sum >> 16) + (sum & 0xffff);
						sum += (sum >> 16);
						
						if (checksum == (short)(~sum))
*/							//store packet time
						curPacket.time = System.currentTimeMillis();
							//add packet to list
						packets.add(curPacket);//throw
						
						//initialize part
						rxStreamOrder = PacketPos.RX_PACKET_POS_START;
					}//if : check footer size
				}//if : footer part
			}//while : scan data
		} catch (Exception e) {
			//next data
			rxStreamOrder = PacketPos.RX_PACKET_POS_START;
		}
		//try - catch
	}
	
	public void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
		packets.clear();
		rxAry.clear();
	}
	
	static public short getFileSequence(LinkPacket packet) throws Exception {
		if (packet.dataSize < 2)
			throw new Exception("");
		
		ByteBuffer buf = ByteBuffer.wrap(packet.data);
		return buf.getShort();
	}
	
	static public int getFileSize(LinkPacket packet) {
		
		try {
			if (getFileSequence(packet) != 0)
				return -1;
		} catch (Exception e) {
			return -1;
		}
			
		if (packet.dataSize < 6)
			return -1;
		
		ByteBuffer buf = ByteBuffer.wrap(packet.data, 2, 4);
		return buf.getInt();
	}//getFileSize
	
}
