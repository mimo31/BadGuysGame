package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import game.PaintUtils;
import game.StringDraw;

public class ConnectionProblemScreen extends Screen {

	// Components
	private Rectangle exitButton;
	private Rectangle infoBounds;
	private Dimension contentSize;

	@Override
	public void onStart() {
		this.initilaizeComponents();
	}

	private void initilaizeComponents() {
		this.exitButton = new Rectangle();
		this.infoBounds = new Rectangle();
		this.contentSize = new Dimension();
	}

	private void updateComponents(Dimension contentSize) {
		if (this.contentSize.width != contentSize.width || this.contentSize.height != contentSize.height) {
			this.contentSize = contentSize;
			this.exitButton.setBounds(0, contentSize.height / 2, contentSize.width, contentSize.height / 2);
			this.infoBounds.setBounds(0, 0, contentSize.width, contentSize.height / 2);
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) {
		this.updateComponents(contentSize);
		PaintUtils.drawChangingRect(g, this.exitButton, Color.black, Color.magenta, mousePosition);
		g.setColor(Color.black);
		StringDraw.drawMaxString(g, this.contentSize.height / 8, "We're sorry, but we're unable to connect to the server and download the requied resources. \u2639\u2639\u2639 Please, check your internet connection.", this.infoBounds);
		g.setColor(Color.white);
		StringDraw.drawMaxString(g, this.contentSize.height / 8, "Exit", this.exitButton);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (exitButton.contains(event.getPoint())) {
			System.exit(0);
		}
	}
}
