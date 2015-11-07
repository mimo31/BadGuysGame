package game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class StringDraw {
	public static final int Middle = 0;
	public static final int Up = 1;
	public static final int UpRight = 2;
	public static final int Right = 3;
	public static final int DownRight = 4;
	public static final int Down = 5;
	public static final int DownLeft = 6;
	public static final int Left = 7;
	public static final int UpLeft = 8;

	public enum TextAlign {
		MIDDLE, UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT
	}

	private static int getSpaceSize(final Graphics2D graph2) {
		return getStringBounds(graph2, "h h", 0, 0).width - getStringBounds(graph2, "hh", 0, 0).width;
	}

	public static void drawMaxString(final Graphics2D graph2, final int borderSize, final String str, final TextAlign align, final Rectangle textBounds, final int fontType) {
		final Rectangle bounds = new Rectangle(textBounds.x + borderSize, textBounds.y + borderSize, textBounds.width - 2 * borderSize, textBounds.height - 2 * borderSize);
		if (bounds.width > 0 && bounds.height > 0) {
			graph2.setFont(graph2.getFont().deriveFont(fontType, 101f));
			Rectangle s1Size = getStringBounds(graph2, str, 0, 0);
			final Double s1Per1Width = s1Size.width / (double) 101;
			final Double s1Per1Height = s1Size.height / (double) 101;
			if (s1Per1Width / s1Per1Height > bounds.width / (double) bounds.height) {
				graph2.setFont(graph2.getFont().deriveFont(fontType, (float) (bounds.width / s1Per1Width)));
			}
			else {
				graph2.setFont(graph2.getFont().deriveFont(fontType, (float) (bounds.height / s1Per1Height)));
			}
			s1Size = getStringBounds(graph2, str, 0, 0);
			final int up = bounds.y - s1Size.y;
			final int down = bounds.y + bounds.height - s1Size.height - s1Size.y;
			final int left = bounds.x;
			final int right = bounds.x + bounds.width - s1Size.width;
			final int xMiddle = bounds.x + bounds.width / 2 - s1Size.width / 2;
			final int yMiddle = bounds.y + bounds.height / 2 + s1Size.height / 2 - (s1Size.height + s1Size.y);
			switch (align) {
				case UP:
					graph2.drawString(str, xMiddle, up);
					break;
				case UPRIGHT:
					graph2.drawString(str, right, up);
					break;
				case RIGHT:
					graph2.drawString(str, right, yMiddle);
					break;
				case DOWNRIGHT:
					graph2.drawString(str, right, down);
					break;
				case DOWN:
					graph2.drawString(str, xMiddle, down);
					break;
				case DOWNLEFT:
					graph2.drawString(str, left, down);
					break;
				case LEFT:
					graph2.drawString(str, left, yMiddle);
					break;
				case UPLEFT:
					graph2.drawString(str, left, up);
					break;
				default:
					graph2.drawString(str, xMiddle, yMiddle);
			}
			s1Size = null;
		}
	}

	public static void drawMaxString(final Graphics2D graph2, final int borderSize, final String str, final TextAlign align, Rectangle bounds) {
		drawMaxString(graph2, borderSize, str, align, bounds, Font.PLAIN);
	}

	public static void drawMaxString(final Graphics2D graph2, final String str, final TextAlign align, Rectangle bounds) {
		drawMaxString(graph2, 0, str, align, bounds, Font.PLAIN);
	}

	public static void drawMaxString(final Graphics2D graph2, final int borderSize, final String str, Rectangle bounds) {
		drawMaxString(graph2, borderSize, str, TextAlign.MIDDLE, bounds, Font.PLAIN);
	}

	public static void drawMaxString(final Graphics2D graph2, final String str, Rectangle bounds) {
		drawMaxString(graph2, 0, str, TextAlign.MIDDLE, bounds, Font.PLAIN);
	}

	public static void drawMaxString(final Graphics2D graph2, final String str, Rectangle bounds, final int fontType) {
		drawMaxString(graph2, 0, str, TextAlign.MIDDLE, bounds, fontType);
	}

	public static Rectangle getStringBounds(final Graphics2D g2, final String str, final float x, final float y) {
		if (str.length() != 0) {
			final FontRenderContext frc = g2.getFontRenderContext();
			final GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
			final Rectangle result = gv.getPixelBounds(null, x, y);
			result.x = 0;
			if (str.toCharArray()[0] == ' ' || str.toCharArray()[str.length() - 1] == ' ') {
				int counter = 0;
				while (counter < str.length()) {
					if (str.toCharArray()[counter] != ' ') {
						result.width = result.width + counter * getSpaceSize(g2);
						counter = str.length() - 1;
						while (counter >= 0) {
							if (str.toCharArray()[counter] != ' ') {
								result.width = result.width + (str.length() - 1 - counter) * getSpaceSize(g2);
								return result;
							}
							counter--;
						}
					}
					counter++;
				}
				if (counter == str.length()) {
					return new Rectangle(0, 0, getSpaceSize(g2) * str.length(), 0);
				}
			}
			return result;
		}
		else {
			return new Rectangle(0, 0, 0, 0);
		}
	}

	public static StringDrawAttributes getAttributes(final Graphics2D graph2, final int borderSize, final String str, final TextAlign align, final Rectangle textBounds, final int fontType) {
		final Rectangle bounds = new Rectangle(textBounds.x + borderSize, textBounds.y + borderSize, textBounds.width - 2 * borderSize, textBounds.height - 2 * borderSize);
		if (bounds.width > 0 && bounds.height > 0) {
			graph2.setFont(graph2.getFont().deriveFont(fontType, 101f));
			Rectangle s1Size = getStringBounds(graph2, str, 0, 0);
			final Double s1Per1Width = s1Size.width / (double) 101;
			final Double s1Per1Height = s1Size.height / (double) 101;
			final float fontSize;
			if (s1Per1Width / s1Per1Height > bounds.width / (double) bounds.height) {
				fontSize = (float) (bounds.width / s1Per1Width);
			}
			else {
				fontSize = (float) (bounds.height / s1Per1Height);
			}
			graph2.setFont(graph2.getFont().deriveFont(fontSize));
			s1Size = getStringBounds(graph2, str, 0, 0);
			final int up = bounds.y - s1Size.y;
			final int down = bounds.y + bounds.height - s1Size.height - s1Size.y;
			final int left = bounds.x;
			final int right = bounds.x + bounds.width - s1Size.width;
			final int xMiddle = bounds.x + bounds.width / 2 - s1Size.width / 2;
			final int yMiddle = bounds.y + bounds.height / 2 + s1Size.height / 2 - (s1Size.height + s1Size.y);
			final int x;
			final int y;
			switch (align) {
				case UP:
					x = xMiddle;
					y = up;
					break;
				case UPRIGHT:
					x = right;
					y = up;
					break;
				case RIGHT:
					x = right;
					y = yMiddle;
					break;
				case DOWNRIGHT:
					x = right;
					y = down;
					break;
				case DOWN:
					x = xMiddle;
					y = down;
					break;
				case DOWNLEFT:
					x = left;
					y = down;
					break;
				case LEFT:
					x = left;
					y = yMiddle;
					break;
				case UPLEFT:
					x = left;
					y = up;
					break;
				default:
					x = xMiddle;
					y = yMiddle;
			}
			return new StringDrawAttributes(x, y, fontSize);
		}
		else {
			return new StringDrawAttributes(true);
		}
	}

	public static StringDrawAttributes getAttributes(final Graphics2D graph2, final int borderSize, final String str, final TextAlign align, Rectangle bounds) {
		return getAttributes(graph2, borderSize, str, align, bounds, Font.PLAIN);
	}

	public static StringDrawAttributes getAttributes(final Graphics2D graph2, final String str, final TextAlign align, Rectangle bounds) {
		return getAttributes(graph2, 0, str, align, bounds, Font.PLAIN);
	}

	public static StringDrawAttributes getAttributes(final Graphics2D graph2, final int borderSize, final String str, Rectangle bounds) {
		return getAttributes(graph2, borderSize, str, TextAlign.MIDDLE, bounds, Font.PLAIN);
	}

	public static StringDrawAttributes getAttributes(final Graphics2D graph2, final String str, Rectangle bounds) {
		return getAttributes(graph2, 0, str, TextAlign.MIDDLE, bounds, Font.PLAIN);
	}

	public static StringDrawAttributes getAttributes(final Graphics2D graph2, final String str, Rectangle bounds, final int fontType) {
		return getAttributes(graph2, 0, str, TextAlign.MIDDLE, bounds, fontType);
	}

	public static void drawStringByAttributes(final Graphics2D g, String str, StringDrawAttributes attributes) {
		if (!attributes.doNotDraw) {
			g.setFont(g.getFont().deriveFont(attributes.fontSize));
			g.drawString(str, attributes.x, attributes.y);
		}
	}

	public static class StringDrawAttributes {
		private boolean doNotDraw;
		private int x;
		private int y;
		private float fontSize;

		private StringDrawAttributes(int x, int y, float fontSize) {
			this.x = x;
			this.y = y;
			this.fontSize = fontSize;
			this.doNotDraw = false;
		}

		private StringDrawAttributes(boolean doNotDraw) {
			this.doNotDraw = doNotDraw;
		}
	}
}
