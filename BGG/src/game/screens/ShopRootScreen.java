package game.screens;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;
import game.StringDraw;

public class ShopRootScreen extends MenuScreen {

	private boolean showingHelp;

	public ShopRootScreen() {
		super(new String[] { "Barrels", "Automatic weapons" }, Color.orange, Color.cyan, Color.white, 3 / (float) 5, 1 / (float) 4, 0);
	}

	@Override
	public void paintOver() throws IOException {
		PaintUtils.drawCurrentMoney(super.g, super.contentSize);
		if (this.showingHelp) {
			PaintUtils.paintGenericHelpScreen(super.g, super.contentSize);
			StringDraw.drawMaxString(super.g, "This is a menu to let you choose the type of weapons you want to buy / upgrade.", new Rectangle(super.contentSize.width / 4, super.contentSize.height / 4, super.contentSize.width / 2, super.contentSize.height / 8));
		}
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
		if (this.showingHelp) {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE || event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = false;
			}
		}
		else {
			if (event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = true;
			}
			else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				Screen.startNew(new StartScreen());
			}
		}
	}
	
	@Override
	protected boolean acceptInputs() {
		return !showingHelp;
	}
}
