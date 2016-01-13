package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import game.StringDraw.TextAlign;
import game.io.ResourceHandler;

public class PaintUtils {
	
	public static final Color DARK_GREEN2 = new Color(0, 127, 0);
	public static final Color TRANSPARENT_GRAY = new Color(127, 127, 127, 63);
	public static final Color TRANSPARENT_WHITE = new Color(255, 255, 255, 223);
	
	public static float shiftedSine(float x) {
		return (float) (Math.sin(x * Math.PI - Math.PI / (double) 2) + 1) / (float) 2;
	}

	public static float shiftedArcsine(float x) {
		return (float) (Math.asin(x * 2 - 1) / (Math.PI / 2) + 1) / (float) 2;
	}
	
	public static void drawChangingRect(Graphics2D g, Rectangle rect, Color defaultColor, Color onMouseColor, Point mousePosition) {
		if (rect.contains(mousePosition)) {
			g.setColor(onMouseColor);
		}
		else {
			g.setColor(defaultColor);
		}
		g.fill(rect);
	}

	public static void drawStage(Graphics2D g, Dimension contentSize, String text, float state) {
		int alpha = (int) (255 - Math.abs(state - Main.stageShowTime / 2) / (float) (Main.stageShowTime / 2) * (float) 255);
		g.setColor(new Color(0, 0, 0, alpha));
		g.fillRect(0, 0, contentSize.width, contentSize.height);
		g.setColor(new Color(255, 255, 255, alpha));
		StringDraw.drawMaxString(g, contentSize.height / 32, text, new Rectangle(0, contentSize.height / 2 - contentSize.height / 16, contentSize.width, contentSize.height / 8));
	}
	
	public static void drawCurrentMoney(Graphics2D g, Dimension contentSize) throws IOException {
		g.drawImage(ResourceHandler.getTexture("BasicCoin.png", contentSize.height / 32), contentSize.width - contentSize.height / 32 - contentSize.height / 64, contentSize.height / 64, null);
		g.setColor(DARK_GREEN2);
		StringDraw.drawMaxString(g, contentSize.height / 64, String.valueOf(Main.money), TextAlign.RIGHT, new Rectangle(0, 0, contentSize.width - contentSize.height / 16, contentSize.height / 16));
	}
	
	public static void paintGenericHelpScreen(Graphics2D g, Dimension contentSize) {
		paintGenericHelpScreen(g, contentSize, TRANSPARENT_WHITE);
	}
	
	public static void paintGenericHelpScreen(Graphics2D g, Dimension contentSize, int alpha) {
		paintGenericHelpScreen(g, contentSize, new Color(255, 255, 255, alpha));
	}
	
	public static void paintGenericHelpScreen(Graphics2D g, Dimension contentSize, Color color) {
		g.setColor(color);
		g.fillRect(0, 0, contentSize.width, contentSize.height);
		g.setColor(Color.black);
		StringDraw.drawMaxString(g, contentSize.height / 128, "Press the Esc key to exit the help mode.", TextAlign.DOWNRIGHT, new Rectangle(0, contentSize.height - contentSize.height / 32, contentSize.width, contentSize.height / 32));
	}
}
