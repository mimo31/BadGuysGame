package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import game.PaintUtils;
import game.StringDraw;

public class WelcomeScreen extends Screen {

	private boolean showingHelp;

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) {
		g.setColor(Color.red);
		StringDraw.drawMaxString(g, "Welcome to Bad Guys Game", new Rectangle(contentSize.width / 8, contentSize.height / 16, contentSize.width * 3 / 4, contentSize.height / 8));
		g.setColor(Color.black);
		StringDraw.drawMaxString(g, "Here are two tips that might help you during the game:", new Rectangle(contentSize.width / 8, contentSize.height / 4, contentSize.width * 3 / 4, contentSize.height / 16));
		StringDraw.drawMaxString(g, "1) When you want to go a page back, try to press the Esc key.", new Rectangle(contentSize.width / 8, contentSize.height * 3 / 8, contentSize.width * 3 / 4, contentSize.height / 16));
		StringDraw.drawMaxString(g, "2) When you need help, try to press the H key.", new Rectangle(contentSize.width / 8, contentSize.height / 2, contentSize.width * 3 / 4, contentSize.height / 16));
		StringDraw.drawMaxString(g, "Press the Enter key to continue.", new Rectangle(contentSize.width / 4, contentSize.height * 3 / 4, contentSize.width / 2, contentSize.height / 16));
		if (this.showingHelp) {
			PaintUtils.paintGenericHelpScreen(g, contentSize);
			StringDraw.drawMaxString(g, "Yes, this is the help mode.", new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 2));
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (!this.showingHelp) {
			if (event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = true;
			}
			else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				Screen.startNew(new StartScreen());
			}
		}
		else {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE || event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = false;
			}
		}
	}
}
