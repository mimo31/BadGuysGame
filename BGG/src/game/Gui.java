package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

public final class Gui {
	
	public static JFrame gui;
	public static Point mousePosition = new Point(0, 0);
	
	public static Dimension getContentSize() {
		return gui.getContentPane().getSize();
	}
	
	public static void intializeGraphics(){
		gui = new JFrame("The Bad Guys Game");
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setVisible(true);;
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.add(paintComponent);
		gui.getContentPane().addMouseListener(mouseEventsHandler);
		gui.getContentPane().addMouseMotionListener(mouseEventsHandler);
	}
	
	@SuppressWarnings("serial")
	public static JComponent paintComponent = new JComponent(){
		
		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			try {
				Point mouseScreenPosition = MouseInfo.getPointerInfo().getLocation();
				Point paneScreenPosition = gui.getContentPane().getLocationOnScreen();
				Painting.paint(g, gui.getContentPane().getSize(), new Point(mouseScreenPosition.x - paneScreenPosition.x, mouseScreenPosition.y - paneScreenPosition.y));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static MouseInputAdapter mouseEventsHandler = new MouseInputAdapter() {
		
		public void mousePressed(MouseEvent event) {
			Dimension contentSize = getContentSize();
			Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
			float vecX = event.getX() - barrelCenter.x;
			float vecY = event.getY() - barrelCenter.y;
			float factor = (float)(contentSize.width / (float)32 / Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2)));
			Point firePoint = new Point((int)(barrelCenter.x + vecX * factor), (int)(barrelCenter.y + vecY * factor));
			Bullet bullet = new Bullet(firePoint.x / (float)contentSize.width, firePoint.y / (float)contentSize.height, vecX / (float)contentSize.width, vecY / (float)contentSize.height);
			Main.bullets.add(bullet);
		}
		
		public void mouseMoved(MouseEvent event){
			gui.repaint();
		}
		
		public void mouseDragged(MouseEvent event){
			gui.repaint();
		}
	};
}
