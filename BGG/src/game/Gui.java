package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

public final class Gui {

	public static JFrame gui;

	public static Dimension getContentSize() {
		return gui.getContentPane().getSize();
	}

	public static Point getMousePanePosition() {
		Point mouseScreenPosition = MouseInfo.getPointerInfo().getLocation();
		Point paneScreenPosition = gui.getContentPane().getLocationOnScreen();
		return new Point(mouseScreenPosition.x - paneScreenPosition.x, mouseScreenPosition.y - paneScreenPosition.y);
	}

	public static void intializeGraphics() {
		IO.logStartSectionTag("GUI");
		IO.log("Initializing GUI");
		gui = new JFrame("The Bad Guys Game");
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.add(paintComponent);
		gui.getContentPane().addMouseListener(mouseEventsHandler);
		gui.getContentPane().addMouseMotionListener(mouseEventsHandler);
		gui.addKeyListener(keysEventsHandler);
		gui.repaint();
		IO.log("GUI initialized");
		IO.logEndSectionTag("GUI");
	}

	@SuppressWarnings("serial")
	private static JComponent paintComponent = new JComponent() {

		@Override
		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			try {
				Main.currentScreen.paint(g, gui.getContentPane().getSize(), getMousePanePosition());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	};

	private static MouseInputAdapter mouseEventsHandler = new MouseInputAdapter() {

		@Override
		public void mousePressed(MouseEvent event) {
			Main.currentScreen.mousePressed(event);
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			Main.currentScreen.mouseReleased(event);
		}
		
		@Override
		public void mouseMoved(MouseEvent event) {
			Main.currentScreen.mouseMoved(event);
			gui.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			Main.currentScreen.mouseDragged(event);
			gui.repaint();
		}
	};

	private static KeyAdapter keysEventsHandler = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent event) {
			Main.currentScreen.keyPressed(event);
		}
	};
}
