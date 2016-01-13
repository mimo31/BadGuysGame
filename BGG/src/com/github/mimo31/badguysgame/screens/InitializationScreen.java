package com.github.mimo31.badguysgame.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.StringDraw;
import com.github.mimo31.badguysgame.StringDraw.StringDrawAttributes;

public class InitializationScreen extends Screen {

	// Components
	private Rectangle firstRect;
	private Rectangle secondRect;
	private Rectangle thirdRect;
	private StringDrawAttributes textAttributes;
	private Dimension contentSize;

	private float state = 0;

	private void initializeComponents() {
		this.firstRect = new Rectangle();
		this.secondRect = new Rectangle();
		this.thirdRect = new Rectangle();
		this.contentSize = new Dimension();
	};

	@Override
	public void onStart() {
		this.initializeComponents();
	}

	private void updateComponents(Dimension contentSize, Graphics2D g) {
		if (this.contentSize.width != contentSize.width || this.contentSize.height != contentSize.height) {
			this.contentSize = contentSize;
			this.firstRect.setBounds(0, 0, this.contentSize.width / 3, this.contentSize.height);
			this.secondRect.setBounds(this.firstRect.width, 0, this.firstRect.width, this.contentSize.height);
			this.thirdRect.setBounds(this.firstRect.width * 2, 0, this.firstRect.width, this.contentSize.height);
			this.textAttributes = StringDraw.getAttributes(g, "Initializing", new Rectangle(contentSize.height / 4, contentSize.height / 4, contentSize.width - contentSize.height / 2, contentSize.height / 2));
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) {
		this.updateComponents(contentSize, g);
		Rectangle selectedRectangle;
		if (this.state < 25) {
			selectedRectangle = this.firstRect;
		}
		else if (this.state < 50) {
			selectedRectangle = this.secondRect;
		}
		else {
			selectedRectangle = this.thirdRect;
		}
		g.setColor(Color.ORANGE);
		StringDraw.drawStringByAttributes(g, "Initializing", this.textAttributes);
		g.setColor(PaintUtils.TRANSPARENT_GRAY);
		g.fill(selectedRectangle);
	}

	@Override
	public void update(int time) {
		this.state += time / (float) 40;
		if (this.state >= 75) {
			this.state = 0;
		}
	}
}
