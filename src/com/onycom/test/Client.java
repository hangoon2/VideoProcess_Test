package com.onycom.test;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.onycom.test.audio.AudioManager;
import com.onycom.test.audio.JAacPlayer;
import com.onycom.test.models.BlockingQueue;
import com.onycom.test.models.DeviceStatus;
import com.onycom.test.models.MediaDataQueue;
import com.onycom.test.models.VideoData;
import com.onycom.test.threads.AudioReceiveThread;
import com.onycom.test.threads.ReceiveThread;
import com.onycom.test.threads.ReceiveThread.ITFRecordStopListener;

@SuppressWarnings("serial")
public class Client extends JDialog implements MouseListener, MouseMotionListener, ITFRecordStopListener {
	
	public static final byte START_FLAG = 0x7F;
	public static final byte END_FLAG = (byte)0xEF;
	
	public MediaDataQueue recvQue = null;
	BlockingQueue<VideoData> videoQue = null;
	
	private Socket audioClient = null;
	private BufferedInputStream audioBis = null;
	private DataOutputStream audioBos = null;
	
	public JAacPlayer audioPlayerThread = null;
	public AudioReceiveThread audioRecvThread = null;
	
	private boolean isSoundOn = false;
	
	private DeviceStatus ds = new DeviceStatus();
	
	private Socket Client = null;
	private BufferedInputStream bis = null;
	private DataOutputStream bos = null;
	
	/* device control agent */
	private Socket agentSock = null;
	private DataOutputStream agentOs = null;
	
	ReceiveThread recvThread = null;
	
	TFDisplayView imageView = null;
//	TFSubDisplayView subImageView = null;
	
	private int m_nHpNo = 0;
	private boolean m_bGuest = false;
	private VPSTest m_frame = null;
	
	private JButton captureBtn = null;
	private JButton recordStart = null;
	private JButton recordStop = null;
	private JButton soundOnOff = null;
	private JButton rotateBtn = null;
	private JButton dumpBtn = null;
	
	boolean rotate = false;
	
	int guestCount = 0;
	int m_guestNum = 0;
	
	boolean mouseMoving = false;
    boolean mouseDownAndUp = false;
	long timeMouseDownSave = 0;
    Point ptMouseDownSave = null;
    Point ptRealDeviceLcdPointSave = null;
    
    public Timer timerTouchDownCheck = null;
    public Timer timerLongClickCheck = null;
    public Timer timerDoubleClickCheck = null;

    private ArrayList<Point> movePoints = new ArrayList<Point>();
    
    AudioManager audioManager = null;
	
