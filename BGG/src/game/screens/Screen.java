package game.screens;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import game.Gui;
import game.Main;

public abstract class Screen {

	public Screen() {
		this.onStart();
	}

	public void onStart() {
	}

	public void update(int time) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}

	public void mouseDragged(MouseEvent event) {
	}

	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws Throwable {
	}

	public void keyPressed(KeyEvent event) {
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
	}
	
	public void getCloseReady() {
	}

	protected static void startNew(Screen screen) {
		Main.currentScreen.getCloseReady();
		Main.currentScreen = screen;
		Gui.gui.repaint();
	}
	
	protected static void startNewWithoutClosing(Screen screen) {
		Main.currentScreen = screen;
		Gui.gui.repaint();
	}
}
