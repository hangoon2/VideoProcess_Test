package com.onycom.test.threads;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.onycom.test.IVPSCommon;

public class DCReceiveThread extends Thread{
    private BufferedInputStream bis = null;
    public boolean Running = true;

	public DCReceiveThread(BufferedInputStream byteIS) {
		this.bis = byteIS;
		this.Running = true;
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
				     				break;
				     				
				     			case IVPSCommon.CMD_CMD_ACK: 
				     				break;
				     				
				     			case IVPSCommon.CMD_CMD_LOGCAT: 
					     			break;
					     			
				     			case IVPSCommon.CMD_GUEST_UPDATED:
				     				System.out.println("Guest Updated");
				     				break;
				     				
				     			case IVPSCommon.CMD_CAPTURE_COMPLETED:
				     				String strData = String.valueOf( pRcvBuf.array() );
				     				System.out.println("Record Success : " + strData);
				     				break;
				     				
				     			case 32000:
//				     				System.out.println("VPS HearBeat");
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
       
 }
