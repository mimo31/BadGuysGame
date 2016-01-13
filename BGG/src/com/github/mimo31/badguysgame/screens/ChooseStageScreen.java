package com.github.mimo31.badguysgame.screens;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;

import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.StringDraw;

public class ChooseStageScreen extends ListScreen {

	private float showingStageState = 0;
	private boolean showingStage = false;
	private int selectedStage;
	private boolean showingHelp;

	public ChooseStageScreen() {
		super(getButtonTextes(), Color.BLUE, Color.RED, Color.WHITE);
	}

	private static String[] getButtonTextes() {
		String[] textArray = new String[Main.maxReachedStage + 1];
		textArray[0] = "The very beginning";
		for (int i = 1; i < textArray.length; i++) {
			textArray[i] = "Stage " + String.valueOf(i * 5);
		}
		return textArray;
	}

	@Override
	public void paintOver() throws IOException {
		PaintUtils.drawCurrentMoney(super.g, super.contentSize);
		if (this.showingStage) {
			PaintUtils.drawStage(super.g, super.contentSize, "Stage " + String.valueOf(this.selectedStage), this.showingStageState);
		}
		if (this.showingHelp) {
			PaintUtils.paintGenericHelpScreen(super.g, super.contentSize);
			StringDraw.drawMaxString(super.g, "This is a menu to choose the stage you want to play.", new Rectangle(super.contentSize.width / 4, super.contentSize.height / 4, super.contentSize.width / 2, super.contentSize.height / 8));
		}
	}

	@Override
	public void buttonClicked(int index) {
		this.selectedStage = index * 5;
		this.showingStage = true;
		this.showingStageState = 0;
	}

	@Override
	public void update(int time) {
		if (this.showingStage) {
			this.showingStageState += time / (float) 40;
			if (this.showingStageState >= Main.stageShowTime / 2) {
				Screen.startNew(new GameScreen(this.selectedStage, super.mousePosition));
			}
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
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (this.showingStage) {
					this.showingStage = false;
				}
				else {
					Screen.startNew(new StartScreen());
				}
			}
			else if (event.getKeyCode() == KeyEvent.VK_H && !this.showingStage) {
				this.showingHelp = true;
			}
		}
	}

	@Override
	protected boolean acceptInputs() {
		return !this.showingStage && !this.showingHelp;
	}
}
