package com.onycom.test.finger;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import com.onycom.test.TFMirroringImageView;

public class TFMultiTouchManager implements MouseListener, MouseMotionListener {
	
	private Point ptMousePressed = new Point(-1, -1);
	
	private TFFinger finger_1 = null;
	private TFFinger finger_2 = null;
	
	private TFMirroringImageView view = null;
	
	public TFMultiTouchManager(Window owner, TFMirroringImageView view) {
		// TODO Auto-generated constructor stub
		finger_1 = new TFFinger(owner, view, this);
		finger_2 = new TFFinger(owner, view, this);
		
		this.view = view;
	}
	
	public void setLCDArea(Rectangle bounds) {
		finger_1.setLCDArea(bounds);
		finger_2.setLCDArea(bounds);
	}
	
	public void showFinger(int x, int y) {
		TFFinger finger = null;
		
		Point pt = new Point(x, y);
		SwingUtilities.convertPointToScreen(pt, view);
		
		if(finger_1.isVisible() == false) {
			finger = finger_1;
		} else if(finger_1.isVisible() && finger_2.isVisible() == false) {
			finger = finger_2;
		} else {
			finger = finger_1;
			finger_2.setVisible(false);
		}
		
		if(finger != null) {
			finger.setLocation( pt.x - (finger_2.getWidth()/2), pt.y - (finger_2.getHeight()/2) );
			finger.setVisible(true);
		}
		
		System.out.println("Show Finger : " + x + ", " + y + ", " + finger_1.isVisible() + ", " + finger_2.isVisible());
	}
	
	public void hideFinger() {
		finger_1.setVisible(false);
		finger_2.setVisible(false);
	}
	
	public boolean isMultiTouchMode() {
		return finger_1.isVisible();
	}
	
	public boolean isMultiTouchActionMode() {
		return finger_1.isVisible() && finger_2.isVisible();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		TFFinger finger = (TFFinger)e.getComponent();
		
		if( finger.getCursor().equals( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) ) == false ) {
			finger.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		}
		
		int newPointX = e.getXOnScreen() - ptMousePressed.x;
		int newPointY = e.getYOnScreen() - ptMousePressed.y;
		
		if( finger.isLCDArea(newPointX, newPointY) == false ) {
			return;
		}
		
		Point movePt = finger.getScreenMovePoint( new Point(newPointX, newPointY) );
		if(movePt.x == 0 && movePt.y == 0) {
			return;
		}
		
		movePt.x *= -1;
		movePt.y *= -1;
		
		if( finger.equals(finger_1) ) {
			if( finger_2.isVisible() ) {
				Point screenPt = finger_2.getScreenPoint();
				screenPt.x += movePt.x;
				screenPt.y += movePt.y;
				
				if( finger_2.isLCDArea(screenPt.x, screenPt.y) ) {
					 finger_2.setLocation(screenPt);
				}
			}
		} else {
			Point screenPt = finger_1.getScreenPoint();
			screenPt.x += movePt.x;
			screenPt.y += movePt.y;
			
			if( finger_1.isLCDArea(screenPt.x, screenPt.y) ) {
				finger_1.setLocation(screenPt);
			}
		}
		
		finger.setLocation(newPointX, newPointY);
		
		if( isMultiTouchActionMode() ) {
			Point touchPt1 = finger_1.getTouchPoint();
			Point touchPt2 = finger_2.getTouchPoint();
			
			view.multiTouch_move(touchPt1.x, touchPt1.y, touchPt2.x, touchPt2.y);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		TFFinger finger = (TFFinger)e.getComponent();
		finger.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		
		ptMousePressed.setLocation( e.getX(), e.getY() );
		
		if( isMultiTouchActionMode() ) {
			Point touchPt1 = null;
			Point touchPt2 = null;
			
			if( finger.equals(finger_1) ) {
				touchPt1 = finger.getTouchPoint();
				touchPt2 = finger_2.getTouchPoint();
			} else {
				touchPt1 = finger_1.getTouchPoint();
				touchPt2 = finger.getTouchPoint();
			}
			
			view.multiTouch_down(touchPt1.x, touchPt1.y, touchPt2.x, touchPt2.y);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		TFFinger finger = (TFFinger)e.getComponent();
		finger.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		
		if( isMultiTouchActionMode() ) {
			Point touchPt1 = null;
			Point touchPt2 = null;
			
			if( finger.equals(finger_1) ) {
				touchPt1 = finger.getTouchPoint();
				touchPt2 = finger_2.getTouchPoint();
			} else {
				touchPt1 = finger_1.getTouchPoint();
				touchPt2 = finger.getTouchPoint();
			}
			
			view.multiTouch_up(touchPt1.x, touchPt1.y, touchPt2.x, touchPt2.y);
			
			hideFinger();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		TFFinger finger = (TFFinger)e.getComponent();
		finger.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		TFFinger finger = (TFFinger)e.getComponent();
		finger.setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );
	}

}
