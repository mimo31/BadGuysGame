package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.io.IOException;

import game.Gui;
import game.Main;
import game.PaintUtils;
import game.Screen;
import game.StringDraw;
import game.StringDraw.TextAlign;
import game.barrels.Barrel;
import game.barrels.BarrelUpgradablePropertyImplementation;
import game.io.ResourceHandler;

public final class ShopScreen extends Screen {

	public static final Color GREEN = new Color(114, 241, 46);
	public static final Color DARK_GREEN = GREEN.darker();
	public static final Color CITRINE = new Color(228, 208, 10);
	public static final Color DARK_RED = new Color(170, 56, 30);
	public static final Color CHAMPAGNE = new Color(247, 231, 206);

	private float barrelsListPosition = 0;
	private boolean notBoughtWarning = false;
	private int notBoughtStage = 0;
	private float[] listsPosition = new float[Main.barrels.length];

	// Components
	private Rectangle leftArrowButton;
	private Rectangle rightArrowButton;
	private Polygon leftArrowTriangle;
	private Polygon rightArrowTriangle;
	private Rectangle barrelNameBounds;
	private int tableBordersSize;
	private int coinIconX;
	private int coinIconY;
	private int coinIconSize;
	private Rectangle moneySignBounds;
	private Rectangle moneyAmountBounds;
	private int barrelsSize;
	private Rectangle buyButton;
	private boolean onLeftButton;
	private boolean onRightButton;
	private Dimension contentSize;
	private Graphics2D g;

	@Override
	public void onStart() {
		this.initializeComponents();
	}

	private void initializeComponents() {
		this.contentSize = new Dimension();
		this.leftArrowButton = new Rectangle();
		this.rightArrowButton = new Rectangle();

		this.leftArrowTriangle = new Polygon();
		this.leftArrowTriangle.npoints = 3;
		this.leftArrowTriangle.xpoints = new int[3];
		this.leftArrowTriangle.ypoints = new int[3];

		this.rightArrowTriangle = new Polygon();
		this.rightArrowTriangle.npoints = 3;
		this.rightArrowTriangle.xpoints = new int[3];
		this.rightArrowTriangle.ypoints = new int[3];

		this.barrelNameBounds = new Rectangle();

		this.moneySignBounds = new Rectangle();
		this.moneyAmountBounds = new Rectangle();
		this.buyButton = new Rectangle();
	}

