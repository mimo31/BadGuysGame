package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;
import game.Screen;
import game.mechanics.Game;
import game.mechanics.GameReturnData;

public class GameScreen extends Screen {

	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 31);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 31);

	private Game game;
	private boolean showingStage;
	private boolean gameOver;
	private boolean noMoreStages;
	private float showingStageState;
	private int showingStageNumber;
	
	// Components
	private Point usedMousePosition;
	private Dimension contentSize;

	public GameScreen(int stage, Point showingStageMousePos) {
		this.game = new Game(stage, Main.getSelectedBarrel());
		this.usedMousePosition = showingStageMousePos;
		this.showingStage = true;
		this.showingStageState = Main.stageShowTime / 2;
		this.showingStageNumber = stage;
	}
	
	private void updateComponents(Dimension contentSize, Point mousePosition) {
		if (!this.showingStage) {
			this.usedMousePosition = mousePosition;
		}
		this.contentSize = contentSize;
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(contentSize, mousePosition);
		this.game.paint(g, contentSize, usedMousePosition);
		PaintUtils.drawCurrentMoney(g, contentSize);
		if (this.showingStage) {
			if (this.gameOver) {
				PaintUtils.drawStage(g, contentSize, "Game Over", this.showingStageState);
			}
			else if (this.noMoreStages) {
				PaintUtils.drawStage(g, contentSize, "No more stages :{", this.showingStageState);
			}
			else {
				PaintUtils.drawStage(g, contentSize, "Stage " + String.valueOf(this.showingStageNumber), this.showingStageState);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!this.showingStage) {
			this.game.clicked(event.getX(), event.getY(), this.contentSize);
		}
	}

	@Override
	public void update(int time) {
		if (!this.showingStage) {
			GameReturnData returnData = this.game.update(time, this.contentSize);
			if (returnData != null) {
				switch (returnData.actionType) {
					case GAME_OVER:
						this.gameOver = true;
						break;
					case NO_MORE_STAGES:
						this.noMoreStages = true;
						break;
					case NEXT_STAGE:
						this.showingStageNumber = returnData.stage;
						break;
					default:
						break;
				}
				this.showingStage = true;
				this.showingStageState = 0;
			}
		}
		else {
			this.showingStageState += time / (float) 40;
			if (this.showingStageState >= Main.stageShowTime) {
				this.showingStage = false;
			}
			else if (this.showingStageState >= Main.stageShowTime / 2) {
				if (this.gameOver || this.noMoreStages) {
					Screen.startNew(new StartScreen(this.noMoreStages, this.usedMousePosition));
				}
			}
		}
	}
	
	@Override
	public void getCloseReady() {
		int achievedStage = this.game.getExitData().stage;
		int reachedCheckpoint = (int) Math.floor(achievedStage / (float) 5);
		if (reachedCheckpoint > Main.maxReachedStage)
		{
			Main.maxReachedStage = reachedCheckpoint;
		}
	}
}
