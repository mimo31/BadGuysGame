package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
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
		gui = new JFrame("The Bad Guys Game");
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.add(paintComponent);
		gui.getContentPane().addMouseListener(mouseEventsHandler);
		gui.getContentPane().addMouseMotionListener(mouseEventsHandler);
		gui.repaint();
	}

	@SuppressWarnings("serial")
	public static JComponent paintComponent = new JComponent() {

		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			try {
				Main.currentScreen.paint(g, gui.getContentPane().getSize(), getMousePanePosition());
				//Painting.paint(g, gui.getContentPane().getSize(), getMousePanePosition());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	};

	public static MouseInputAdapter mouseEventsHandler = new MouseInputAdapter() {

		public void mousePressed(MouseEvent event) {
			Main.currentScreen.mousePressed(event);
			/*Dimension contentSize = getContentSize();
			switch(Main.inScreen) {
				case START_SCREEN:
					if (Painting.playButton.contains(event.getPoint())) {
						Main.startPlaying();
						gui.repaint();
					}
					else if (Painting.shopButton.contains(event.getPoint())) {
						Main.inScreen = Screen.SHOP_SCREEN;
						gui.repaint();
					}
					break;
				case GAME_SCREEN:
					if (!Main.showingStage) {
						if (Main.loadState == 1) {
							Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
							float vecX = event.getX() - barrelCenter.x;
							float vecY = event.getY() - barrelCenter.y;
							float factor = (float) (contentSize.width / (float) 32 / Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2)));
							Point firePoint = new Point((int) (barrelCenter.x + vecX * factor), (int) (barrelCenter.y + vecY * factor));
							Barrel selectedBarrel = Main.getSelectedBarrel();
							float speed = selectedBarrel.getProjectileSpeed();
							String textureName = selectedBarrel.projectileTextureName;
							float hitPower = selectedBarrel.getProjectilePower();
							float dirX = vecX / (float) contentSize.width;
							float dirY =  vecY / (float) contentSize.height;
							Projectile projectile = new Projectile(firePoint.x / (float) contentSize.width, firePoint.y / (float) contentSize.height, dirX, dirY, speed, textureName, hitPower);
							Main.projectiles.add(projectile);
							Main.loadState = 0;
						}
					}
					break;
				case SHOP_SCREEN:
					break;
			}*/
		}

		public void mouseMoved(MouseEvent event) {
			gui.repaint();
		}

		public void mouseDragged(MouseEvent event) {
			gui.repaint();
		}
	};
}
