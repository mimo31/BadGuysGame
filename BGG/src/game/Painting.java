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
	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 127);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 127);

	public static void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		Point usedMousePosition;
		if (Main.showingStage) {
			usedMousePosition = Main.showingStageMousePos;
		}
		else {
			usedMousePosition = mousePosition;
		}
		if (Main.inStartScreen) {
			playButton = new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 2);
			drawChangingRect(g, playButton, Color.black, Color.MAGENTA, usedMousePosition);
			g.setColor(Color.white);
			StringDraw.drawMaxString(g, contentSize.height / 16, "Play", TextAlign.MIDDLE, playButton, Font.ITALIC);
		}
		else {
			for (int i = 0; i < Main.badGuys.size(); i++) {
				drawBadGuy(g, Main.badGuys.get(i), contentSize);
			}
			int baseScreenX = contentSize.width / 2 - contentSize.width / 16;
			int baseScreenY = contentSize.height - contentSize.width / 16;
			g.drawImage(IO.getTexture("Base.png", contentSize.width / 8), baseScreenX, baseScreenY, null);
			int greenWidth = (int) (contentSize.width / 8 * Main.loadState);
			g.setColor(TRANSPARENT_GREEN);
			g.fillRect(baseScreenX, baseScreenY, greenWidth, contentSize.width / 16);
			g.setColor(TRANSPARENT_RED);
			g.fillRect(baseScreenX + greenWidth, baseScreenY, contentSize.width / 8 - greenWidth, contentSize.width / 16);
			Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
			AffineTransform transform = AffineTransform.getTranslateInstance(barrelCenter.x, barrelCenter.y);
			double vecX = -barrelCenter.x + usedMousePosition.x;
			double vecY = -usedMousePosition.y + barrelCenter.y;
			transform.rotate(vecY, vecX);
			transform.translate(-contentSize.width / 32, -contentSize.width / 32);
			g.drawImage(IO.getTexture(Main.getSelectedBarrel().textureName, contentSize.width / 16), transform, null);
			for (int i = 0; i < Main.projectiles.size(); i++) {
				Projectile currentProjectile = Main.projectiles.get(i);
				g.drawImage(IO.getTexture(currentProjectile.textureName, contentSize.width / 64), (int) (currentProjectile.x * contentSize.width - contentSize.width / (float) 128), (int) (currentProjectile.y * contentSize.height - contentSize.width / (float) 128), null);
			}
		}
		if (Main.showingStage) {
			int alpha = (int) (255 - Math.abs(Main.showingStageState - 60) / (float) 60 * (float) 255);
			String text;
			if (Main.noMoreStages) {
				text = "No more stages :{";
			}
			else if (Main.gameOver) {
				text = "Game Over";
			}
			else {
				text = "Stage " + Main.currentStage;
				drawStage(g, contentSize, "Stage " + Main.currentStage, alpha);
			}
			drawStage(g, contentSize, text, alpha);
		}
	}

	private static void drawBadGuy(Graphics2D g, BadGuy badGuy, Dimension contentSize) throws IOException {
		int screenX = (int) ((badGuy.x / (float) 4 + 1 / (float) 8 - 1 / (float) 32) * contentSize.width);
		int screenY = (int) (badGuy.y * contentSize.height - contentSize.width / 16);
		g.drawImage(IO.getTexture(badGuy.textureName, contentSize.width / 16), screenX, screenY, null);
		g.setColor(TRANSPARENT_GREEN);
		int filledSize = (int) (badGuy.getShownLive() * contentSize.width / 16);
		g.fillRect(screenX, screenY + contentSize.width / 16 - filledSize, contentSize.width / 16, filledSize);
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

	private static void drawStage(Graphics2D g, Dimension contentSize, String text, int alpha) {
		g.setColor(new Color(0, 0, 0, alpha));
		g.fillRect(0, 0, contentSize.width, contentSize.height);
		g.setColor(new Color(255, 255, 255, alpha));
		StringDraw.drawMaxString(g, contentSize.height / 32, text, new Rectangle(0, contentSize.height / 2 - contentSize.height / 16, contentSize.width, contentSize.height / 8));
	}
}
