package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import game.Main;
import game.PaintUtils;
import game.Screen;
import game.StringDraw;

public class ChooseStageScreen extends Screen {

	private float showingStageState = 0;
	private boolean showingStage = false;
	private int selectedStage;

	// Components
	private Dimension contentSize;
	private Point usedMousePosition;

	private float listPosition;

	private void updateComponents(Dimension contentSize, Point mousePosition) {
		this.contentSize = contentSize;
		if (!this.showingStage) {
			this.usedMousePosition = mousePosition;
		}
	}

	private void initializeComponents() {
		this.contentSize = new Dimension();
	}

	@Override
	public void onStart() {
		this.initializeComponents();
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(contentSize, mousePosition);
		for (int i = (int) Math.floor((double) this.listPosition); i <= Main.maxReachedStage && i <= Math.floor(this.listPosition + 4); i++) {
			Rectangle bounds = new Rectangle(contentSize.width / 4, (int) ((i - this.listPosition) * contentSize.height / 4 + contentSize.height / 16), contentSize.width / 2, contentSize.height / 8);
			PaintUtils.drawChangingRect(g, bounds, Color.blue, Color.red, usedMousePosition);
			String text;
			if (i == 0) {
				text = "The very beginning";
			}
			else {
				text = "Stage " + String.valueOf(i * 5);
			}
			g.setColor(Color.white);
			StringDraw.drawMaxString(g, contentSize.height / 32, text, bounds);
		}
		PaintUtils.drawCurrentMoney(g, contentSize);
		if (this.showingStage) {
			PaintUtils.drawStage(g, contentSize, "Stage " + String.valueOf(this.selectedStage), showingStageState);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (!this.showingStage) {
			if (event.getWheelRotation() > 0) {
				if (this.listPosition != Main.maxReachedStage - 2) {
					this.listPosition += event.getWheelRotation() / (float) 8;
					if (this.listPosition > Main.maxReachedStage - 2) {
						this.listPosition = Main.maxReachedStage - 2;
					}
					if (this.listPosition < 0) {
						this.listPosition = 0;
					}
				}
			}
			else {
				if (this.listPosition != 0) {
					this.listPosition += event.getWheelRotation() / (float) 8;
					if (this.listPosition < 0) {
						this.listPosition = 0;
					}
				}
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!this.showingStage) {
			if (event.getX() >= contentSize.width / 4 && event.getX() < contentSize.width * 3 / 4) {
				float relListClickPosition = this.listPosition + event.getY() / (float) this.contentSize.height * 4;
				if (Math.floor(relListClickPosition) + 1 / (float) 4 <= relListClickPosition && Math.floor(relListClickPosition + 1) - 1 / (float) 4 > relListClickPosition && Math.floor(relListClickPosition) <= Main.maxReachedStage) {
					this.selectedStage = (int) Math.floor(relListClickPosition) * 5;
					this.showingStage = true;
					this.usedMousePosition = event.getPoint();
					this.showingStageState = 0;
				}
			}
		}
	}

	@Override
	public void update(int time) {
		if (this.showingStage) {
			this.showingStageState += time / (float) 40;
			if (this.showingStageState >= Main.stageShowTime / 2) {
				Screen.startNew(new GameScreen(this.selectedStage, this.usedMousePosition));
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
}
