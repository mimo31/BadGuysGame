package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import game.PaintUtils;
import game.StringDraw;

public class ListScreen extends Screen {

	private final String[] buttonsText;
	private final Color defaultColor;
	private final Color onMouseColor;
	private final Color textColor;
	private final float widthFraction;
	private final float heightFraction;
	private final float spaceHeightFraction;

	private float listPosition;
	private final float downListPositionBound;
	private final float upListPositionBound;

	// Components
	protected Graphics2D g;
	protected Dimension contentSize;
	protected Point mousePosition;
	private float rectsWidth = 0;
	private float rectsX = 0;

	public ListScreen(String[] text, Color defaultColor, Color onMouseColor, Color textColor) {
		this(text, defaultColor, onMouseColor, textColor, 1 / (float) 2, 1 / (float) 8, 1 / (float) 8);
	}

	public ListScreen(String[] text, Color defaultColor, Color onMouseColor, Color textColor, float widthFraction, float heightFraction, float spaceHeightFraction) {
		super();
		this.buttonsText = text;
		this.defaultColor = defaultColor;
		this.onMouseColor = onMouseColor;
		this.textColor = textColor;
		this.widthFraction = widthFraction;
		this.heightFraction = heightFraction;
		this.spaceHeightFraction = spaceHeightFraction;
		this.listPosition = spaceHeightFraction / 2;
		this.downListPositionBound = this.listPosition;
		if (this.buttonsText.length >= 2) {
			this.upListPositionBound = (this.buttonsText.length - 2) * (this.heightFraction + this.spaceHeightFraction) + this.downListPositionBound;
		}
		else {
			this.upListPositionBound = this.downListPositionBound;
		}
	}

	private void updateComponents(Graphics2D g, Dimension contentSize, Point mousePosition) {
		this.g = g;
		this.contentSize = contentSize;
		if (this.acceptInputs()) {
			this.mousePosition = mousePosition;
		}
		this.rectsWidth = widthFraction * contentSize.width;
		this.rectsX = (1 - widthFraction) / 2 * contentSize.width;
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws Throwable {
		this.updateComponents(g, contentSize, mousePosition);
		float usedSpace = 0;
		this.drawNthElement((int) Math.floor(this.listPosition / (this.spaceHeightFraction + this.heightFraction)));
		usedSpace = this.spaceHeightFraction + this.heightFraction - this.listPosition % (this.spaceHeightFraction + this.heightFraction);
		for (int i = (int) Math.floor(this.listPosition / (this.spaceHeightFraction + this.heightFraction) + 1); i < this.buttonsText.length && usedSpace < 1; i++) {
			drawNthElement(i);
			usedSpace += this.heightFraction + this.spaceHeightFraction;
		}
		this.paintOver();
	}

	private void drawNthElement(int n) {
		float posInHeights = n * (heightFraction + spaceHeightFraction) + spaceHeightFraction - this.listPosition;
		int realPos = (int) (posInHeights * this.contentSize.height);
		Rectangle buttonRect = new Rectangle((int) this.rectsX, realPos, (int) this.rectsWidth, (int) (this.contentSize.height * this.heightFraction));
		PaintUtils.drawChangingRect(this.g, buttonRect, defaultColor, onMouseColor, this.mousePosition);
		this.g.setColor(this.textColor);
		StringDraw.drawMaxString(g, (int) (this.contentSize.height * this.heightFraction / 4), this.buttonsText[n], buttonRect);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (this.acceptInputs()) {
			this.listPosition += event.getWheelRotation() / (float) 32;
			if (this.listPosition < this.downListPositionBound) {
				this.listPosition = this.downListPositionBound;
			}
			else if (this.listPosition > this.upListPositionBound) {
				this.listPosition = this.upListPositionBound;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (this.acceptInputs()) {
			if (event.getX() > this.rectsX && event.getX() < this.rectsX + this.rectsWidth) {
				float heightClickPosition = this.listPosition + event.getY() / (float) this.contentSize.height;
				boolean buttonClicked = heightClickPosition % (this.heightFraction + this.spaceHeightFraction) > this.spaceHeightFraction;
				if (buttonClicked) {
					int buttonIndex = (int) Math.floor(heightClickPosition / (this.heightFraction + this.spaceHeightFraction));
					if (buttonIndex < this.buttonsText.length) {
						this.buttonClicked(buttonIndex);
					}
				}
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
