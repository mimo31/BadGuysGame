package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class PaintUtils {
	
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

	public static void drawStage(Graphics2D g, Dimension contentSize, String text) {
		int alpha = (int) (255 - Math.abs(Main.showingStageState - Main.stageShowTime / 2) / (float) (Main.stageShowTime / 2) * (float) 255);
		g.setColor(new Color(0, 0, 0, alpha));
		g.fillRect(0, 0, contentSize.width, contentSize.height);
		g.setColor(new Color(255, 255, 255, alpha));
		StringDraw.drawMaxString(g, contentSize.height / 32, text, new Rectangle(0, contentSize.height / 2 - contentSize.height / 16, contentSize.width, contentSize.height / 8));
	}
}
