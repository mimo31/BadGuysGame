package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.IOException;

import game.Barrel;
import game.Main;
import game.PaintUtils;
import game.Screen;
import game.StringDraw;
import game.Barrel.BarrelGameProperty;
import game.StringDraw.TextAlign;
import game.io.ResourceHandler;

public final class ShopScreen extends Screen {

	public static final Color GREEN = new Color(114, 241, 46);
	public static final Color DARK_GREEN = GREEN.darker();
	public static final Color TRANSPARENT_GRAY = new Color(127, 127, 127, 63);
	public static final Color CITRINE = new Color(228, 208, 10);
	public static final Color DARK_RED = new Color(170, 56, 30);
	public static final Color CHAMPAGNE = new Color(247, 231, 206);

	private float barrelsListPosition = 0;
	private boolean notBoughtWarning = false;
	private int notBoughtStage = 0;

	// Components
	private Rectangle leftArrowButton;
	private Rectangle rightArrowButton;
	private Polygon leftArrowTriangle;
	private Polygon rightArrowTriangle;
	private Rectangle[] propertyNameBounds;
	private Rectangle[] propertyValueBounds;
	private Rectangle[] propertyUpgradeBounds;
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

		this.propertyNameBounds = new Rectangle[3];
		this.propertyValueBounds = new Rectangle[3];
		this.propertyUpgradeBounds = new Rectangle[3];
		for (int i = 0; i < 3; i++) {
			this.propertyNameBounds[i] = new Rectangle();
			this.propertyValueBounds[i] = new Rectangle();
			this.propertyUpgradeBounds[i] = new Rectangle();
		}

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
			for (int i = 0; i < 3; i++) {
				int rowY = (int) ((i + 2) * rowHeight);
				this.propertyNameBounds[i].setBounds(0, rowY, contentSize.width / 2, (int) rowHeight);
				this.propertyValueBounds[i].setBounds(contentSize.width / 2, rowY, contentSize.width / 4, (int) rowHeight);
				this.propertyUpgradeBounds[i].setBounds(contentSize.width * 3 / 4, rowY, contentSize.width / 4, (int) rowHeight);
			}
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
		Barrel selectedBarrel = Main.getSelectedBarrel();
		g.setColor(Color.orange);
		for (int i = 0; i < 3; i++) {
			float fractionToFill = selectedBarrel.gameProperties[i].getUpgradedDrawnFraction();
			if (fractionToFill != 0) {
				g.fillRect(0, (i + 2) * contentSize.height / 8, (int) (contentSize.width * fractionToFill / 2), contentSize.height / 8);
			}
		}
		g.setColor(Color.black);
		StringDraw.drawMaxString(g, this.tableBordersSize, Main.getSelectedBarrel().name, this.barrelNameBounds);
		StringDraw.drawMaxString(g, this.tableBordersSize, "Loading time", TextAlign.LEFT, this.propertyNameBounds[0]);
		StringDraw.drawMaxString(g, this.tableBordersSize, "Projectile power", TextAlign.LEFT, this.propertyNameBounds[1]);
		StringDraw.drawMaxString(g, this.tableBordersSize, "Projectile speed", TextAlign.LEFT, this.propertyNameBounds[2]);
		StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.getLoadingTime()), this.propertyValueBounds[0]);
		StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.getProjectilePower()), this.propertyValueBounds[1]);
		StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.getProjectileSpeed()), this.propertyValueBounds[2]);
		if (selectedBarrel.bought) {
			for (int i = 0; i < 3; i++) {
				if (!selectedBarrel.gameProperties[i].isFullyUpgraded()) {
					PaintUtils.drawChangingRect(g, this.propertyUpgradeBounds[i], Color.cyan, Color.red, mousePosition);
				}
			}
			g.setColor(Color.black);
			for (int i = 0; i < 3; i++) {
				StringDraw.drawMaxString(g, this.tableBordersSize, getLastColumnTextBought(selectedBarrel.gameProperties[i]), this.propertyUpgradeBounds[i]);
			}
		}
		else {
			g.setColor(Color.black);
			for (int i = 0; i < 3; i++) {
				StringDraw.drawMaxString(g, this.tableBordersSize, getLastColumnTextNotBought(selectedBarrel.gameProperties[i]), this.propertyUpgradeBounds[i]);
			}
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
			this.g.setColor(TRANSPARENT_GRAY);
			this.g.fillRect(x, 0, this.barrelsSize, this.barrelsSize);
		}
		if (index == Main.selectedBarrel) {
			float cornerSize = this.barrelsSize / (float) 16;
			Area wholeBarrel = new Area(new Rectangle(x, 0, this.barrelsSize, this.barrelsSize));
			wholeBarrel.subtract(new Area(new Rectangle((int) (x + cornerSize), (int) cornerSize, (int) (this.barrelsSize - cornerSize * 2), (int) (this.barrelsSize - cornerSize * 2))));
			this.g.setColor(CITRINE);
			g.fill(wholeBarrel);
			if (this.notBoughtWarning) {
				this.g.setColor(new Color(255, 0, 0, (int)(this.notBoughtStage / (float) 30 * 255)));
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

	private static String getLastColumnTextNotBought(BarrelGameProperty barrelProperties) {
		return "best " + toString(barrelProperties.gatMaxValue()) + "(" + String.valueOf(barrelProperties.getMaxCost()) + "c)";
	}

	private static String getLastColumnTextBought(BarrelGameProperty barrelProperties) {
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
		else if (event.getX() >= this.propertyUpgradeBounds[0].x && event.getY() >= this.propertyUpgradeBounds[0].y && event.getY() < this.propertyUpgradeBounds[2].y + this.propertyUpgradeBounds[2].height) {
			Barrel selectedBarrel = Main.getSelectedBarrel();
			if (selectedBarrel.bought) {
				int clickedProperty;
				if (event.getY() < this.propertyUpgradeBounds[1].y) {
					clickedProperty = 0;
				}
				else if (event.getY() < this.propertyUpgradeBounds[2].y) {
					clickedProperty = 1;
				}
				else {
					clickedProperty = 2;
				}
				BarrelGameProperty gameProperty = selectedBarrel.gameProperties[clickedProperty];
				if (!gameProperty.isFullyUpgraded()) {
					int upgradeCost = gameProperty.getUpgradeCost();
					if (Main.money >= upgradeCost) {
						Main.money -= upgradeCost;
						gameProperty.upgrade();
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
}
