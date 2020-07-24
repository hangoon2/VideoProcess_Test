package com.onycom.test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.onycom.test.models.VideoData;

@SuppressWarnings("serial")
public class TFMirroringImageView extends JPanel implements MouseListener , MouseMotionListener{

	BufferedImage image = null;
	Rectangle rc = null;
	int count = 0;
	
	final int nHpNo;
	final boolean subScreen;
	
	int startX = 0;
	int startY = 0;
	int imageWidth = 0;
	int imageHeight = 0;
	
	int deviceWidth = 0;
	int deviceHeight = 0;
	
	private int lastX = 0;
	private int lastY = 0;
	
	private boolean pressed = false;
	
	private DataOutputStream bos = null;
	
	private int frameCount = 0;
	private long startTime = 0;
	
	public TFMirroringImageView(final int nHpNo, final boolean subScreen, final int deviceWidth, final int deviceHeight) {
		// TODO Auto-generated constructor stub
		setBackground(Color.white);		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.nHpNo = nHpNo;
		this.subScreen = subScreen;
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
	}
	
	private long getTickCount() {
		return new Date().getTime();
	}
	
	public void setVideoData(VideoData data) {
		if( data.isKeyFrame ) {
			image = null;
		}
		
		frameCount++;
		
		if(startTime == 0) {
			startTime = getTickCount();
		} else {
			long curr = getTickCount();
			if(curr - startTime >= 1000) {
				//System.out.println("Client Frame Count : " + frameCount);
				startTime = 0;
				frameCount = 0;
			}
		}
		
		if(image == null) {
			ByteArrayInputStream stream = new ByteArrayInputStream(data.videoData);
			try {
				rc = data.rc;
				image = ImageIO.read(stream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Graphics g = image.getGraphics();
			
			ByteArrayInputStream stream = new ByteArrayInputStream(data.videoData);
			try {
				BufferedImage img = ImageIO.read(stream);
				g.drawImage(img, data.rc.x, data.rc.y, null);
				g.dispose();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		repaint();
	}
	
	public void stopMirroring() {
		image = null;
		repaint();
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
	    if((originalImage.getWidth() == width) && (originalImage.getHeight() == height)){
	    	return originalImage;
	    }

	    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = resizedImage.createGraphics();

//	    if (imageType == BufferedImage.TYPE_INT_RGB) {
	    	g.setComposite(AlphaComposite.Src);
	    	g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    	g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//	    }

	    g.drawImage(originalImage, 0, 0, width, height, this);
	    g.dispose();

	    return resizedImage;
	}
	
	 BufferedImage getScaledImage(BufferedImage image, int width, int height){
         
         GraphicsConfiguration gc = getGraphicsConfiguration();
         BufferedImage result = gc.createCompatibleImage(width, height, image.getColorModel().getTransparency());
         Graphics2D g = result.createGraphics();
        
         double w = image.getWidth();
         double h = image.getHeight();
         g.scale((double)width/w, (double)height/h);
         g.drawRenderedImage(image, null);
         g.dispose();
        
         return result;
 }
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		
		if(image != null) {
			int width = getWidth();
			int height = getHeight();
			
			int imgWidth = rc.width;
			int imgHeight = rc.height;
			
			if(rc.width > rc.height) {
				imgHeight = height;
				imgWidth = (int) ((float)height / (float)rc.height * (float)rc.width); 
			} else {
				imgHeight = height;
				imgWidth = (int) ((float)height / (float)rc.height * (float)rc.width);
//				imgHeight = (int) ((float)width / (float)rc.width * (float)rc.height);
			}
			
			int x = (width - imgWidth) / 2;
			int y = (height - imgHeight) / 2;
			
			startX = x;
			startY = y;
			imageWidth = imgWidth;
			imageHeight = imgHeight;
			
			BufferedImage drawImg = resizeImage(image, imgWidth, imgHeight);
			
			g.drawImage(drawImg, x, y, null);
		}
	}
	
	public void setDataOutputStream(DataOutputStream bos) {
		this.bos = bos;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if( !isLcdArea(e.getX(), e.getY()) ) return;
		
		int x = e.getX() - startX;
		int y = e.getY() - startY;
		
		if(x < 0 || y < 0) return;
		
		int realX = (x * deviceWidth) / imageWidth;
		int realY = (y * deviceHeight) / imageHeight;
		
		lastX = realX;
		lastY = realY;
		
		byte[] data = makeVPSDeviceCommand(subScreen ? 62 : 52, realX, realY);
		if(bos != null) {
			try {
				bos.write(data);
				bos.flush();
			} catch(Exception err) {
				err.printStackTrace();
			}
		}
		
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if( !isLcdArea(e.getX(), e.getY()) ) return;
		
		if(pressed) {
			int x = e.getX() - startX;
			int y = e.getY() - startY;
			
			if(x < 0 || y < 0) return;
			
			int realX = (x * deviceWidth) / imageWidth;
			int realY = (y * deviceHeight) / imageHeight;
			
			byte[] data = makeVPSDeviceCommand(subScreen ? 63 : 53, realX, realY);
			if(bos != null) {
				try {
					bos.write(data);
					bos.flush();
				} catch(Exception err) {
					err.printStackTrace();
				}
			}
			
			lastX = 0;
			lastY = 0;			
			pressed = false;
		}
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
		if(pressed) {
			if( !isLcdArea(e.getX(), e.getY()) ) {
				byte[] data = makeVPSDeviceCommand(subScreen ? 63 : 53, lastX, lastY);
				if(bos != null) {
					try {
						bos.write(data);
						bos.flush();
					} catch(Exception err) {
						err.printStackTrace();
					}
				}
				pressed = false;
				return;
			}
			
			int x = e.getX() - startX;
			int y = e.getY() - startY;
			
			if(x < 0 || y < 0) return;
			
			int realX = (x * deviceWidth) / imageWidth;
			int realY = (y * deviceHeight) / imageHeight;
			
			lastX = realX;
			lastY = realY;
			
			byte[] data = makeVPSDeviceCommand(subScreen ? 64 : 54, realX, realY);
			if(bos != null) {
				try {
					bos.write(data);
					bos.flush();
				} catch(Exception err) {
					err.printStackTrace();
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	private boolean isLcdArea(int x, int y) {
		if(imageWidth == 0 || imageHeight == 0) return false;
		
		if(x < startX || y < startY) return false;
		
		if(x > startX + imageWidth) return false;
		
		if(y > startY + imageHeight) return false;
		
		return true;
	}
	
	public byte[] makeVPSDeviceCommand(int cmd, int x, int y) {
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
		
		bbCommand.putShort((short)cmd);
		bDeviceNo = (byte)nHpNo;
		
		lastX = x;
		lastY = y;

		if( cmd == 52 || cmd == 53 || cmd == 54 || cmd == 62 || cmd == 63 || cmd == 64 ) {	
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
			
			bbPositon.putShort((short)x);
			System.arraycopy(bbPositon.array(), 0, baPacket, destPos, bbPositon.limit());
			destPos += bbPositon.limit();	
			bbPositon.clear();
			bbPositon.putShort((short)y);
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
	
}
