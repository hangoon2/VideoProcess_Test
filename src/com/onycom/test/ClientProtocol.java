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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientProtocol implements Runnable {
	
	private enum PacketPos {
		RX_PACKET_POS_START,
		RX_PACKET_POS_HEAD,
		RX_PACKET_POS_DATA,
		RX_PACKET_POS_TAIL;
	}
	
	public class Packet {
		short code = 0;
		byte deviceNo = 0;
		int dataSize = 0;
		byte [] data = null;
	}
	
	static final int PACKET_HEAD_SIZE = 8;
	static final int PACKET_TAIL_SIZE = 3;
	
	static final byte START_FLAG = 0x7F;
	static final byte END_FLAG = (byte) 0xEF;
	
	static final int MSG_HEAD_SIZE_POS = 1;
	static final int MSG_HEAD_TYPE_POS = 5;
	static final int MSG_HEAD_NO_POS = 7;
 
	private PacketPos rxStreamOrder = PacketPos.RX_PACKET_POS_START;
 
	private List<ByteBuffer> rxAry = Collections.synchronizedList(new ArrayList<ByteBuffer>());
	private List<Packet> packets = Collections.synchronizedList(new ArrayList<Packet>());
	
	public ClientProtocol () {
	}
	
	public void putRawData(final byte [] data) {
		rxAry.add(ByteBuffer.wrap(data));
	}
	
	public final Packet getPacket() {
		Packet p = null;
		
		synchronized (packets) {
			if (!packets.isEmpty()) {
				p = packets.get(0);
				packets.remove(0);
			}
		}
	
		return p;
	}
	
	public void run() {
		byte [] head = new byte [PACKET_HEAD_SIZE];
		byte [] tail = new byte [PACKET_TAIL_SIZE];
		int iPacketPos = 0, iReadPos = 0, iWrite = 0;
		Packet packet = null;
		ByteBuffer msg = null;

		rxStreamOrder = PacketPos.RX_PACKET_POS_START;
		
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				break;
			}
			
			msg = null;
			
			synchronized (rxAry) {
				if(!rxAry.isEmpty()) {
					msg = rxAry.get(0);
					rxAry.remove(0);
				}
			}
			
			if(msg == null) { 
				continue;
			}
			
			iReadPos = 0;
			
			while(iReadPos < msg.capacity()) {
				if(rxStreamOrder == PacketPos.RX_PACKET_POS_START) {
					for(; iReadPos < msg.capacity(); ++iReadPos) {
						if(msg.get(iReadPos) == LinkProtocol.START_FLAG) {
							rxStreamOrder = PacketPos.RX_PACKET_POS_HEAD;
							iPacketPos = 0;
							break;
						}
					}
				}
				
				// head
				if(rxStreamOrder == PacketPos.RX_PACKET_POS_HEAD) {
					iWrite = Math.min(LinkProtocol.PACKET_HEAD_SIZE - iPacketPos, msg.capacity() - iReadPos);

					System.arraycopy(msg.array(), iReadPos, head, iPacketPos, iWrite);

					iPacketPos += iWrite;
					iReadPos += iWrite;
					
					if(iPacketPos == LinkProtocol.PACKET_HEAD_SIZE) {
						packet = new Packet();

						packet.code = ByteBuffer.wrap(head, MSG_HEAD_TYPE_POS, 2).getShort();
						packet.deviceNo = head[MSG_HEAD_NO_POS];
						packet.dataSize = ByteBuffer.wrap(head, MSG_HEAD_SIZE_POS, 4).getInt();
						if(packet.dataSize < 0 || packet.dataSize > 1024 * 1024) {
							// invalid data
							System.out.println("Invalid Receive Data");
							rxStreamOrder = PacketPos.RX_PACKET_POS_START;
							continue;
						}
						packet.data = new byte[packet.dataSize];
						
						rxStreamOrder = PacketPos.RX_PACKET_POS_DATA;
						iPacketPos = 0;
					}
				}
				
				// data
				if(rxStreamOrder == PacketPos.RX_PACKET_POS_DATA) {
					iWrite = Math.min(packet.dataSize - iPacketPos, msg.capacity() - iReadPos);

					System.arraycopy(msg.array(), iReadPos, packet.data, iPacketPos, iWrite);

					iPacketPos += iWrite;
					iReadPos += iWrite;
					
					if(iPacketPos == packet.dataSize) {
						rxStreamOrder = PacketPos.RX_PACKET_POS_TAIL;
						iPacketPos = 0;
					}
				}
				
				// tail
				if(rxStreamOrder == PacketPos.RX_PACKET_POS_TAIL) {
					iWrite = Math.min(PACKET_TAIL_SIZE - iPacketPos, msg.capacity() - iReadPos);

					System.arraycopy(msg.array(), iReadPos, tail, iPacketPos, iWrite);

					iPacketPos += iWrite;
					iReadPos += iWrite;
					
					if(iPacketPos == PACKET_TAIL_SIZE) {
						packets.add(packet);
						
						rxStreamOrder = PacketPos.RX_PACKET_POS_START;
					}
				}
			}
		}
		
		packets.clear();
		rxAry.clear();
		
		System.out.println("Client Protocol Thread Closed");
	}

}