	public Client(final VPSTest frame, final int nHpNo, boolean guest, int guestNum) {
		super(frame);
		// TODO Auto-generated constructor stub
		setLayout( new BorderLayout(0, 5) );
		
		JPanel buttonPanel = new JPanel();
		
		rotateBtn = new JButton("Landscape");
		buttonPanel.add(rotateBtn);
		
		captureBtn = new JButton("Capture");
		buttonPanel.add(captureBtn);
		
		recordStart = new JButton("Rec Start");
		buttonPanel.add(recordStart);
		
		recordStop = new JButton("Rec Stop");
		recordStop.setEnabled(false);
		buttonPanel.add(recordStop);
		
		soundOnOff = new JButton("Sound On/Off");
		buttonPanel.add(soundOnOff);
		
		dumpBtn = new JButton("Dump");
		buttonPanel.add(dumpBtn);
		
		add(buttonPanel, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerSize(20);
		
		imageView = new TFDisplayView(nHpNo);
//		add(imageView, BorderLayout.CENTER);
				
//		subImageView = new TFSubDisplayView(1);
//		splitPane.add(subImageView);
//		splitPane.add(imageView);
		
//		add(splitPane, BorderLayout.CENTER);
		add(imageView, BorderLayout.CENTER);
		
		setSize(500, 600);
//		splitPane.setDividerLocation(500);
		
		m_nHpNo = nHpNo;
		m_bGuest = guest;
		m_frame = frame;
		m_guestNum = guestNum;
		
		connectAgent();
		
		int port = TFIniFile.getInstance().getInt("Stream", "TCPPort", 10001);
		Client = connectToVPS("192.168.1.38", port);
		if(Client != null) {
			try {
				bis = new BufferedInputStream(Client.getInputStream());
				bos = new DataOutputStream(Client.getOutputStream());
				
//				imageView.setDataOutputStream(bos);
				
				if(guest) {
					setTitle("Guest Device " + nHpNo);
					byte[] packet = makeVPSCommand(30007, nHpNo, "guest device" + m_guestNum, 0);
					sendCommandToVPS(Client, packet);
				} else {
					setTitle("Host Device " + nHpNo);
					byte[] packet = makeVPSCommand(30000, nHpNo, "applet_" + nHpNo, 0);
					sendCommandToVPS(Client, packet);
					
					packet = makeVPSCommand(1003, nHpNo, "applet_" + nHpNo, 0);
					sendCommandToVPS(Client, packet);
				}
				
				recvQue = new MediaDataQueue();
				
				recvThread = new ReceiveThread(this.bis, recvQue, imageView, null);
				recvThread.setRecordStopListener(this);
				recvThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		captureBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				capture(nHpNo);
				sendPowerKey();
			}
		});
		
		recordStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				record(nHpNo, true);
			}
		});
		
		recordStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				record(nHpNo, false);
			}
		});
				
		soundOnOff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				onSound();
			}
		});
		
		rotateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(rotate) {
					// portrait
					byte[] data = makeOnyPacket(m_nHpNo, 1003, null, 0);
					sendCommandToVPS(Client, data);
					
					rotate = false;
					rotateBtn.setText("Landscape");
					
//					if(agentOs != null) {
//						try {
//							agentOs.write(data);
//							agentOs.flush();
//							rotate = false;
//							
//							rotateBtn.setText("Landscape");
//						} catch (IOException ex) {
//							// TODO Auto-generated catch block
//							ex.printStackTrace();
//						}					
//					}
				} else {
					// landscape
					byte[] data = makeOnyPacket(m_nHpNo, 1004, null, 0);
					sendCommandToVPS(Client, data);
					
					rotate = true;
					rotateBtn.setText("Portrait");
					
//					if(agentOs != null) {
//						try {
//							agentOs.write(data);
//							agentOs.flush();
//							rotate = true;
//							
//							rotateBtn.setText("Portrait");
//						} catch (IOException ex) {
//							// TODO Auto-generated catch block
//							ex.printStackTrace();
//						}					
//					}
				}
			}
		});
		
		dumpBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				byte[] data = makeOnyPacket(m_nHpNo, 305, null, 0);
				if(agentOs != null) {
					try {
						agentOs.write(data);
						agentOs.flush();
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}					
				}
			}
		});
				
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				stopTest(nHpNo);
				frame.stopTest(nHpNo);
				frame.closeDevice(nHpNo);
			}
		});
	}
	
	public void connectAgent() {
//		String cmd = "adb shell uiautomator runtest /data/local/tmp/com.onycom.agent.jar -c com.onycom.agent.AgentMain";
//		shell(cmd);
//		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
				
		agentSock = connectToAgent("127.0.0.1", 2013);
		if(agentSock != null) {
			try {
				agentOs = new DataOutputStream(agentSock.getOutputStream());
				imageView.setDataOutputStream(agentOs);
//				subImageView.setDataOutputStream(agentOs);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
//		sendPowerKey();
	}
	
	public void sendPowerKey() {
		ByteBuffer b = ByteBuffer.allocate(3);
		b.put((byte)0);
		b.putShort((short)26);
		byte[] data = makeOnyPacket(m_nHpNo, 10, b.array(), 3);
		if(agentOs != null) {
			try {
				agentOs.write(data);
				agentOs.flush();
				System.out.println("Send Data : " + data.length);
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}					
		}
	}
	
	public void capture(int nHpNo) {
		byte[] packet = makeOnyPacket(nHpNo, 1006, null, 0);
		sendCommandToVPS(Client, packet);
	}
	
	public void record(int nHpNo, boolean onOff) {
		byte[] packet = makeRecordPacket(nHpNo, 1005, onOff);
		sendCommandToVPS(Client, packet);
		
		if(onOff) {
			recordStart.setEnabled(false);
			recordStop.setEnabled(true);
		} else {
			recordStart.setEnabled(true);
			recordStop.setEnabled(false);
		}
	}
	
	public boolean soundOn() {

    	byte[] commandPacket = makeVPSCommand(30004, m_nHpNo, "applet_" + m_nHpNo, 1);
	       
     	sendCommandToVPSAudio(audioClient, commandPacket);

     	isSoundOn = true;

    	return true;
    }
	
	public void sendCommandToVPSAudio(Socket sock, byte[] commandPacket) {
        
        if((sock != null) && sock.isConnected() && this.audioBos != null){
     	   try {
     		   this.audioBos.write(commandPacket);
     		   this.audioBos.flush();
     	   } catch (IOException e) {
 		       e.printStackTrace();

		      disconnectFromVpsAudio();
     	   }
        }
    }

	/**
	 * Sound off
	 * 
	 * @return boolean
	 */
    public boolean soundOff() {

    	byte[] commandPacket = makeVPSCommand(30004, m_nHpNo, "applet_" + m_nHpNo, 0);
	       
    	sendCommandToVPSAudio(audioClient, commandPacket);

     	isSoundOn = false;

        return true;
     }
	
	protected void onSound() {
		if (isSoundOn) {
			soundOff();

			disconnectFromVpsAudio();
			
			soundOnOff.setText("Sound On");
		} else {
			connectToVpsAudio();

			soundOn();
			
			soundOnOff.setText("Sound Off");
		}
	}
	
	public void connectToVpsAudio() {
		String ip = "192.168.1.38";
		int port = 10001;
		
		try {
			audioManager = new AudioManager();
			
		 	// Connect as audio client
		 	audioClient = connectToVPS(ip, port);
			audioBis = new BufferedInputStream(audioClient.getInputStream());
			audioBos = new DataOutputStream(audioClient.getOutputStream());

			/**
	     	 * ����� ������ ó�� ������ ����
	     	 * */
		 	audioRecvThread = new AudioReceiveThread(this.audioBis, audioManager);
		 	audioRecvThread.setPriority(Thread.MAX_PRIORITY);
	        audioRecvThread.start();

	        try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	        byte[] commandPacket = makeVPSCommand(30005, m_nHpNo, "applet_" + m_nHpNo, 0);
			sendCommandToVPSAudio(audioClient, commandPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectFromVpsAudio() {
		CloseAudioSocket();
		
		if(audioManager != null) {
			audioManager.doStop();
			audioManager = null;
		}

		if(audioRecvThread != null) {
			audioRecvThread.doStop();
			audioRecvThread = null;
		}

		if(audioPlayerThread != null) {
			audioPlayerThread.doStop();
			audioPlayerThread = null;
		}

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void CloseAudioSocket() {
		try {
			if ((audioClient != null) && audioClient.isConnected()) {
				if (this.audioBis != null) {
					this.audioBis.close();
					this.audioBis = null;
				}
				if (this.audioBos != null) {
					this.audioBos.close();
					this.audioBos = null;
				}
				this.audioClient.close();
				Thread.sleep(100);
				this.audioClient = null;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (this.audioBis != null) {
				try {
					this.audioBis.close();
				} catch (Exception ex) {
				}
			}
			if (this.audioBos != null) {
				try {
					this.audioBos.close();
				} catch (Exception ex) {
				}
			}
			if (this.audioClient != null) {
				try {
					this.audioClient.close();
					Thread.sleep(100);
				} catch (Exception ex) {
				}
			}
		}
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stopTest(m_nHpNo);
		m_frame.stopTest(m_nHpNo);
		m_frame.closeDevice(m_nHpNo);
		
		super.dispose();
	}
	
	@Override
	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
//		if(b) {
//			VPSTest frame = (VPSTest)getParent();
//			frame.openDevice(m_nHpNo);
//		}
		super.setVisible(b);
	}
	
	public void stopTest(int nHpNo) {
		if(m_bGuest) {
			byte[] packet = makeVPSCommand(30010, nHpNo, "guest device" + m_guestNum, 0);
			sendCommandToVPS(Client, packet);
		} else {
			byte[] packet = makeVPSCommand(30010, nHpNo, "applet_" + nHpNo, 0);
			sendCommandToVPS(Client, packet);
		}
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if((agentSock != null) && agentSock.isConnected()) {
			if(agentOs != null) {
				byte[] data = makeClosePacket(m_nHpNo);
				try {
					agentOs.write(data);
					agentOs.flush();
					
					this.agentSock.close();
					Thread.sleep(100);
					this.agentSock = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}				
		}
		
		if(recvThread != null) {
			recvThread.doStop();
			CloseSocket();
			recvThread = null;
		}
		
		if(imageView != null) {
			imageView.stopMirroring();
			imageView = null;
		}
	}
	
	private Socket connectToAgent(String ip, int port) {
		Socket clientSocket = new Socket();
		
		SocketAddress addr = new InetSocketAddress(ip, port);
		
		try {
			clientSocket.connect(addr, 3000);
			clientSocket.setSoTimeout(3000);
			clientSocket.setTcpNoDelay(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return clientSocket;
	}
	
	private Socket connectToVPS(String ip, int port) {
		Socket clientSocket = new Socket();
		
		SocketAddress addr = new InetSocketAddress(ip, port);
		
		try {
			clientSocket.connect(addr, 3000);
			clientSocket.setSoTimeout(3000);
			clientSocket.setTcpNoDelay(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return clientSocket;
	}
	
	public boolean sendCommandToVPS(Socket sock, byte[] commandPacket) {
		boolean r = false;

		if((sock != null) && sock.isConnected() && this.bos != null){
			try {
				this.bos.write(commandPacket);
				this.bos.flush();

				r = true;
			} catch (IOException e) {
				CloseSocket();
			}
		}

		return r;
	}
	
	private void CloseSocket() {
		try {
			if(agentOs != null) {
				agentOs.close();
				agentOs = null;
			}
			
			if ((Client != null) && Client.isConnected()) {
				this.Client.close();
				Thread.sleep(100);
				this.Client = null;
			}
			
			disconnectFromVpsAudio();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (this.Client != null) {
				try {
					this.Client.close();
					Thread.sleep(100);
				} catch (Exception ex) {
				}
			}
		}
	}
	
	public byte[] makeVPSCommand(int cmd, int vpsChan, String id, int value){

		short checkSum = 0;
		
		int headerSize = 7, dataSize = 0, destPos = 0;
		byte startFlag = (byte) 0x7F;
		byte endFlag = (byte) 0xEF;
		
		ByteBuffer  bbDataSize = ByteBuffer.wrap(new byte[4]);
		ByteBuffer  bbCommand = ByteBuffer.wrap(new byte[2]);
		ByteBuffer  bbCheckSum = ByteBuffer.wrap(new byte[2]);
		byte bDeviceNo = 0;
		byte [] baPacket = null;
		
		bbCommand.putShort((short)cmd);
		bDeviceNo = (byte)vpsChan;
		
		if ( cmd == 30000 || cmd == 30010 || cmd == 1003 || cmd == 1004) {
			dataSize = id.length();
			bbDataSize.putInt(dataSize);
			
			baPacket = new byte[1 + headerSize + dataSize + bbCheckSum.limit() + 1];
			baPacket[0] = startFlag;
			destPos = 1;
			System.arraycopy(bbDataSize.array(), 0, baPacket, destPos, bbDataSize.limit());
			destPos += bbDataSize.limit();
			System.arraycopy(bbCommand.array(), 0, baPacket, destPos, bbCommand.limit());
			destPos += bbCommand.limit();
			baPacket[destPos] = bDeviceNo;
			destPos++;
			System.arraycopy(id.getBytes(), 0, baPacket, destPos, id.length());
			destPos += id.length();		
		}
		else{
			// 30001. Quality ( Quality : 1~100)
			// 30002. Frame Rate ( FPS : 0~60)
			// 30003. Compress Mode ( 0: JPG, 1: H.264 )
			// 30004. Audio ON/OFF ( 0: OFF, 1: ON )
			
			//cmdBody = "!#" + vpsConnect.getCommand() + "!#" + vpsChan + "!#" + id + "!#" + value + "!#";
			
			ByteBuffer  bbValue = ByteBuffer.wrap(new byte[2]);
			bbValue.putShort((short)value);
			dataSize = id.length() + 2;
			bbDataSize.putInt(dataSize);
			
			baPacket = new byte[1 + headerSize + dataSize + bbCheckSum.limit() + 1];
			
			baPacket[0] = startFlag;
			destPos = 1;
			System.arraycopy(bbDataSize.array(), 0, baPacket, destPos, bbDataSize.limit());
			destPos += bbDataSize.limit();
			System.arraycopy(bbCommand.array(), 0, baPacket, destPos, bbCommand.limit());
			destPos += bbCommand.limit();
			baPacket[destPos] = bDeviceNo;
			destPos++;
			System.arraycopy(bbValue.array(), 0, baPacket, destPos, bbValue.limit());
			destPos += bbValue.limit();
			System.arraycopy(id.getBytes(), 0, baPacket, destPos, id.length());
			destPos += id.length();
		}
		
		checkSum = calcChechSum(baPacket, baPacket.length);
		bbCheckSum.putShort(checkSum);
		System.arraycopy(bbCheckSum.array(), 0, baPacket, destPos, bbCheckSum.limit());
		
		baPacket[baPacket.length-1] = endFlag;

		
		return baPacket;
    }
	
	public final byte[] makeOnyPacket(int nHpNo, int cmd, byte[] data, int len) { 
		ByteBuffer ret = ByteBuffer.allocate(8 + len + 3);
		
		// start flag
		ret.put(START_FLAG);
		// data size
		ret.putInt(len);
		// command code
		ret.putShort((short)cmd);
		// device no
		ret.put((byte)nHpNo);
		// data
		if(len > 0)
			ret.put(data);
		
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
		ret.put(END_FLAG);
		
		return ret.array();
	}
	
	public final byte[] makeRecordPacket(int nHpNo, int cmd, boolean start) { 
		ByteBuffer ret = ByteBuffer.allocate(8 + 1 + 3);
		
		// start flag
		ret.put(START_FLAG);
		// data size
		ret.putInt(1);
		// command code
		ret.putShort((short)cmd);
		// device no
		ret.put((byte)nHpNo);
		// data
		ret.put(start ? (byte)1 : (byte)0);
		
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
		ret.put(END_FLAG);
		
		return ret.array();
	}
	
	public final byte[] makeClosePacket(int nHpNo) { 
		ByteBuffer ret = ByteBuffer.allocate(8 + 1 + 3);
		
		// start flag
		ret.put(START_FLAG);
		// data size
		ret.putInt(0);
		// command code
		ret.putShort((short)2);
		// device no
		ret.put((byte)nHpNo);
		
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
		ret.put(END_FLAG);
		
		return ret.array();
	}
	
	private short calcChechSum(byte[] data, int bytes) {
		
    	short checksum = 0;
		long sum = 0;
		int pos = 1, calBytes = 0;
	
		if(bytes <= 4){
			return 0;
		}
		
		ByteBuffer bbData = ByteBuffer.wrap(data, 1, bytes-4);
		
		calBytes = bbData.limit()-1;
		while(calBytes > 1) { 
			sum += (long)bbData.getShort(pos);
			pos += 2;
			calBytes -= 2;
		}

		if(calBytes == 1) { 
			sum += (long)(bbData.get(pos) & 0xff);
		}
		
		sum = (sum >> 16) + (sum & 0xffff); 
		
		sum += (sum >> 16);
		checksum = (short) ~sum; 
		
		return checksum;
	}
	
	public byte[] makeVPSDeviceCommand(int vpsConnect, int vpsChan, Point ptStart, Point ptEnd) {
		
		short checkSum = 0;
		
		int headerSize = 7, dataSize = 0, destPos = 0;
		byte startFlag = (byte) 0x7F;
		byte endFlag = (byte) 0xEF;
		
		ByteBuffer  bbDataSize = ByteBuffer.wrap(new byte[4]);
		ByteBuffer  bbCommand = ByteBuffer.wrap(new byte[2]);
		ByteBuffer  bbCheckSum = ByteBuffer.wrap(new byte[2]);
		ByteBuffer  bbPositon = ByteBuffer.wrap(new byte[2]);
		byte bDeviceNo = 0;
		byte [] baPacket = null;
		
		bbCommand.putShort((short)vpsConnect);
		bDeviceNo = (byte)vpsChan;

		if( vpsConnect == 52 ||
			vpsConnect == 53 ||
			vpsConnect == 54 ){	
			dataSize = 4;
			bbDataSize.putInt(dataSize);
			
			baPacket = new byte[1 + headerSize + dataSize + bbCheckSum.limit() + 1];
			
			baPacket[0] = startFlag;
			destPos = 1;
			System.arraycopy(bbDataSize.array(), 0, baPacket, destPos, bbDataSize.limit());
			destPos += bbDataSize.limit();
			System.arraycopy(bbCommand.array(), 0, baPacket, destPos, bbCommand.limit());
			destPos += bbCommand.limit();
			baPacket[destPos] = bDeviceNo;
			destPos++;
			
			bbPositon.putShort((short)ptStart.x);
			System.arraycopy(bbPositon.array(), 0, baPacket, destPos, bbPositon.limit());
			destPos += bbPositon.limit();	
			bbPositon.clear();
			bbPositon.putShort((short)ptStart.y);
			System.arraycopy(bbPositon.array(), 0, baPacket, destPos, bbPositon.limit());
			destPos += bbPositon.limit();
		}

		if (baPacket != null) {
			checkSum = calcChechSum(baPacket, baPacket.length);
			bbCheckSum.putShort(checkSum);
			System.arraycopy(bbCheckSum.array(), 0, baPacket, destPos, bbCheckSum.limit());
		
			baPacket[baPacket.length-1] = endFlag;
		}

		return baPacket;
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		
		if( !isInboundLcdArea(x, y) ) 
			return;
		
		Point pt = calculatedRealDeviceLcdPoint(x, y);
		
		sendTouchDown(m_nHpNo, pt.x, pt.y);
		
//		if( e.getButton() == MouseEvent.BUTTON1 ) {
//			/**
//			 * LCD 영역을 누른 경우
//			 */
//			timeMouseDownSave = e.getWhen();
//			
//			ptMouseDownSave = new Point(x, y);
//     		ptRealDeviceLcdPointSave = calculatedRealDeviceLcdPoint(x, y);
//           
//     		if(timerTouchDownCheck == null){
//     			timerTouchDownCheck = new Timer();
//     		}
//     		/**
//     		 * 300m/s 이내에 Mouse Released 되면 Touch Tap 으로 처리
//     		 * mouseDownAndUp = true
//     		 */
//     		timerTouchDownCheck.schedule(new touchDownTask(), 300, 3600000);
//		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		
		if( !isInboundLcdArea(x, y) ) 
			return;
		
//		if( e.getButton() == MouseEvent.BUTTON1 ) {
//			/**
//			 * LCD 영역에서 Mouse Released 된 경우
//			 */
//			Point ptStart;
//			
//			ptStart = calculatedRealDeviceLcdPoint(x, y);
//			
// 			if(mouseMoving || mouseDownAndUp){
// 				sendTouchUp(m_nHpNo, ptStart.x, ptStart.y);		
// 			}
// 			else{
// 				/**
// 				 * MOUSE DONW/UP 없이 tap 으로 처리
// 				 */
// 				sendTouch(m_nHpNo, ptStart.x, ptStart.y);
// 			}
//		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		
//		if(timeMouseDownSave != 0 && isInboundLcdArea(x, y)){
//			/**
//			 * LCD 영역 안에서 마우스 드래그 중 인 경우
//			 */
//			if(!mouseMoving){
//				/**
//				 * 마우스 드래그 최초 이벤트에서 Touch Swipe 검사
//				 */
//				if(!mouseDownAndUp){
//					sendTouchDown(m_nHpNo, ptRealDeviceLcdPointSave.x, ptRealDeviceLcdPointSave.y);
//				}
//			}
// 			mouseMoving = true;
// 			Point ptMove = calculatedRealDeviceLcdPoint(x, y);
// 			sendTouchMove(m_nHpNo, ptMove.x, ptMove.y, false);
// 		}
//		else if(timeMouseDownSave != 0){ //Out bound LCD area
//			/**
//			 * 마우스 드래그 중 LCD 영역 빢으로 나갈 경우 강제로 LCD 영역 경계에서 드리그 종료 처리 
//			 */
//			timeMouseDownSave = 0;
//			if(timerTouchDownCheck != null){
//				timerTouchDownCheck.cancel();
//				timerTouchDownCheck = null;
//			}
//			Point ptUp = calculatedRealDeviceLcdPoint(x, y);
// 			sendTouchUp(m_nHpNo, ptUp.x, ptUp.y);
//		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean isInboundLcdArea(int x, int y) {
		int width = getWidth();
		int height = getHeight();
		
		if(x >= 5 && x <= width - 10 && y >= 5 && y <= height - 10)
			return true;
		
		return false;
	}
	
	public Point calculatedRealDeviceLcdPoint(int x, int y) {
		Point ptCalc = new Point(0, 0);
		Rectangle rc = new Rectangle(5, 5, getWidth()-10, getHeight()-10);
		
		int width = 720;
		int height = 1280;
		
		int subHeight = rc.height * 160 / (height + 160);
		int zeroPoint = rc.y + subHeight;
		
		double ratioW = (double) width / rc.getWidth();
		double ratioH = 0.0;
		
		ptCalc.x = x - rc.x;
		ptCalc.y = y - zeroPoint;
		
		if(y > zeroPoint) {
			ratioH = (double) height / (rc.getHeight()-subHeight);
		} else {
			ratioH = (double) 160 / subHeight;
		}
		
		ptCalc.x = (int) (ptCalc.x * ratioW);
		ptCalc.y = (int) (ptCalc.y * ratioH);

		return ptCalc;
	}
	
	public class touchDownTask extends TimerTask {
		  /**
		   * Method run.
		   * 
		   * @see java.lang.Runnable#run()
		   */
		  @Override
		  public void run() {
		   
			  if(!mouseMoving){
				  mouseDownAndUp = true;
				  sendTouchDown(m_nHpNo, ptRealDeviceLcdPointSave.x, ptRealDeviceLcdPointSave.y);
				  
				  if(timerTouchDownCheck != null){
					  timerTouchDownCheck.cancel();
				  }
			  }
		  }
	}
	
	private void sendTouch(int id, int x, int y) {
//		byte[] commandPacket = makeVPSDeviceCommand(51, id, new Point(x,y), new Point(0,0));
//    	sendCommandToVPS(Client, commandPacket);
	}
	
	private void sendTouchDown(int id, int x, int y) {
    	byte[] commandPacket = makeVPSDeviceCommand(52, id, new Point(x,y), new Point(0,0));
    	sendCommandToVPS(Client, commandPacket);
    }
	
	private void sendTouchMove(int id, int x, int y, boolean sumTouchMove) {
		if(sumTouchMove){
    		/**
    		 * Swipe 인 경우 MOVE 좌표를 전달하지 않고 누적한다.
    		 */
    		if(movePoints.size() > 0)
    		{
    			Point pt = movePoints.get(movePoints.size()-1);
    			if((Math.abs(pt.x - x) > 10) || (Math.abs(pt.y - y) > 10)){
    				movePoints.add(new Point(x, y));
    			}
    		}
    		else{
    			movePoints.add(new Point(x, y));
    		}
    	}
    	else{
    		movePoints.add(new Point(x, y));
    	}
		
    	byte[] commandPacket = makeVPSDeviceCommand(54, id, new Point(x,y), new Point(0,0));
    	sendCommandToVPS(Client, commandPacket);
    }
    
	private void sendTouchUp(int id, int x, int y) {
    	byte[] commandPacket = makeVPSDeviceCommand(53, id, new Point(x,y), new Point(0,0));
    	sendCommandToVPS(Client, commandPacket);
    }
	
	public byte[] makeVPSDeviceCommand_TouchMove(){
    	short checkSum = 0;
		
		int headerSize = 7, dataSize = 0, destPos = 0;
		byte startFlag = (byte) 0x7F;
		byte endFlag = (byte) 0xEF;
		
		ByteBuffer  bbDataSize = ByteBuffer.wrap(new byte[4]);
		ByteBuffer  bbCommand = ByteBuffer.wrap(new byte[2]);
		ByteBuffer  bbCheckSum = ByteBuffer.wrap(new byte[2]);
		ByteBuffer  bbPositon = ByteBuffer.wrap(new byte[2]);
		byte bDeviceNo = 0;
		byte [] baPacket = null;
		
		bbCommand.putShort((short)54);
		bDeviceNo = (byte)m_nHpNo;
		
		dataSize = movePoints.size() * 4;
		bbDataSize.putInt(dataSize);
		
		baPacket = new byte[1 + headerSize + dataSize + bbCheckSum.limit() + 1];
		
		baPacket[0] = startFlag;
		destPos = 1;
		System.arraycopy(bbDataSize.array(), 0, baPacket, destPos, bbDataSize.limit());
		destPos += bbDataSize.limit();
		System.arraycopy(bbCommand.array(), 0, baPacket, destPos, bbCommand.limit());
		destPos += bbCommand.limit();
		baPacket[destPos] = bDeviceNo;
		destPos++;
		Point ptStart = null;
		for(int i=0; i<movePoints.size(); i++){
			ptStart = movePoints.get(i);

//			ensureTouchCoordinate(ptStart);

			bbPositon.clear();
			bbPositon.putShort((short)ptStart.x);
			System.arraycopy(bbPositon.array(), 0, baPacket, destPos, bbPositon.limit());
			destPos += bbPositon.limit();	
			bbPositon.clear();
			bbPositon.putShort((short)ptStart.y);
			System.arraycopy(bbPositon.array(), 0, baPacket, destPos, bbPositon.limit());
			destPos += bbPositon.limit();
		}
		
		checkSum = calcChechSum(baPacket, baPacket.length);
		bbCheckSum.putShort(checkSum);
		System.arraycopy(bbCheckSum.array(), 0, baPacket, destPos, bbCheckSum.limit());
		
		baPacket[baPacket.length-1] = endFlag;

		movePoints.clear();
		
		return baPacket;
    }
	
	private String shell(String cmd) {
		String s = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
//			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			String result = "";
//			while( (s = stdInput.readLine()) != null ) {
//				result = result + s + "\n";
//			}
//			
//			while( (s = stdError.readLine()) != null ) {
//				System.out.println(s);
//			}
			
			return result;
		} catch(Exception e) {
			System.out.println("Exception here's what I know : ");
			e.printStackTrace();
			return "Exception occurred";
		}
	}

	@Override
	public void onRecordStop() {
		// TODO Auto-generated method stub
		record(m_nHpNo, false);
	}

}
