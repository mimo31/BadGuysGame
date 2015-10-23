package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import game.StringDraw.TextAlign;

public final class Painting {
	
	public static Rectangle playButton;
	
	public static void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException{
		if (Main.inStartScreen) {
			playButton = new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 2);
			drawChangingRect(g, playButton, Color.black, Color.MAGENTA, mousePosition);
			g.setColor(Color.white);
			StringDraw.drawMaxString(g, contentSize.height / 16, "Play", TextAlign.MIDDLE, playButton, Font.ITALIC);
		}
		else {
				for (int i = 0; i < Main.badGuys.size(); i++) {
					BadGuy currentBadGuy = Main.badGuys.get(i);
					g.drawImage(IO.getTexture("BadGuy.png", contentSize.width / 16), (int)((currentBadGuy.x / (float)4 + 1 / (float)8 - 1 / (float)32) * contentSize.width), (int)(currentBadGuy.y * contentSize.height - contentSize.width / 16), null);
				}
				g.drawImage(IO.getTexture("Base.png", contentSize.width / 8), contentSize.width / 2 - contentSize.width / 16, contentSize.height - contentSize.width / 16, null);
				Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
				AffineTransform transform = AffineTransform.getTranslateInstance(barrelCenter.x, barrelCenter.y);
				Point pointTo;
				if (Main.showingStage) {
					pointTo = Main.showingStageMousePos;
				}
				else {
					pointTo = mousePosition;
				}
				double vecX = -barrelCenter.x + pointTo.x;
				double vecY = -pointTo.y + barrelCenter.y;
				transform.rotate(vecY, vecX);
				transform.translate(-contentSize.width / 32, -contentSize.width / 32);
				g.drawImage(IO.getTexture("Barrel.png", contentSize.width / 16), transform, null);
				for (int i = 0; i < Main.bullets.size(); i++) {
					Bullet currentBullet = Main.bullets.get(i);
					g.drawImage(IO.getTexture("Bullet.png", contentSize.width / 64), (int)(currentBullet.x * contentSize.width - contentSize.width / (float)128), (int)(currentBullet.y * contentSize.height - contentSize.width / (float)128), null);
				}
				if (Main.showingStage) {
					int alpha = (int)(255 - Math.abs(Main.showingStageState - 60) / (float)60 * (float)255);
					drawStage(g, contentSize, alpha);
				}
		}
	}
	
	private static void drawChangingRect(Graphics2D g, Rectangle rect, Color defaultColor, Color onMouseColor, Point mousePosition) {
		if (rect.contains(mousePosition)) {
			g.setColor(onMouseColor);
		}
		else {
			g.setColor(defaultColor);
		}
		g.fill(rect);
	}
	
	private static void drawStage(Graphics2D g, Dimension contentSize, int alpha) {
		g.setColor(new Color(0, 0, 0, alpha));
		g.fillRect(0, 0, contentSize.width, contentSize.height);
		g.setColor(new Color(255, 255, 255, alpha));
		StringDraw.drawMaxString(g, "Stage " + Main.currentStage, new Rectangle(contentSize.width / 2 - contentSize.width / 16, contentSize.height / 2 - contentSize.height / 16, contentSize.width / 8, contentSize.height / 8));
	}
}
