package game.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;

public class ShopRootScreen extends MenuScreen {

	public ShopRootScreen() {
		super(new String[] { "Barrels", "Automatic weapons" }, Color.orange, Color.cyan, Color.white, 3 / (float) 5, 1 / (float) 4, 0);
	}

	@Override
	public void paintOver() throws IOException {
		PaintUtils.drawCurrentMoney(super.g, super.contentSize);
	}
	
	@Override
	protected void buttonClicked(int index) {
		if (index == 0) {
			Screen.startNew(new ShopScreen(Main.barrels, Main.selectedBarrel));
		}
		else {
			Screen.startNew(new ShopScreen(Main.autoweapons, Main.selectedAutoweapon));
		}
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			Screen.startNew(new StartScreen());
		}
	}
}