	private void updateComponents(Graphics2D g, Dimension contentSize) {
		this.g = g;
		if (!(contentSize.width == this.contentSize.width && contentSize.height == this.contentSize.height)) {
			this.contentSize = contentSize;
			this.leftArrowButton.setBounds(0, 0, contentSize.height / 16 - contentSize.height / 256, contentSize.height / 8 - contentSize.height / 256);
			this.rightArrowButton.setBounds(contentSize.width - leftArrowButton.width, 0, leftArrowButton.width, leftArrowButton.height);

			int[] leftTrigXs = this.leftArrowTriangle.xpoints;
			leftTrigXs[0] = this.leftArrowButton.width * 3 / 4;
			leftTrigXs[1] = this.leftArrowButton.width * 3 / 4;
			leftTrigXs[2] = this.leftArrowButton.width / 4;
			int[] leftTrigYs = this.leftArrowTriangle.ypoints;
			leftTrigYs[0] = this.leftArrowButton.height * 3 / 4;
			leftTrigYs[1] = this.leftArrowButton.height / 4;
			leftTrigYs[2] = this.leftArrowButton.height / 2;

			int[] rightTrigXs = this.rightArrowTriangle.xpoints;
			rightTrigXs[0] = contentSize.width - this.leftArrowButton.width * 3 / 4;
			rightTrigXs[1] = contentSize.width - this.leftArrowButton.width / 4;
			rightTrigXs[2] = contentSize.width - this.leftArrowButton.width * 3 / 4;
			int[] rightTrigYs = this.rightArrowTriangle.ypoints;
			rightTrigYs[0] = this.leftArrowButton.height * 3 / 4;
			rightTrigYs[1] = this.leftArrowButton.height / 2;
			rightTrigYs[2] = this.leftArrowButton.height / 4;

			float rowHeight = contentSize.height / 8;
			this.tableBordersSize = contentSize.height / 32;

			this.barrelNameBounds.setBounds(0, (int) rowHeight, contentSize.width, (int) rowHeight);

			this.coinIconSize = contentSize.height / 16;
			this.coinIconX = (int) (contentSize.width - this.coinIconSize / 2 - this.coinIconSize);
			this.coinIconY = (int) (contentSize.height - this.coinIconSize / 2 - this.coinIconSize);

			this.moneySignBounds.setBounds(0, contentSize.height * 7 / 8, contentSize.width / 2, (int) rowHeight);
			this.moneyAmountBounds.setBounds(contentSize.width / 2, contentSize.height * 7 / 8, contentSize.width / 2 - this.coinIconSize * 2, (int) rowHeight);

			this.barrelsSize = this.leftArrowButton.height;
			this.buyButton.setBounds(0, (int) (rowHeight * 5), contentSize.width, (int) (rowHeight * 2));
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(g, contentSize);

		// Draws the barrel's properties interface
		Barrel selectedBarrel = Main.getSelectedBarrel();
		int maxShownIndex = (int) Math.ceil(this.listsPosition[Main.selectedBarrel]) + 3;
		if (selectedBarrel.bought) {
			maxShownIndex += 2;
		}
		for (int i = (int) Math.floor(this.listsPosition[Main.selectedBarrel]); i < maxShownIndex && i < selectedBarrel.gameProperties.length; i++) {
			g.setColor(Color.orange);
			int rowAbsoluteY = (int) ((i - this.listsPosition[Main.selectedBarrel] + 2) * contentSize.height / 8);
			if (selectedBarrel.gameProperties[i] instanceof BarrelUpgradablePropertyImplementation) {
				BarrelUpgradablePropertyImplementation upgradableImplementation = (BarrelUpgradablePropertyImplementation) selectedBarrel.gameProperties[i];
				float fractionToFill = upgradableImplementation.getUpgradedDrawnFraction();
				if (fractionToFill != 0) {
					g.fillRect(0, rowAbsoluteY, (int) (contentSize.width * fractionToFill / 2), contentSize.height / 8);
				}
				Rectangle upgradeColumnRect = new Rectangle(contentSize.width * 3 / 4, rowAbsoluteY, contentSize.width / 4, contentSize.height / 8);
				if (selectedBarrel.bought) {
					if (upgradableImplementation.isFullyUpgraded()) {
						g.setColor(Color.black);
						StringDraw.drawMaxString(g, this.tableBordersSize, "maxed", upgradeColumnRect);
					}
					else {
						PaintUtils.drawChangingRect(g, upgradeColumnRect, Color.cyan, Color.red, mousePosition);
						g.setColor(Color.black);
						StringDraw.drawMaxString(g, this.tableBordersSize, getLastColumnTextBought(upgradableImplementation), upgradeColumnRect);
					}
				}
				else {
					g.setColor(Color.black);
					StringDraw.drawMaxString(g, this.tableBordersSize, getLastColumnTextNotBought(upgradableImplementation), upgradeColumnRect);
				}
			}
			Rectangle nameColumnRect = new Rectangle(0, rowAbsoluteY, contentSize.width / 2, contentSize.height / 8);
			Rectangle valueColumnRect = new Rectangle(contentSize.width / 2, rowAbsoluteY, contentSize.width / 4, contentSize.height / 8);
			g.setColor(Color.black);
			StringDraw.drawMaxString(g, this.tableBordersSize, selectedBarrel.gameProperties[i].propertyType.name, TextAlign.LEFT, nameColumnRect);
			StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.gameProperties[i].getActualValue()), valueColumnRect);
		}

		g.setColor(Gui.gui.getBackground());
		g.fillRect(0, 0, contentSize.width, contentSize.height / 4);
		if (selectedBarrel.bought) {
			g.fillRect(0, contentSize.height * 7 / 8, contentSize.width, contentSize.height / 8);
		}
		g.setColor(Color.black);
		StringDraw.drawMaxString(g, this.tableBordersSize, selectedBarrel.name, this.barrelNameBounds);

		int maxBarrelIndex = (int) Math.floor(this.barrelsListPosition + (contentSize.width - contentSize.height / 8) / (float) (contentSize.height / 8 - contentSize.height / 256));
		for (int i = (int) Math.floor(this.barrelsListPosition); i < Main.barrels.length && i <= maxBarrelIndex; i++) {
			this.drawBarrel(i);
		}
		g.setColor(Color.black);
		g.fillRect(0, contentSize.height / 8 - contentSize.height / 256, contentSize.width, contentSize.height / 256);
		g.fillRect(this.leftArrowButton.width, 0, contentSize.height / 256, this.leftArrowButton.height);
		g.fillRect(this.rightArrowButton.x - contentSize.height / 256, 0, contentSize.height / 256, this.rightArrowButton.height);
		PaintUtils.drawChangingRect(g, this.leftArrowButton, GREEN, DARK_GREEN, mousePosition);
		if (this.leftArrowButton.contains(mousePosition)) {
			if (this.onLeftButton) {
				g.setColor(Color.red);
			}
			else {
				g.setColor(Color.yellow);
			}
		}
		else {
			g.setColor(Color.white);
		}
		g.fillPolygon(this.leftArrowTriangle);
		PaintUtils.drawChangingRect(g, this.rightArrowButton, GREEN, DARK_GREEN, mousePosition);
		if (this.rightArrowButton.contains(mousePosition)) {
			if (this.onRightButton) {
				g.setColor(Color.red);
			}
			else {
				g.setColor(Color.yellow);
			}
		}
		else {
			g.setColor(Color.white);
		}
		g.fillPolygon(this.rightArrowTriangle);

