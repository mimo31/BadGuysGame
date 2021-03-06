package com.github.mimo31.badguysgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import com.github.mimo31.badguysgame.io.IOBase;
import com.github.mimo31.badguysgame.io.Logging;

public final class Gui {

	public static JFrame gui;
	public static int maxScreenRefreshRate;
	public static int refreshRate;

	public static Dimension getContentSize() {
		return gui.getContentPane().getSize();
	}

	public static Point getMousePanePosition() {
		Point mouseScreenPosition = MouseInfo.getPointerInfo().getLocation();
		Point paneScreenPosition = gui.getContentPane().getLocationOnScreen();
		return new Point(mouseScreenPosition.x - paneScreenPosition.x, mouseScreenPosition.y - paneScreenPosition.y);
	}

	public static void intializeGraphics() {
		Logging.logStartSectionTag("GUI");
		Logging.log("Initializing GUI");
		int refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
		if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
			maxScreenRefreshRate = 60;
		}
		else {
			maxScreenRefreshRate = refreshRate;
		}
		gui = new JFrame("The Bad Guys Game");
		try {
			gui.setIconImage(ImageIO.read(ClassLoader.getSystemResource("com/github/mimo31/badguysgame/resources/Icon.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.addWindowListener(windowEvents);
		gui.add(paintComponent);
		gui.getContentPane().addMouseListener(mouseEventsHandler);
		gui.getContentPane().addMouseMotionListener(mouseEventsHandler);
		gui.getContentPane().addMouseWheelListener(mouseEventsHandler);
		gui.addKeyListener(keysEventsHandler);
		gui.repaint();
		Logging.log("GUI initialized");
		Logging.logEndSectionTag("GUI");
	}

	private static void drawDebugMode(Graphics2D g, Dimension contentSize) {
		g.setColor(Color.black);
		StringDraw.drawMaxString(g, contentSize.height / 256, String.valueOf(refreshRate) + " fps", new Rectangle(contentSize.width - contentSize.width / 16, 0, contentSize.width / 16, contentSize.height / 16));
	}
	
	@SuppressWarnings("serial")
	private static JComponent paintComponent = new JComponent() {

		@Override
		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			try {
				Dimension contentSize = gui.getContentPane().getSize();
				Main.currentScreen.paint(g, contentSize, getMousePanePosition());
				Achievement.paint(g, contentSize);
				if (Main.inDebugMode) {
					drawDebugMode(g, contentSize);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	};

	private static WindowAdapter windowEvents = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent event) {
			try {
				Main.running = false;
				Main.updateThread.join();
				Main.currentScreen.getCloseReady();
				IOBase.save();
				Logging.log("Exiting");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

	};

	private static MouseInputAdapter mouseEventsHandler = new MouseInputAdapter() {

		@Override
		public void mousePressed(MouseEvent event) {
			try {
				Main.currentScreen.mousePressed(event);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			Main.currentScreen.mouseReleased(event);
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			Main.currentScreen.mouseMoved(event);
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			Main.currentScreen.mouseDragged(event);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			Main.currentScreen.mouseWheelMoved(event);
		}
	};

	private static KeyAdapter keysEventsHandler = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent event) {
			if (Main.debugging) {
				if (event.getKeyCode() == KeyEvent.VK_Q) {
					Main.money += 1000;
					Main.maxReachedStage = Main.stages.length / 5;
					for (int i = 0; i < Achievement.achievements.length; i++) {
						Achievement.achieve(i);
					}
				}
				else if (event.getKeyCode() == KeyEvent.VK_D) {
					Main.inDebugMode = !Main.inDebugMode;
				}
			}
			Main.currentScreen.keyPressed(event);
		}
	};
}
