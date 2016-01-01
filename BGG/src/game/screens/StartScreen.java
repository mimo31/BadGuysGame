package game.screens;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;

import game.Main;
import game.PaintUtils;

public final class StartScreen extends MenuScreen {

	// Components
	private boolean showingStage;
	private boolean noMoreStages;
	private float showingStageState;

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
		return !this.showingStage;
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
			Screen.startNew(new ShopScreen());
		}
		else {
			Screen.startNew(new AchievementsScreen());
		}
	}
}
