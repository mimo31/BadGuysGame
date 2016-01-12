package game.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;
import game.StringDraw;
import game.StringDraw.TextAlign;
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
	private boolean showingHelp;
	private boolean secondHelpPage;

	// Components
	private Point usedMousePosition;
	private Dimension contentSize;

	public GameScreen(int stage, Point showingStageMousePos) {
		this.game = new Game(stage, Main.getSelectedBarrel(), Main.getSelectedAutoweapon());
		this.usedMousePosition = showingStageMousePos;
		this.showingStage = true;
		this.showingStageState = Main.stageShowTime / 2;
		this.showingStageNumber = stage;
	}

	private void updateComponents(Dimension contentSize, Point mousePosition) {
		if (!this.showingStage && !this.showingHelp) {
			this.usedMousePosition = mousePosition;
		}
		this.contentSize = contentSize;
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(contentSize, mousePosition);
		this.game.paint(g, contentSize, this.usedMousePosition);
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
		if (this.showingHelp) {
			PaintUtils.paintGenericHelpScreen(g, contentSize);
			if (!this.secondHelpPage) {
				StringDraw.drawMaxString(g, "This is the main game field.", new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 8));
				StringDraw.drawMaxString(g, "Your task is to shoot at the bad guys falling from the top.", new Rectangle(contentSize.width / 4, contentSize.height / 2, contentSize.width / 2, contentSize.height / 16));
				StringDraw.drawMaxString(g, "Click anywhere to see more details.", new Rectangle(contentSize.width / 4, contentSize.height - contentSize.height / 8, contentSize.width / 2, contentSize.height / 16));
			}
			else {
				StringDraw.drawMaxString(g, "Don't let the bad guys go to the bottom!!!", new Rectangle(contentSize.width / 4, contentSize.height / 16, contentSize.width / 2, contentSize.height / 8));
				StringDraw.drawMaxString(g, "Shoot by clicking.", new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 16));
				StringDraw.drawMaxString(g, "Shoot at the bad guys to reduce their lives.", new Rectangle(contentSize.width / 4, contentSize.height / 4 + contentSize.height / 8, contentSize.width / 2, contentSize.height / 16));
				StringDraw.drawMaxString(g, "Shoot at the coins to collect them.", new Rectangle(contentSize.width / 4, contentSize.height / 2, contentSize.width / 2, contentSize.height / 16));
				
				int lineWidth = contentSize.width / 256;
				g.setStroke(new BasicStroke(lineWidth));
				Rectangle baseAndBarrelRect = new Rectangle(contentSize.width / 2 - contentSize.width / 16 + lineWidth / 2, contentSize.height - contentSize.width * 3 / 32 + lineWidth / 2, contentSize.width / 8 - lineWidth, contentSize.width * 3 / 32 - lineWidth);
				g.draw(baseAndBarrelRect);
				StringDraw.drawMaxString(g, lineWidth * 2, "Your barrel and its base.", baseAndBarrelRect);
				StringDraw.drawMaxString(g, lineWidth * 4, "You can shoot only if your barrel is fully loaden (that's when its base is green).", TextAlign.UP, new Rectangle(baseAndBarrelRect.x + baseAndBarrelRect.width, baseAndBarrelRect.y, contentSize.width - baseAndBarrelRect.x - baseAndBarrelRect.width, baseAndBarrelRect.height));
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (this.showingHelp) {
			this.secondHelpPage = true;
		}
		else {
			if (!this.showingStage) {
				this.game.clicked(event.getX(), event.getY(), this.contentSize);
			}
		}
	}

	@Override
	public void update(int time) {
		if (!this.showingStage && !this.showingHelp) {
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
				else {
					this.game.onMiddleChangingStage();
				}
			}
		}
	}

	@Override
	public void getCloseReady() {
		int achievedStage = this.game.getExitData().stage;
		int reachedCheckpoint = (int) Math.floor(achievedStage / (float) 5);
		if (reachedCheckpoint > Main.maxReachedStage) {
			Main.maxReachedStage = reachedCheckpoint;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (this.showingHelp) {
			if (event.getKeyCode() == KeyEvent.VK_H || event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.showingHelp = false;
			}
		}
		else {
			if (event.getKeyCode() == KeyEvent.VK_H) {
				if (!this.showingStage) {
					this.showingHelp = true;
					this.secondHelpPage = false;
				}
			}
		}
	}
}
