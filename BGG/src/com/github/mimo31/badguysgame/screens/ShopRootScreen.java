package com.github.mimo31.badguysgame.screens;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.mimo31.badguysgame.IntHolder;
import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.StringDraw;
import com.github.mimo31.badguysgame.mechanics.weaponry.Weapon;

public class ShopRootScreen extends MenuScreen {

	private boolean showingHelp;

	public ShopRootScreen() {
		this(getOfferedWeapons());
	}
	
	private ShopRootScreen(String[] offeredWeapons) {
		super(offeredWeapons, Color.orange, Color.cyan, Color.white, 3 / (float) 5, 1 / (float) (offeredWeapons.length + 2), 0);
	}

	private static String[] getOfferedWeapons() {
		List<String> offerList = new ArrayList<String>();
		offerList.add("Barrels");
		for (int i = 0; i < Main.autoweapons.length; i++) {
			if (Main.autoweapons[i].doDisplay()) {
				offerList.add("Autoweapons");
				break;
			}
		}
		for (int i = 0; i < Main.crushers.length; i++) {
			if (Main.crushers[i].doDisplay()) {
				offerList.add("Crushers");
				break;
			}
		}
		return offerList.toArray(new String[offerList.size()]);
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
		IntHolder selectedItem = null;
		Weapon[] weaponArray = null;
		String clickedText = super.buttonsText[index];
		if (clickedText.equals("Barrels")) {
			selectedItem = Main.selectedBarrel;
			weaponArray = Main.barrels;
		}
		else if (clickedText.equals("Autoweapons")) {
			selectedItem = Main.selectedAutoweapon;
			weaponArray = Main.autoweapons;
		}
		else if (clickedText.equals("Crushers")) {
			selectedItem = Main.selectedCrusher;
			weaponArray = Main.crushers;
		}
		Screen.startNew(new ShopScreen(weaponArray, selectedItem));
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
