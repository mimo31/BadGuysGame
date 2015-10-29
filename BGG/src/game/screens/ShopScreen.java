package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.IOException;

import game.Barrel;
import game.IO;
import game.Main;
import game.PaintUtils;
import game.Screen;
import game.StringDraw;
import game.Barrel.BarrelGameProperty;
import game.StringDraw.TextAlign;

public class ShopScreen extends Screen {
	
	public static final Color GREEN = new Color(114, 241, 46);
	public static final Color DARK_GREEN = GREEN.darker();
	
	//Components
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
	private Graphics2D g;
	private Dimension contentSize;
	
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
			int[] leftTrigYs = this.rightArrowTriangle.ypoints;
			leftTrigYs[0] = leftArrowButton.height * 3 / 4;
			leftTrigYs[1] = leftArrowButton.height / 4;
			leftTrigYs[2] = leftArrowButton.height / 2;
			
			int[] rightTrigXs = this.rightArrowTriangle.xpoints;
			rightTrigXs[0] = contentSize.width - rightArrowButton.width * 3 / 4;
			rightTrigXs[1] = contentSize.width - leftArrowButton.width / 4;
			rightTrigXs[2] = contentSize.width - leftArrowButton.width * 3 / 4;
			int[] rightTrigYs = this.rightArrowTriangle.ypoints;
			rightTrigYs[0] = leftArrowButton.height * 3 / 4;
			rightTrigYs[1] = leftArrowButton.height / 2;
			rightTrigYs[2] = leftArrowButton.height / 4;
			
			float rowHeight = contentSize.height / 8;
			for (int i = 0; i < 3; i++) {
				int rowY = (int) ((i + 2)*rowHeight);
				this.propertyNameBounds[i].setBounds(0, rowY, contentSize.width / 2, (int) rowHeight);
				this.propertyValueBounds[i].setBounds(contentSize.width / 2, rowY, contentSize.width / 4, (int) rowHeight);
				this.propertyUpgradeBounds[i].setBounds(contentSize.height * 3 / 4, rowY, contentSize.width / 4, (int) rowHeight);
			}
			this.tableBordersSize = contentSize.height / 32;
			
			this.barrelNameBounds.setBounds(0, (int) rowHeight, contentSize.width, (int) rowHeight);
			
			this.coinIconSize = contentSize.height / 16;
			this.coinIconX = (int) (contentSize.width - this.coinIconSize / 2 - this.coinIconSize);
			this.coinIconY = (int) (contentSize.height - this.coinIconSize / 2 - this.coinIconSize);
			
			this.moneySignBounds.setBounds(0, contentSize.height * 7 / 8, contentSize.width / 2, (int) rowHeight);
			this.moneyAmountBounds.setBounds(contentSize.width / 2, contentSize.height * 7 / 8, contentSize.width / 2 - this.coinIconSize * 2, (int) rowHeight);
		}
	}
	
	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(g, contentSize);
		g.setColor(Color.black);
		g.fillRect(0, contentSize.height / 8 - contentSize.height / 256, contentSize.width, contentSize.height / 256);
		PaintUtils.drawChangingRect(g, this.leftArrowButton, GREEN, DARK_GREEN, mousePosition);
		if (leftArrowButton.contains(mousePosition)) {
			g.setColor(Color.yellow);
		}
		else {
			g.setColor(Color.white);
		}
		g.fillPolygon(this.leftArrowTriangle);
		PaintUtils.drawChangingRect(g, rightArrowButton, GREEN, DARK_GREEN, mousePosition);
		if (rightArrowButton.contains(mousePosition)) {
			g.setColor(Color.yellow);
		}
		else {
			g.setColor(Color.white);
		}
		g.fillPolygon(rightArrowTriangle);
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
		StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.getLoadingSpeed()), this.propertyValueBounds[0]);
		StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.getProjectilePower()), this.propertyValueBounds[1]);
		StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedBarrel.getProjectileSpeed()), this.propertyValueBounds[2]);
		for (int i = 0; i < 3; i++) {
			StringDraw.drawMaxString(g, this.tableBordersSize, getLastColumnText(selectedBarrel.gameProperties[i]), this.propertyUpgradeBounds[i]);
		}
		g.drawImage(IO.getTexture("BasicCoin.png", this.coinIconSize), this.coinIconX, this.coinIconY, null);
		StringDraw.drawMaxString(g, this.tableBordersSize, "Total money", TextAlign.LEFT, this.moneySignBounds);
		g.setColor(DARK_GREEN);
		StringDraw.drawMaxString(g, this.tableBordersSize, String.valueOf(Main.money), TextAlign.RIGHT, this.moneyAmountBounds);
	}
	
	private static String toString(float f) {
		if (f == (int)f) {
			return String.valueOf((int)f);
		}
		else {
			return String.valueOf(f);
		}
	}

	private static String getLastColumnText(BarrelGameProperty barrelProperties) {
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
}
