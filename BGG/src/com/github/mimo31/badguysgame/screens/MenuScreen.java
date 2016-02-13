package com.github.mimo31.badguysgame.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.StringDraw;

public class MenuScreen extends Screen {

	protected final String[] buttonsText;
	private final Color defaultColor;
	private final Color onMouseColor;
	private final Color textColor;
	private final float widthFraction;
	private final float heightFraction;
	private final float intent;

	// Components
	protected Graphics2D g;
	protected Dimension contentSize = new Dimension(0, 0);
	protected Point mousePosition;
	private float rectsWidth = 0;
	private float rectsX = 0;
	private Rectangle[] buttons;

	public MenuScreen(String[] text, Color defaultColor, Color onMouseColor, Color textColor) {
		this(text, defaultColor, onMouseColor, textColor, 1 / (float) 2, 1 / (float) 8, 0);
	}

	public MenuScreen(String[] text, Color defaultColor, Color onMouseColor, Color textColor, float widthFraction, float heightFraction, float upDownIntent) {
		super();
		this.buttonsText = text;
		this.defaultColor = defaultColor;
		this.onMouseColor = onMouseColor;
		this.textColor = textColor;
		this.widthFraction = widthFraction;
		this.heightFraction = heightFraction;
		this.intent = upDownIntent;
		this.buttons = new Rectangle[buttonsText.length];
	}

	private void updateComponents(Graphics2D g, Dimension contentSize, Point mousePosition) {
		this.g = g;
		if (this.acceptInputs()) {
			this.mousePosition = mousePosition;
		}
		if (!this.contentSize.equals(contentSize)) {
			this.contentSize = contentSize;
			this.rectsWidth = widthFraction * contentSize.width;
			this.rectsX = (1 - widthFraction) / 2 * contentSize.width;
			float usableSpace = 1 - 2 * this.intent;
			float distanceBetweenButtons = usableSpace / (this.buttonsText.length + 1);
			for (int i = 0; i < this.buttonsText.length; i++) {
				this.buttons[i] = new Rectangle((int) this.rectsX, (int) (contentSize.height * (this.intent + (i + 1) * distanceBetweenButtons - this.heightFraction / 2)), (int) this.rectsWidth, (int) (this.heightFraction * contentSize.height));
			}
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws Throwable {
		this.updateComponents(g, contentSize, mousePosition);
		for (int i = 0; i < this.buttons.length; i++) {
			PaintUtils.drawChangingRect(this.g, this.buttons[i], defaultColor, onMouseColor, this.mousePosition);
			this.g.setColor(this.textColor);
			StringDraw.drawMaxString(g, (int) (this.contentSize.height * this.heightFraction / 4), this.buttonsText[i], this.buttons[i]);
		}
		this.paintOver();
	}

	@Override
	public void mousePressed(MouseEvent event) {
		for (int i = 0; i < this.buttons.length; i++) {
			if (this.buttons[i].contains(event.getPoint())) {
				this.buttonClicked(i);
				break;
			}
		}
	}

	protected void buttonClicked(int index) {

	}

	protected void paintOver() throws Throwable {

	}

	protected boolean acceptInputs() {
		return true;
	}

}
