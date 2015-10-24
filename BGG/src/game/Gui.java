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

	public static Dimension getContentSize() {
		return gui.getContentPane().getSize();
	}

	public static Point getMousePanePosition() {
		Point mouseScreenPosition = MouseInfo.getPointerInfo().getLocation();
		Point paneScreenPosition = gui.getContentPane().getLocationOnScreen();
		return new Point(mouseScreenPosition.x - paneScreenPosition.x, mouseScreenPosition.y - paneScreenPosition.y);
	}

	public static void intializeGraphics() {
		gui = new JFrame("The Bad Guys Game");
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.add(paintComponent);
		gui.getContentPane().addMouseListener(mouseEventsHandler);
		gui.getContentPane().addMouseMotionListener(mouseEventsHandler);
	}

	@SuppressWarnings("serial")
	public static JComponent paintComponent = new JComponent() {

		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			try {
				Painting.paint(g, gui.getContentPane().getSize(), getMousePanePosition());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static MouseInputAdapter mouseEventsHandler = new MouseInputAdapter() {

		public void mousePressed(MouseEvent event) {
			if (Main.inStartScreen) {
				if (Painting.playButton.contains(event.getPoint())) {
					Main.startPlaying();
					gui.repaint();
				}
			}
			else {
				if (!Main.showingStage) {
					Dimension contentSize = getContentSize();
					if (Main.loadState == 1) {
						Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
						float vecX = event.getX() - barrelCenter.x;
						float vecY = event.getY() - barrelCenter.y;
						float factor = (float) (contentSize.width / (float) 32 / Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2)));
						Point firePoint = new Point((int) (barrelCenter.x + vecX * factor), (int) (barrelCenter.y + vecY * factor));
						Projectile bullet = new Projectile(firePoint.x / (float) contentSize.width, firePoint.y / (float) contentSize.height, vecX / (float) contentSize.width, vecY / (float) contentSize.height);
						Main.projectiles.add(bullet);
						Main.loadState = 0;
					}
				}
			}
		}

		public void mouseMoved(MouseEvent event) {
			gui.repaint();
		}

		public void mouseDragged(MouseEvent event) {
			gui.repaint();
		}
	};
}
