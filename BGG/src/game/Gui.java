package game;

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
	
	public static void intializeGraphics(){
		gui = new JFrame("The Bad Guys Game");
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setVisible(true);;
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.add(paintComponent);
		gui.addMouseListener(mouseEventsHandler);
		gui.addMouseMotionListener(mouseEventsHandler);
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
		
		public void mouseMoved(MouseEvent event){
			gui.repaint();
		}
		
		public void mouseDragged(MouseEvent event){
			gui.repaint();
		}
	};
}
