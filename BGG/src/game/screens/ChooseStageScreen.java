package game.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;

public class ChooseStageScreen extends ListScreen {

	private float showingStageState = 0;
	private boolean showingStage = false;
	private int selectedStage;

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
		if (this.showingStage) {
			PaintUtils.drawStage(super.g, super.contentSize, "Stage " + String.valueOf(this.selectedStage), this.showingStageState);
		}
		PaintUtils.drawCurrentMoney(super.g, super.contentSize);
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
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (this.showingStage) {
				this.showingStage = false;
			}
			else {
				Screen.startNew(new StartScreen());
			}
		}
	}
	
	@Override
	protected boolean acceptInputs() {
		return !this.showingStage;
	}
}
