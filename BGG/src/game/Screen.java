package game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class Screen {
	
	public Screen() {
		onStart();
	}
	
	public void onStart() { }
	
	public void update() { }
	
	public void mousePressed(MouseEvent event) {}
	
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws Throwable {}
	
	protected static void startNew(Screen screen) {
		Main.currentScreen = screen;
		Gui.gui.repaint();
	}
}
