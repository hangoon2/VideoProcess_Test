package com.onycom.test;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class VPSTest extends JFrame {
	
	public static final byte START_FLAG = 0x7F;
	public static final byte END_FLAG = (byte)0xEF;
	
//	SocketChannel client = null;
	
	Socket vpsSock = null;
	OutputStream writer = null;
	DataInputStream reader = null;
	
	private JButton startBtn1 = null;
	private JButton startBtn2 = null;
	private JButton startBtn3 = null;
	private JButton startBtn4 = null;
	private JButton startBtn5 = null;
	private JButton startBtn6 = null;
	private JButton startBtn7 = null;
	private JButton startBtn8 = null;
	private JButton startBtn9 = null;
	private JButton startBtn10 = null;	
	private JButton startBtn11 = null;
	private JButton startBtn12 = null;
	private JButton startBtn13 = null;
	private JButton startBtn14 = null;
	private JButton startBtn15 = null;
	private JButton startBtn16 = null;
	private JButton startBtn17 = null;
	private JButton startBtn18 = null;
	private JButton startBtn19 = null;
	private JButton startBtn20 = null;
	
	private ArrayList<Client> connectLists = new ArrayList<Client>();
	
	static String[] gs_deviceIDs = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
	static Process[] execLists = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
	
	private VpsDistributeThread distributeThread = null;
	private Thread socketMonitorThread = null;
	
	private Process adbProcess = null;
	
	public VPSTest() {
		// TODO Auto-generated constructor stub
		setTitle("Client");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout( new FlowLayout() );
		
		startBtn1 = new JButton("Device 1");
		add(startBtn1);
		
		startBtn2 = new JButton("Device 2");
		add(startBtn2);
		
		startBtn3 = new JButton("Device 3");
		add(startBtn3);
		
		startBtn4 = new JButton("Device 4");
		add(startBtn4);
		
		startBtn5 = new JButton("Device 5");
		add(startBtn5);
		
		startBtn6 = new JButton("Device 6");
		add(startBtn6);
		
		startBtn7 = new JButton("Device 7");
		add(startBtn7);
		
		startBtn8 = new JButton("Device 8");
		add(startBtn8);
		
		startBtn9 = new JButton("Device 9");
		add(startBtn9);
		
		startBtn10 = new JButton("Device 10");
		add(startBtn10);
		
		startBtn11 = new JButton("Device 11");
		add(startBtn11);
		
		startBtn12 = new JButton("Device 12");
		add(startBtn12);
		
		startBtn13 = new JButton("Device 13");
		add(startBtn13);
		
		startBtn14 = new JButton("Device 14");
		add(startBtn14);
		
		startBtn15 = new JButton("Device 15");
		add(startBtn15);
		
		startBtn16 = new JButton("Device 16");
		add(startBtn16);
		
		startBtn17 = new JButton("Device 17");
		add(startBtn17);
		
		startBtn18 = new JButton("Device 18");
		add(startBtn18);
		
		startBtn19 = new JButton("Device 19");
		add(startBtn19);
		
		startBtn20 = new JButton("Device 20");
		add(startBtn20);
		
		setMinimumSize(new Dimension(300, 200));		
		setPreferredSize(new Dimension(600, 200));
		
//		initializeDevices();
		
//		for(int i = 0; i < gs_deviceIDs.length; i++) {
//			JButton button = getButton(i+1);
			
//			if(gs_deviceIDs[i] != null)
//				button.setEnabled(true);
//			else
//				button.setEnabled(false);
//		}
		
		startBtn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				String cmd = "adb forward tcp:2013 tcp:2013";
//				shell(cmd);
//				
//				cmd = "adb forward tcp:8801 tcp:8801";
//				shell(cmd);
//				
//				cmd = "adb forward tcp:8821 tcp:8821";
//				shell(cmd);
//				
//				cmd = "adb shell CLASSPATH=/data/local/tmp/com.onycom.mirror.jar app_process / com.onycom.mirror.Main 1 8801 8821 0 1";
//				adbProcess = shell(cmd);
//				
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				openDevice(1);
			}
		});
		
		startBtn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(2);
			}
		});
		
		startBtn3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(3);
			}
		});
		
		startBtn4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(4);
			}
		});
		
		startBtn5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(5);
			}
		});
		
		startBtn6.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(6);
			}
		});
		
		startBtn7.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(7);
			}
		});
		
		startBtn8.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(8);
			}
		});
		
		startBtn9.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(9);
			}
		});
		
		startBtn10.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(10);
			}
		});
		
		startBtn11.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(11);
			}
		});
		
		startBtn12.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(12);
			}
		});
		
		startBtn13.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(13);
			}
		});
		
		startBtn14.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(14);
			}
		});
		
		startBtn15.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(15);
			}
		});
		
		startBtn16.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(16);
			}
		});
		
		startBtn17.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(17);
			}
		});
		
		startBtn18.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(18);
			}
		});
		
		startBtn19.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(19);
			}
		});
		
		startBtn20.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openDevice(20);
			}
		});

		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				for(int i = 0; i < connectLists.size(); i++) {
					if(connectLists.get(i).isVisible())
						connectLists.get(i).dispose();
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
//					client.close();
					vpsSock.close();
					reader.close();
					writer.close();					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for(int i = 0; i < 8; i++) {
					if(execLists[i] != null)
						execLists[i].destroy();
				}
			}
			
		});
	}
	
	public void openGuest(Client device) {
		connectLists.add(device);
	}
	
	public void openDevice(int nHpNo) {
		ByteBuffer packet = ByteBuffer.wrap( makeOnyPacketStartDevice(nHpNo, true) );
		try {
			writer.write(packet.array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Client device = new Client(VPSTest.this, nHpNo, false, 0);
		connectLists.add(device);
		device.setVisible(true);
		
		JButton button = getButton(nHpNo);
		button.setEnabled(false);
		
//		device.connectAgent();
	}
	
	public void closeDevice(int nHpNo) {
		JButton button = getButton(nHpNo);
		button.setEnabled(true);
		
		if(nHpNo ==1 && adbProcess != null) {
			adbProcess.destroy();
			adbProcess = null;
		}
	}
	
	private JButton getButton(int nHpNo) {
		if(nHpNo == 1)
			return startBtn1;
		else if(nHpNo == 2)
			return startBtn2;
		else if(nHpNo == 3)
			return startBtn3;
		else if(nHpNo == 4)
			return startBtn4;
		else if(nHpNo == 5)
			return startBtn5;
		else if(nHpNo == 6)
			return startBtn6;
		else if(nHpNo == 7)
			return startBtn7;
		else if(nHpNo == 8)
			return startBtn8;
		else if(nHpNo == 9)
			return startBtn9;
		else if(nHpNo == 10)
			return startBtn10;		
		else if(nHpNo == 11)
			return startBtn11;		
		else if(nHpNo == 12)
			return startBtn12;
		else if(nHpNo == 13)
			return startBtn13;
		else if(nHpNo == 14)
			return startBtn14;
		else if(nHpNo == 15)
			return startBtn15;
		else if(nHpNo == 16)
			return startBtn16;
		else if(nHpNo == 17)
			return startBtn17;
		else if(nHpNo == 18)
			return startBtn18;
		else if(nHpNo == 19)
			return startBtn19;
		
		return startBtn20;
	}
	
	public void stopTest(int nHpNo) {
		try {
			ByteBuffer packet = ByteBuffer.wrap( makeOnyPacketStartDevice(nHpNo, false) );
//			client.write(packet);
			writer.write(packet.array());
			
			if(execLists[nHpNo-1] != null) {
				execLists[nHpNo-1].destroy();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startTest() {
		try {
			int port = TFIniFile.getInstance().getInt("Stream", "TCPPort", 10001);
			vpsSock = new Socket(InetAddress.getByName("192.168.1.38"), port);
			writer = vpsSock.getOutputStream();
			reader = new DataInputStream(vpsSock.getInputStream());
			
			rxMsgThread();
			startMonitor();			
			
			ByteBuffer packet = ByteBuffer.wrap( makeOnyPacketID("MOBILECONTROL") );
			writer.write(packet.array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private final byte[] makeOnyPacketID(String id) {
		ByteBuffer ret = null;
		
		try{
			ret = ByteBuffer.allocate(8 + id.getBytes().length + 3);
			
			// start flag
			ret.put(START_FLAG);
			// data size
			ret.putInt(id.getBytes().length);
			// command code
			ret.putShort((short)30000);
			// device no
			ret.put((byte)1);
			// data
			try {
				ret.put(id.getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
		} catch (Exception e) {
			
		}
		
		return ret.array();
	}
	
	private final byte[] makeOnyPacketStartDevice(int nHpNo, boolean start) {
		try {
			String videoType = "usb";
			int size = (4 + videoType.getBytes().length);
			ByteBuffer ret = ByteBuffer.allocate(8 + size + 3);
			
			// start flag
			ret.put(START_FLAG);
			// data size
			ret.putInt(size);
			// command code
			ret.putShort(start ? (short)1 : (short)2);
			// device no
			ret.put((byte)nHpNo);
			
			ret.putShort((short)720);
			ret.putShort((short)1280);
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
			ret.put(END_FLAG);
			
			return ret.array();
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
//	public final byte[] makeOnyPacket(int nHpNo, int cmd, byte[] data, int len) { 
//		ByteBuffer ret = ByteBuffer.allocate(8 + len + 3);
//		
//		// start flag
//		ret.put(START_FLAG);
//		// data size
//		ret.putInt(len);
//		// command code
//		ret.putShort((short)cmd);
//		// device no
//		ret.put((byte)nHpNo);
//		// data
//		if(len > 0)
//			ret.put(data);
//		
//		long sum=0;
//		ret.position(1);
//		//sum short values to data
//		while (ret.position() < ret.capacity() -4)
//			sum += (long)ret.getShort();
//		//sum last byte
//		if (ret.position() != ret.capacity() -3)
//			sum += (long)(ret.get() & 0xff);
//		
//		sum = (sum >> 16) + (sum & 0xffff);
//		sum += (sum >> 16);
//
//		//checksum
//		ret.putShort((short) ~sum);
//		//end flag
//		ret.put(END_FLAG);
//		
//		return ret.array();
//	}
	
//	private static boolean initializeDevices() {
//		String[] exec = {"adb", "devices"}; 
//		
//		try {
//			Process process = new ProcessBuilder(exec).start();
//			InputStream is = process.getInputStream();
//			InputStreamReader isr = new InputStreamReader(is);
//			BufferedReader br = new BufferedReader(isr);
//			
//			String line;
//			while( (line = br.readLine()) != null ) {
//				String[] infos = line.split("\t");
//				if( infos.length == 2 && infos[1].equals("device") ) {
//					if(gs_deviceIDs[0] == null) {
//						gs_deviceIDs[0] = infos[0];
//					} else if(gs_deviceIDs[3] == null) {
//						gs_deviceIDs[3] = infos[0];
//					} else if(gs_deviceIDs[6] == null) {
//						gs_deviceIDs[6] = infos[0];
//					} else {
//						for(int i = 0; i < 8; i++) {
//							if(gs_deviceIDs[i] == null) {
//								gs_deviceIDs[i] = infos[0];
//								break;
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			return false;
//		}
//		
//		return true;
//	}
	
//	public static boolean startDevice(final int nHpNo) {
//		String mirrorPort = String.valueOf(8800+nHpNo);
//		String controlPort = String.valueOf(8810+nHpNo);
//		
//		String[] execMirror = {"adb", "forward", "tcp:"+mirrorPort, "tcp:"+mirrorPort};
//		
//		try {
//			new ProcessBuilder(execMirror).start();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			return false;
//		}
//		
//		String[] execControl = {"adb", "forward", "tcp:"+controlPort, "tcp:"+controlPort};
//		
//		try {
//			new ProcessBuilder(execControl).start();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			return false;
//		}
//		
//		final String[] exec= {"adb", "shell", "/data/local/tmp/nxptc_21", String.valueOf(nHpNo), mirrorPort, controlPort};
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					Process process = new ProcessBuilder(exec).start();
//					execLists[nHpNo-1] = process;
//					
//					InputStream is = process.getInputStream();
//					InputStreamReader isr = new InputStreamReader(is);
//					BufferedReader br = new BufferedReader(isr);
//					
//					String line;
//					while( (line = br.readLine()) != null ) {
//						String[] infos = line.split(" ");
//						if(infos.length > 1) {
//							if(infos[0].equals("[mirror]") || infos[0].equals("[control]") || infos[0].equals("setOnOff")) 
//							{
//								System.out.println(line);
//							}
//						}						
//					}
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//				}
//			}
//		}).start();
//		
//		return true;
//	}
	
//	public static boolean startDevice(final int nHpNo) {
//		if(gs_deviceIDs[nHpNo-1] == null) {
//			return false;
//		}
//		
//		String mirrorPort = String.valueOf(8800+nHpNo);
//		String controlPort = String.valueOf(8810+nHpNo);
//		
//		String[] execMirror = {"adb", "-s", gs_deviceIDs[nHpNo-1], "forward", "tcp:"+mirrorPort, "tcp:"+mirrorPort};
//		
//		try {
//			new ProcessBuilder(execMirror).start();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			return false;
//		}
//		
//		String[] execControl = {"adb", "-s", gs_deviceIDs[nHpNo-1], "forward", "tcp:"+controlPort, "tcp:"+controlPort};
//		
//		try {
//			new ProcessBuilder(execControl).start();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			return false;
//		}
//		
//		final String[] exec= {"adb", "-s", gs_deviceIDs[nHpNo-1], "shell", "/data/local/tmp/nxptc_24", String.valueOf(nHpNo), mirrorPort, controlPort};
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					Process process = new ProcessBuilder(exec).start();
//					execLists[nHpNo-1] = process;
//					
//					InputStream is = process.getInputStream();
//					InputStreamReader isr = new InputStreamReader(is);
//					BufferedReader br = new BufferedReader(isr);
//					
//					String line;
//					while( (line = br.readLine()) != null ) {
//						String[] infos = line.split(" ");
//						if(infos.length > 1) {
//							if(infos[0].equals("[mirror]") || infos[0].equals("[control]") || infos[0].equals("setOnOff")) 
//							{
//								System.out.println(line);
//							}
//						}						
//					}
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//				}
//			}
//		}).start();
//		
//		return true;
//	}
	
	protected void rxMsgThread() {	
		//check null
		if (distributeThread != null)
			distributeThread.stop();
		//parse data, make packet, distribute packet to devices
		distributeThread = new VpsDistributeThread(writer);		
		
		distributeThread.remoteStart();
	}//rxMsgThread
	
	private void stopMonitor() {
		if (socketMonitorThread != null) {
			socketMonitorThread.interrupt();
			socketMonitorThread = null;
		}		
	}
	
	protected void startMonitor() {
		stopMonitor();
		
		socketMonitorThread = new Thread(new Runnable() {
			@Override
			public void run() {	
				int ret;
				
				try {
					while (true) {
						Thread.sleep(1);//throw, exit
					
						//check data
						ret = reader.available();//throw, exit
						//check size
						if (ret < 1)
							//no data
							continue;
						
						byte [] buf = new byte [ret];
						//read data
						reader.readFully(buf);//throw, exit

						distributeThread.addData(buf);
					}//while : interrupt				
				}//try read
				catch (Exception e) {
				}
			}//run
		});//socketMonitorThread
		
		socketMonitorThread.start();
	}//startMonitor
	
	private Process shell(String cmd) {
		String s = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			String result = "";
//			while( (s = stdInput.readLine()) != null ) {
//				System.out.println("Read Line : " + s);
//				result = result + s + "\n";
//			}
//			
//			while( (s = stdError.readLine()) != null ) {
//				System.out.println(s);
//			}
			
			return p;
		} catch(Exception e) {
			System.out.println("Exception here's what I know : ");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				VPSTest test = new VPSTest();
				test.startTest();
				
				test.pack();
				test.setVisible(true);
			}
		});
	}

}
