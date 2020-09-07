package com.onycom.test.finger;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.onycom.test.TFMirroringImageView;

@SuppressWarnings("serial")
public class TFFinger extends JDialog {
	
	private final static String FINGER_IMG = "multi_touch_point.png";
	
	private Image fingerImg;
	private Finger finger;
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	
	private TFMirroringImageView view = null;
	
	public TFFinger(final Window owner, final TFMirroringImageView view, final TFMultiTouchManager manager) {
		super(owner, "Finger", Dialog.ModalityType.MODELESS);
		
		setUndecorated(true);
		
		URL imageURL;
		ImageIcon img;
		
		imageURL = getClass().getResource(FINGER_IMG);
		img = new ImageIcon(imageURL);
		fingerImg = img.getImage();
		
		setSize( fingerImg.getWidth(null), fingerImg.getHeight(null) );
		
		this.view = view;
		finger = new Finger();
		
		addMouseListener(manager);
		addMouseMotionListener(manager);
		
		Color bgColor = getBackground();
		setBackground( new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0) );
		
		add(finger);
		
		setVisible(false);
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
					manager.hideFinger();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public JPanel getFinger() {
		return finger;
	}
	
	public void setLCDArea(Rectangle bounds) {
		Point loc = new Point(bounds.x, bounds.y);
		SwingUtilities.convertPointToScreen(loc, view);
		
		this.bounds.x = loc.x;
		this.bounds.y = loc.y;
		this.bounds.width = bounds.width;
		this.bounds.height = bounds.height;
	}
	
	public boolean isLCDArea(int x, int y) {
		Point pt = new Point( x + (getWidth()/2), y + (getHeight()/2) );
		
		SwingUtilities.convertPointFromScreen(pt, view);
		
		return view.isLcdArea(pt.x, pt.y);
	}
	
	@Override
	public void setLocation(int x, int y) {
		// TODO Auto-generated method stub
		super.setLocation(x, y);
	}
	
	public Point getScreenPoint() {
		return getLocation();
	}
	
	public Point getScreenMovePoint(Point pt) {
		Point loc = getLocation();
		return new Point(pt.x - loc.x, pt.y - loc.y);
	}
	
	public Point getTouchPoint() {
		Point loc = getCenterPoint();
		SwingUtilities.convertPointFromScreen(loc, view);
		return view.calculateRealDeviceLcdPoint(loc.x, loc.y);
	}
	
	public Point getCenterPoint() {
		return new Point( (int)getBounds().getCenterX(), (int)getBounds().getCenterY() );
	}
	
	public Point getLCDMovePoint(Point pt) {
		Point loc = getCenterPoint();
		SwingUtilities.convertPointFromScreen(loc, view);
		Point lastPt = view.calculateRealDeviceLcdPoint(loc.x, loc.y);
		int moveXPoint = 0;
		int moveYPoint = 0;
		
		if(lastPt.x > pt.x) {
			moveXPoint = lastPt.x - pt.x;
		} else if(lastPt.x < pt.x) {
			moveXPoint = pt.x - lastPt.x;
		}
		
		if(lastPt.y > pt.y) {
			moveYPoint = lastPt.y - pt.y;
		} else if(lastPt.y < pt.y) {
			moveYPoint = pt.y - lastPt.y;
		}
		
		return new Point(moveXPoint, moveYPoint);
	}
	
	class Finger extends JPanel {
		
		public Finger() {
			super();
			
			setSize( fingerImg.getWidth(null), fingerImg.getHeight(null) );
			setBackground(null);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			Graphics2D g2d = (Graphics2D)g;
			g2d.drawImage(fingerImg, 0, 0, null);
		}
		
	}

}