		// Draws the buy button
		if (!selectedBarrel.bought) {
			PaintUtils.drawChangingRect(g, this.buyButton, DARK_RED, CHAMPAGNE, mousePosition);
			Color textColor;
			if (this.buyButton.contains(mousePosition)) {
				textColor = Color.black;
			}
			else {
				textColor = Color.white;
			}
			g.setColor(textColor);
			StringDraw.drawMaxString(g, this.buyButton.height / 4, "Buy - " + String.valueOf(selectedBarrel.cost) + " coins", this.buyButton);
		}

		// Draws the amount of money and the corresponding sign at the bottom
		g.setColor(Color.black);
		g.drawImage(ResourceHandler.getTexture("BasicCoin.png", this.coinIconSize), this.coinIconX, this.coinIconY, null);
		StringDraw.drawMaxString(g, this.tableBordersSize, "Total money", TextAlign.LEFT, this.moneySignBounds);
		g.setColor(PaintUtils.DARK_GREEN2);
		StringDraw.drawMaxString(g, this.tableBordersSize, String.valueOf(Main.money), TextAlign.RIGHT, this.moneyAmountBounds);
	}

	private void drawBarrel(int index) throws IOException {
		int x = (int) ((this.barrelsSize) * (index - this.barrelsListPosition)) + this.contentSize.height / 16;
		this.g.drawImage(ResourceHandler.getTexture(Main.barrels[index].textureName, (int) this.barrelsSize), x, 0, null);
		if (!Main.barrels[index].bought) {
			this.g.setColor(PaintUtils.TRANSPARENT_GRAY);
			this.g.fillRect(x, 0, this.barrelsSize, this.barrelsSize);
		}
		if (index == Main.selectedBarrel) {
			float cornerSize = this.barrelsSize / (float) 16;
			Area wholeBarrel = new Area(new Rectangle(x, 0, this.barrelsSize, this.barrelsSize));
			wholeBarrel.subtract(new Area(new Rectangle((int) (x + cornerSize), (int) cornerSize, (int) (this.barrelsSize - cornerSize * 2), (int) (this.barrelsSize - cornerSize * 2))));
			this.g.setColor(CITRINE);
			g.fill(wholeBarrel);
			if (this.notBoughtWarning) {
				this.g.setColor(new Color(255, 0, 0, (int) (this.notBoughtStage / (float) 30 * 255)));
				this.g.fillRect(x, 0, this.barrelsSize, this.barrelsSize);
			}
		}
	}

	private static String toString(float f) {
		if (f == (int) f) {
			return String.valueOf((int) f);
		}
		else {
			return String.valueOf(f);
		}
	}

	private static String getLastColumnTextNotBought(BarrelUpgradablePropertyImplementation barrelProperties) {
		return "best " + toString(barrelProperties.getMaxValue()) + "(" + String.valueOf(barrelProperties.getMaxCost()) + "c)";
	}

	private static String getLastColumnTextBought(BarrelUpgradablePropertyImplementation barrelProperties) {
		if (barrelProperties.isFullyUpgraded()) {
			return "maxed";
		}
		else {
			float upgradeValue = barrelProperties.getUpgradeValue();
			String valuePart;
			if (upgradeValue > 0) {
				valuePart = "+" + toString(upgradeValue);
			}
			else if (upgradeValue < 0) {
				valuePart = toString(upgradeValue);
			}
			else {
				valuePart = "±0";
			}
			return valuePart + "(" + String.valueOf(barrelProperties.getUpgradeCost()) + "c)";
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		int bottomYBound;
		if (Main.getSelectedBarrel().bought) {
			bottomYBound = this.contentSize.height * 7 / 8;
		}
		else {
			bottomYBound = this.contentSize.height * 5 / 8;
		}
		if (event.getY() < bottomYBound && event.getY() >= this.contentSize.height / 4) {
			this.listsPosition[Main.selectedBarrel] += event.getWheelRotation() / (float) 4;
			if (this.listsPosition[Main.selectedBarrel] < 0) {
				this.listsPosition[Main.selectedBarrel] = 0;
			}
			else if (this.listsPosition[Main.selectedBarrel] > Main.getSelectedBarrel().gameProperties.length - 2) {
				this.listsPosition[Main.selectedBarrel] = Main.getSelectedBarrel().gameProperties.length - 2;
			}
		}
	};

	@Override
	public void update() {
		for (int i = 0; i < Main.barrels.length; i++) {
			Main.barrels[i].update();
		}
		if (this.notBoughtWarning) {
			this.notBoughtStage--;
			if (this.notBoughtStage == 0) {
				this.notBoughtWarning = false;
			}
		}
		if (this.onLeftButton) {
			this.barrelsListPosition += 1 / (float) 16;
			if (this.barrelsListPosition > Main.barrels.length - 1) {
				this.barrelsListPosition = Main.barrels.length - 1;
			}
		}
		else if (this.onRightButton) {
			this.barrelsListPosition -= 1 / (float) 16;
			if (this.barrelsListPosition < 0) {
				this.barrelsListPosition = 0;
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (Main.getSelectedBarrel().bought) {
				for (int i = 0; i < Main.barrels.length; i++) {
					Main.barrels[i].forceUpgrade();
				}
				Screen.startNew(new StartScreen());
			}
			else {
				this.notBoughtWarning = true;
				this.notBoughtStage = 30;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (event.getX() >= this.contentSize.height / 16 && event.getX() < this.contentSize.width - this.contentSize.height / 16 && event.getY() < this.barrelsSize) {
			float relListClickPosition = this.barrelsListPosition + (event.getX() - this.contentSize.height / 16) / (float) this.barrelsSize;
			int clickedBarrelIndex = (int) Math.floor(relListClickPosition);
			if (clickedBarrelIndex < Main.barrels.length) {
				Main.selectedBarrel = clickedBarrelIndex;
				this.notBoughtWarning = false;
			}
		}
		// Handles all the clicks on the properties interface
		else if (event.getY() >= this.contentSize.height / 4 && event.getY() < this.contentSize.height / 8 * 7) {
			Barrel selectedBarrel = Main.getSelectedBarrel();
			boolean clickedOnUpgrade = false;
			if (!selectedBarrel.bought) {
				if (event.getY() >= this.contentSize.height * 5 / 8) {
					if (this.buyButton.contains(event.getPoint())) {
						if (selectedBarrel.cost <= Main.money) {
							Main.money -= selectedBarrel.cost;
							selectedBarrel.bought = true;
						}
					}
				}
				else if (event.getX() >= this.contentSize.width * 3 / 4) {
					clickedOnUpgrade = true;
				}
			}
			else {
				if (event.getX() >= this.contentSize.width * 3 / 4) {
					clickedOnUpgrade = true;
				}
			}
			if (clickedOnUpgrade) {
				int clickedIndex = (int) ((event.getY() - this.contentSize.height / 4) / (float) (this.contentSize.height / 8) + this.listsPosition[Main.selectedBarrel]);
				if (selectedBarrel.gameProperties[clickedIndex] instanceof BarrelUpgradablePropertyImplementation) {
					BarrelUpgradablePropertyImplementation upgradableImplementation = (BarrelUpgradablePropertyImplementation) selectedBarrel.gameProperties[clickedIndex];
					if (!upgradableImplementation.isFullyUpgraded()) {
						int upgradeCost = upgradableImplementation.getUpgradeCost();
						if (Main.money >= upgradeCost) {
							Main.money -= upgradeCost;
							upgradableImplementation.upgrade();
						}
					}
				}
			}
		}
		else if (this.buyButton.contains(event.getPoint()) && !Main.getSelectedBarrel().bought) {
			Barrel selectedBarrel = Main.getSelectedBarrel();
			if (selectedBarrel.cost <= Main.money) {
				Main.money -= selectedBarrel.cost;
				selectedBarrel.bought = true;
			}
		}
		else if (this.leftArrowButton.contains(event.getPoint())) {
			this.onLeftButton = true;
		}
		else if (this.rightArrowButton.contains(event.getPoint())) {
			this.onRightButton = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		this.onLeftButton = false;
		this.onRightButton = false;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (this.onLeftButton) {
			if (!this.leftArrowButton.contains(event.getPoint())) {
				this.onLeftButton = false;
			}
		}
		if (this.onRightButton) {
			if (!this.rightArrowButton.contains(event.getPoint())) {
				this.onRightButton = false;
			}
		}
	}
	
	@Override
	public void getCloseReady() {
		if (!Main.getSelectedBarrel().bought) {
			Main.selectedBarrel = 0;
		}
	}
}
