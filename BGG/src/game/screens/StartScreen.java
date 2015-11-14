package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;
import game.Screen;
import game.StringDraw;
import game.StringDraw.StringDrawAttributes;

public final class StartScreen extends Screen {

	// Components
	private Rectangle playButton;
	private Rectangle shopButton;
	private Point usedMousePosition;
	private StringDrawAttributes playTextAttributes;
	private StringDrawAttributes shopTextAttributes;
	private Dimension contentSize;

	@Override
	public void onStart() {
		this.initializeComponents();
	}

	private void initializeComponents() {
		this.playButton = new Rectangle();
		this.shopButton = new Rectangle();
		this.contentSize = new Dimension();
	}

	private void updateComponents(Graphics2D g, Dimension contentSize, Point mousePosition) {
		if (this.contentSize.width != contentSize.width || this.contentSize.height != contentSize.height) {
			this.contentSize = contentSize;
			this.playButton.setBounds(contentSize.width / 4, contentSize.height / 8, contentSize.width / 2, contentSize.height / 4);
			this.shopButton.setBounds(contentSize.width / 4, contentSize.height / 2 + contentSize.height / 8, contentSize.width / 2, contentSize.height / 4);
			this.playTextAttributes = StringDraw.getAttributes(g, contentSize.height / 16, "Play", playButton);
			this.shopTextAttributes = StringDraw.getAttributes(g, contentSize.height / 16, "Shop", shopButton);
		}
		if (Main.showingStage) {
			usedMousePosition = Main.showingStageMousePos;
		}
		else {
			usedMousePosition = mousePosition;
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		updateComponents(g, contentSize, mousePosition);
		PaintUtils.drawChangingRect(g, playButton, Color.black, Color.MAGENTA, usedMousePosition);
		PaintUtils.drawChangingRect(g, shopButton, Color.black, Color.MAGENTA, usedMousePosition);
		g.setColor(Color.white);
		StringDraw.drawStringByAttributes(g, "Play", playTextAttributes);
		StringDraw.drawStringByAttributes(g, "Shop", shopTextAttributes);
		PaintUtils.drawCurrentMoney(g, contentSize);
		if (Main.showingStage) {
			if (Main.gameOver) {
				PaintUtils.drawStage(g, contentSize, "Game Over");
			}
			else if (Main.noMoreStages) {
				PaintUtils.drawStage(g, contentSize, "No more stages :{");
			}
		}
	}

	@Override
	public void update() {
		if (Main.showingStage) {
			Main.showingStageState++;
			if (Main.showingStageState == Main.stageShowTime) {
				Main.gameOver = false;
				Main.noMoreStages = false;
				Main.showingStage = false;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!Main.showingStage) {
			if (this.playButton.contains(event.getPoint())) {
				Screen.startNew(new ChooseStageScreen());
			}
			else if (this.shopButton.contains(event.getPoint())) {
				Screen.startNew(new ShopScreen());
			}
		}
	}
}
