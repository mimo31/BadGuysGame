package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import game.Gui;
import game.Main;
import game.PaintUtils;
import game.Screen;
import game.StringDraw;
import game.StringDraw.TextAlign;

public final class StartScreen extends Screen {
	
	//Components
	private Rectangle playButton;
	private Rectangle shopButton;
	private Point usedMousePosition;
	
	private void updateComponenets(Dimension contentSize, Point mousePosition) {
		this.playButton = new Rectangle(contentSize.width / 4, contentSize.height / 8, contentSize.width / 2, contentSize.height / 4);
		this.shopButton = new Rectangle(contentSize.width / 4, contentSize.height / 2 + contentSize.height / 8, contentSize.width / 2, contentSize.height / 4);
		if (Main.showingStage) {
			usedMousePosition = Main.showingStageMousePos;
		}
		else {
			usedMousePosition = mousePosition;
		}
	}
	
	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) {
		updateComponenets(contentSize, mousePosition);
		PaintUtils.drawChangingRect(g, playButton, Color.black, Color.MAGENTA, usedMousePosition);
		PaintUtils.drawChangingRect(g, shopButton, Color.black, Color.MAGENTA, usedMousePosition);
		g.setColor(Color.white);
		StringDraw.drawMaxString(g, contentSize.height / 16, "Play", TextAlign.MIDDLE, playButton);
		StringDraw.drawMaxString(g, contentSize.height / 16, "Shop", TextAlign.MIDDLE, shopButton);
		if (Main.showingStage) {
			if (Main.gameOver) {
				PaintUtils.drawStage(g, contentSize, "Game Over");
			}
			else if (Main.noMoreStages) {
				PaintUtils.drawStage(g, contentSize, "No more stages :{");
			}
			else {
				PaintUtils.drawStage(g, contentSize, "Stage " + String.valueOf(Main.currentStage));
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		if (!Main.showingStage) {
			if (this.playButton.contains(event.getPoint())) {
				Main.showingStage = true;
				Main.showingStageMousePos = Gui.getMousePanePosition();
				Main.showingStageState = 0;
				Main.timeInStage = 0;
			}
			else if (this.shopButton.contains(event.getPoint())) {
				Screen.startNew(new ShopScreen());
			}
		}
	}
	
	@Override
	public void update() {
		if (Main.showingStage) {
			Main.showingStageState++;
			if (Main.showingStageState == 60) {
				Screen.startNew(new GameScreen());
			}
			else if (Main.showingStageState == 120) {
				Main.gameOver = false;
				Main.noMoreStages = false;
				Main.showingStage = false;
			}
		}
	}
}
