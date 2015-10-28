package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import game.Barrel.BarrelGameProperty;
import game.StringDraw.TextAlign;

public final class Painting {

	public static Rectangle playButton;
	public static Rectangle shopButton;
	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 127);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 127);
	public static final Color DARK_GREEN = new Color(0, 127, 0);
	public static final Color GREEN = new Color(114, 241, 46);
	public static final Color DARK_GREEN2 = GREEN.darker();

	public static void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		Point usedMousePosition;
		if (Main.showingStage) {
			usedMousePosition = Main.showingStageMousePos;
		}
		else {
			usedMousePosition = mousePosition;
		}
		switch (Main.inScreen) {
			case START_SCREEN:
				playButton = new Rectangle(contentSize.width / 4, contentSize.height / 8, contentSize.width / 2, contentSize.height / 4);
				shopButton = new Rectangle(contentSize.width / 4, contentSize.height / 2 + contentSize.height / 8, contentSize.width / 2, contentSize.height / 4);
				drawChangingRect(g, playButton, Color.black, Color.MAGENTA, usedMousePosition);
				drawChangingRect(g, shopButton, Color.black, Color.MAGENTA, usedMousePosition);
				g.setColor(Color.white);
				StringDraw.drawMaxString(g, contentSize.height / 16, "Play", TextAlign.MIDDLE, playButton);
				StringDraw.drawMaxString(g, contentSize.height / 16, "Shop", TextAlign.MIDDLE, shopButton);
				break;
			case GAME_SCREEN: 
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
				break;
			case SHOP_SCREEN:
				g.setColor(Color.black);
				g.fillRect(0, contentSize.height / 8 - contentSize.height / 256, contentSize.width, contentSize.height / 256);
				Rectangle leftArrowButton = new Rectangle(0, 0, contentSize.height / 16 - contentSize.height / 256, contentSize.height / 8 - contentSize.height / 256);
				drawChangingRect(g, leftArrowButton, GREEN, DARK_GREEN2, usedMousePosition);
				if (leftArrowButton.contains(usedMousePosition)) {
					g.setColor(Color.yellow);
				}
				else {
					g.setColor(Color.white);
				}
				Polygon leftTriangle = new Polygon(new int[] {leftArrowButton.width * 3 / 4, leftArrowButton.width * 3 / 4, leftArrowButton.width / 4}, new int[] {leftArrowButton.height * 3 / 4, leftArrowButton.height / 4, leftArrowButton.height / 2}, 3);
				g.fillPolygon(leftTriangle);
				Rectangle rightArrowButton = new Rectangle(contentSize.width - leftArrowButton.width, 0, leftArrowButton.width, leftArrowButton.height);
				drawChangingRect(g, rightArrowButton, GREEN, DARK_GREEN2, usedMousePosition);
				if (rightArrowButton.contains(usedMousePosition)) {
					g.setColor(Color.yellow);
				}
				else {
					g.setColor(Color.white);
				}
				Polygon rightTriangle = new Polygon(new int[] { contentSize.width - rightArrowButton.width * 3 / 4, contentSize.width - leftArrowButton.width / 4, contentSize.width - leftArrowButton.width * 3 / 4 }, new int[] {leftArrowButton.height * 3 / 4, leftArrowButton.height / 2, leftArrowButton.height / 4}, 3);
				g.fillPolygon(rightTriangle);
				Barrel selectedBarrel = Main.getSelectedBarrel();
				g.setColor(Color.orange);
				for (int i = 0; i < 3; i++) {
					float fractionToFill = selectedBarrel.gameProperties[i].getUpgradedDrawnFraction();
					if (fractionToFill != 0) {
						g.fillRect(0, (i + 2) * contentSize.height / 8, (int) (contentSize.width * fractionToFill / 2), contentSize.height / 8);
					}
				}
				int borderSize = contentSize.height / 32;
				g.setColor(Color.black);
				StringDraw.drawMaxString(g, contentSize.height / 32, Main.getSelectedBarrel().name, new Rectangle(0, contentSize.height / 8, contentSize.width, contentSize.height / 8));
				StringDraw.drawMaxString(g, borderSize, "Loading time", TextAlign.LEFT, getFirstAndSecondCols(2, contentSize));
				StringDraw.drawMaxString(g, borderSize, "Projectile power", TextAlign.LEFT, getFirstAndSecondCols(3, contentSize));
				StringDraw.drawMaxString(g, borderSize, "Projectile speed", TextAlign.LEFT, getFirstAndSecondCols(4, contentSize));
				StringDraw.drawMaxString(g, borderSize, toString(selectedBarrel.getLoadingSpeed()), getRowColumnRect(2, 2, contentSize));
				StringDraw.drawMaxString(g, borderSize, toString(selectedBarrel.getProjectilePower()), getRowColumnRect(3, 2, contentSize));
				StringDraw.drawMaxString(g, borderSize, toString(selectedBarrel.getProjectileSpeed()), getRowColumnRect(4, 2, contentSize));
				for (int i = 0; i < 3; i++) {
					StringDraw.drawMaxString(g, borderSize, getLastColumnText(selectedBarrel.gameProperties[i]), getRowColumnRect(i + 2, 3, contentSize));
				}
				float indentationSize = contentSize.height / (float)256;
				float iconSize = contentSize.height / (float)16;
				g.drawImage(IO.getTexture("BasicCoin.png", (int)iconSize), (int)(contentSize.width - indentationSize - iconSize), (int)(contentSize.height - contentSize.height / 32 - iconSize), null);
				Rectangle moneyAmountBounds = new Rectangle(contentSize.width / 2, contentSize.height - contentSize.height / 8, (int) (contentSize.width / 2 - iconSize - 2 * indentationSize), contentSize.height / 8);
				StringDraw.drawMaxString(g, borderSize, "Total money", TextAlign.LEFT, getFirstAndSecondCols(7, contentSize));
				g.setColor(DARK_GREEN);
				StringDraw.drawMaxString(g, borderSize, String.valueOf(Main.money), TextAlign.RIGHT, moneyAmountBounds);
				break;
		}
		if (Main.inScreen != Screen.SHOP_SCREEN) {
			float indentationSize = contentSize.height / (float)256;
			float iconSize = contentSize.height / (float)16 - indentationSize * 2;
			g.drawImage(IO.getTexture("BasicCoin.png", (int)iconSize), (int)(contentSize.width - iconSize - indentationSize), (int)indentationSize, null);
			Rectangle moneyAmountBounds = new Rectangle(0, 0,  contentSize.width - contentSize.height / 16, contentSize.height / 16);
			g.setColor(DARK_GREEN);
			StringDraw.drawMaxString(g, (int) (indentationSize * 2), String.valueOf(Main.money), StringDraw.TextAlign.RIGHT, moneyAmountBounds);
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

	private static String getLastColumnText(BarrelGameProperty barrelProperties) {
		if (barrelProperties.isFullyUpgraded()) {
			return "maxed";
		}
		else {
			float upgradeValue = barrelProperties.getUpgradeValue();
			String valuePart;
			if (upgradeValue > 0) {
				valuePart = "+" + toString(upgradeValue);
			}
			else if (upgradeValue < 0) {
				valuePart = toString(upgradeValue);
			}
			else {
				valuePart = "±0";
			}
			return valuePart + "(" + String.valueOf(barrelProperties.getUpgradeCost()) + "c)";
		}
	}
	
	private static Rectangle getFirstAndSecondCols(int row, Dimension contentSize) {
		return new Rectangle(0, row * contentSize.height / 8, contentSize.width / 2, contentSize.height / 8);
	}
	
	private static Rectangle getRowColumnRect(int row, int column, Dimension contentSize) {
		return new Rectangle(column * contentSize.width / 4, row * contentSize.height / 8, contentSize.width / 4, contentSize.height / 8);
	}
	
	private static String toString(float f) {
		if (f == (int)f) {
			return String.valueOf((int)f);
		}
		else {
			return String.valueOf(f);
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
