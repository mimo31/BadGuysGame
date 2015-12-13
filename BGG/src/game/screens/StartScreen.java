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
	private boolean showingStage;
	private boolean noMoreStages;
	private float showingStageState;
	private Point usedMousePosition;
	private StringDrawAttributes playTextAttributes;
	private StringDrawAttributes shopTextAttributes;
	private Dimension contentSize;
	
	public StartScreen() {
		
	}
	
	public StartScreen(boolean noMoreStages, Point showingStageMousePos) {
		this.usedMousePosition = showingStageMousePos;
		this.showingStage = true;
		this.noMoreStages = noMoreStages;
		this.showingStageState = Main.stageShowTime / 2;
	}
	
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
		if (!this.showingStage) {
			this.usedMousePosition = mousePosition;
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		updateComponents(g, contentSize, mousePosition);
		PaintUtils.drawChangingRect(g, playButton, Color.black, Color.MAGENTA, this.usedMousePosition);
		PaintUtils.drawChangingRect(g, shopButton, Color.black, Color.MAGENTA, this.usedMousePosition);
		g.setColor(Color.white);
		StringDraw.drawStringByAttributes(g, "Play", playTextAttributes);
		StringDraw.drawStringByAttributes(g, "Shop", shopTextAttributes);
		PaintUtils.drawCurrentMoney(g, contentSize);
		if (this.showingStage) {
			if (this.noMoreStages) {
				PaintUtils.drawStage(g, contentSize, "No more stages :{", this.showingStageState);
			}
			else {
				PaintUtils.drawStage(g, contentSize, "Game Over", this.showingStageState);
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
	public void mousePressed(MouseEvent event) {
		if (!this.showingStage) {
			if (this.playButton.contains(event.getPoint())) {
				Screen.startNew(new ChooseStageScreen());
			}
			else if (this.shopButton.contains(event.getPoint())) {
				Screen.startNew(new ShopScreen());
			}
		}
	}
}
