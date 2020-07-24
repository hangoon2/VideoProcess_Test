package com.onycom.test;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.onycom.test.ClientProtocol.Packet;

@SuppressWarnings("serial")
public class TestClient extends JFrame {
	
	private static TestClient frame = null;
	private Canvas canvas = null;
	
	private Thread protocolThread = null;
	private ClientProtocol protocol = null;
	private Socket client = null;
	
	private boolean closed = false;
	private DataInputStream reader = null;
	
	private int lastWidth = 480;
	private int lastHeight = 960;
	
//	private static long totalReceiveData = 0;
	
	public enum PacketPos {
		RX_PACKET_POS_START,
		RX_PACKET_POS_HEAD,
		RX_PACKET_POS_DATA,
		RX_PACKET_POS_TAIL;
	}
	
	public static TestClient getInstance() {
		if(frame == null) {
			frame = new TestClient();
		}
		
		return frame;
	}
	
	private TestClient() {
		// TODO Auto-generated constructor stub
		super();
		setSize(384, 768);
		getContentPane().setLayout( new BorderLayout() );
		
		canvas = new Canvas();
		getContentPane().add(canvas, BorderLayout.CENTER);
		
		protocol = new ClientProtocol();
		protocolThread = new Thread(protocol);
		protocolThread.start();
		
		try {
			client = new Socket(InetAddress.getByName("127.0.0.1"), 8841);
			client.setTcpNoDelay(true);
			reader = new DataInputStream( client.getInputStream() );
			
			new Socket(InetAddress.getByName("127.0.0.1"), 8851);
//			
//			byte [] startPacket = TestClient.encodePacket(1, (short)1, 0, new byte[0]);
//			client.getOutputStream().write(startPacket, 0, startPacket.length);
//			client.getOutputStream().flush();
//			
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		setVisible(true);
		
		addWindowStateListener(new WindowStateListener() {
			
			@Override
			public void windowStateChanged(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(protocolThread != null) {
					try {
						protocolThread.interrupt();	
						protocolThread.join();
						
						if(reader != null) {
							reader.close();
						}
						
						if(client != null) {
							client.close();
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						protocolThread = null;
						closed = true;
						reader = null;
						client = null;
						
						frame = null;
					}
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				if(protocolThread != null) {
					try {
						if(reader != null) {
							reader.close();
						}
						
						if(client != null) {
							client.close();
						}
					
						protocolThread.interrupt();	
						protocolThread.join();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						protocolThread = null;
						closed = true;
						reader = null;
						client = null;
					}
				}
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public byte[] readFully() {
		int ret = 0;
		try {
			ret = reader.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(ret < 1) return null;
		
		byte[] buffer = new byte[ret];
		try {
			reader.read(buffer, 0, ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return buffer;
	}
	
	public void putRawData(final byte [] data) {
		protocol.putRawData(data);
	}
	
	public Packet getPacket() {
		return protocol.getPacket();
	}
	
	public boolean isThreadInturruped() {
		return closed;
	}
	
	public void setImageData(int width, int height, byte[] imgData) {
		if(canvas != null) {
			if(lastWidth != 0 && lastHeight != 0) {
				if(lastWidth == height && lastHeight == width) {
					int newW= getSize().height;
					int newH = getSize().width;
					setSize(newW, newH);
				}
			}
			
			lastWidth = width;
			lastHeight = height;
			
			canvas.drawImage(imgData);
		}
	}
	
	private class Canvas extends JPanel {
		
		private BufferedImage img = null;
		
		public Canvas() {
			
		}
		
		public void drawImage(byte[] imgData) {
			try {
				ByteArrayInputStream stream = new ByteArrayInputStream(imgData);
				img = ImageIO.read(stream);
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			repaint();
		}
		
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			super.paint(g);
			
			if(img != null) {
				g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
			}
		}
		
	}
	
	public static void main(String[] args) {
		final TestClient client = TestClient.getInstance();
		
		try {
			
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while( !client.isThreadInturruped() ) {
						try {
			    			Thread.sleep(1);
			    			
							byte[] buffer = client.readFully();
							if(buffer == null) continue;
							
//							totalReceiveData += buffer.length;
							
//							System.out.println("Receive Data : " + totalReceiveData);
							
							client.putRawData(buffer);
						} catch (Exception e) {
							break;
						}
					}
					
					System.out.println("Socket Read Thread Closed");
				}
			});
			thread.start();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					long tickCount = System.currentTimeMillis();
					int frameRate = 0;
					
					// TODO Auto-generated method stub
					while( !client.isThreadInturruped() ) {
						Packet packet = client.getPacket();
						if(packet != null) {
							System.out.println("Receive Data : " + packet.code);
							if(packet.code == 20004 || packet.code == 20005) {
								frameRate++;
								
								if(System.currentTimeMillis() - tickCount >= 1000) {
			                		tickCount = System.currentTimeMillis();
			                		System.out.println("Receive from VPS : " + frameRate + "f/s");
			                		frameRate = 0;
			                	}
								
								byte[] data = packet.data;
								
								short width = ByteBuffer.wrap(data, 12, 2).getShort();
								short height = ByteBuffer.wrap(data, 14, 2).getShort();
								
								byte[] imgBytes = TestClient.getImageData(data, packet.dataSize-17);
//								System.out.println("Image Data Size =========== " + imgBytes.length + ", " + totalReceiveData);
								client.setImageData(width, height, imgBytes);
							}
						}
					}
					
					System.out.println("Packet Process Thread Closed");
				}
			}).start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Exited Test Client");
	}
	
//	private static byte[] convertImage(byte[] pPacket) {
//		byte[] bytes = new byte[pPacket.length];
//		int offset = 0;
//		
//		for(int i = 0; i < pPacket.length; i++) {
//			offset = i * 4;
//			int a = (pPacket[offset+3] & 0xff);
//			int r = (pPacket[offset+0] & 0xff);
//			int g = (pPacket[offset+1] & 0xff);
//			int b = (pPacket[offset+2] & 0xff);
//			
//			int value = (byte) (a << 24) | (r << 16) | (g << 8) | (b);
//			bytes[i] = (byte)value;
//		}
//		
//		return bytes;
//	}
	
	private static byte[] getImageData(byte[] pPacket, int dataSize) {
		byte[] imgData = new byte[dataSize];
		
		System.arraycopy(pPacket, 17, imgData, 0, dataSize);
		
		return imgData;
	}
	
	static final byte START_FLAG = 0x7F;
	static final byte END_FLAG = (byte) 0xEF;
		
	public static final byte[] encodePacket(int deviceNo, short code, int dataSize, final byte [] data) {
		//head + data + tail(checksum + end)
		ByteBuffer ret = ByteBuffer.allocate(8 + dataSize + 3);
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
		
		//checksum
		ret.putShort((short) 0);
		//end flag
		ret.put(END_FLAG);
				
		return ret.array();
	}	
	
}
