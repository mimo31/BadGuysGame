package com.github.mimo31.badguysgame.screens;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;

import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.StringDraw;

public final class StartScreen extends MenuScreen {

	private boolean showingStage;
	private boolean noMoreStages;
	private float showingStageState;
	private boolean showingHelp;

	public StartScreen() {
		super(new String[] { "Play", "Shop", "Achievements" }, Color.black, Color.magenta, Color.white, 1 / (float) 2, 1 / (float) 6, 0);
	}

	public StartScreen(boolean noMoreStages, Point showingStageMousePos) {
		super(new String[] { "Play", "Shop", "Achievements" }, Color.black, Color.magenta, Color.white);
		super.mousePosition = showingStageMousePos;
		this.showingStage = true;
		this.noMoreStages = noMoreStages;
		this.showingStageState = Main.stageShowTime / 2;
	}

	@Override
	protected boolean acceptInputs() {
		return !this.showingStage && !this.showingHelp;
	}

	@Override
	public void paintOver() throws IOException {
		PaintUtils.drawCurrentMoney(super.g, super.contentSize);
		if (this.showingStage) {
			if (this.noMoreStages) {
				PaintUtils.drawStage(super.g, super.contentSize, "No more stages :{", this.showingStageState);
			}
			else {
				PaintUtils.drawStage(super.g, super.contentSize, "Game Over", this.showingStageState);
			}
		}
		if (showingHelp) {
			PaintUtils.paintGenericHelpScreen(super.g, super.contentSize);
			StringDraw.drawMaxString(g, "This is the main menu.", new Rectangle(super.contentSize.width / 4, super.contentSize.height / 4, super.contentSize.width / 2, super.contentSize.height / 8));
			StringDraw.drawMaxString(g, "You can start playing, go to the shop or view your achievements.", new Rectangle(super.contentSize.width / 4, super.contentSize.height / 2, super.contentSize.width / 2, super.contentSize.height / 8));
		}
	}

	@Override
	public void update(int time) {
		if (this.showingStage) {
			this.showingStageState += time / (float) 40;
			if (this.showingStageState >= Main.stageShowTime) {
				this.showingStage = false;
			}
		}
	}

	@Override
	public void buttonClicked(int index) {
		if (index == 0) {
			Screen.startNew(new ChooseStageScreen());
		}
		else if (index == 1) {
			boolean unlockedFound = false;
			for (int i = 0; i < Main.autoweapons.length; i++) {
				if (Main.autoweapons[i].doDisplay()) {
					unlockedFound = true;
					break;
				}
			}
			if (!unlockedFound) {
				for (int i = 0; i < Main.crushers.length; i++) {
					if (Main.crushers[i].doDisplay()) {
						unlockedFound = true;
						break;
					}
				}
			}
			if (unlockedFound) {
				Screen.startNew(new ShopRootScreen());
			}
			else {
				Screen.startNew(new ShopScreen());
			}
		}
		else {
			Screen.startNew(new AchievementsScreen());
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
			if (event.getKeyCode() == KeyEvent.VK_H && !this.showingStage) {
				this.showingHelp = true;
			}
		}
	}
}
