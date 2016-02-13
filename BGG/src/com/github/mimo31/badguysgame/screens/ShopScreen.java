package com.github.mimo31.badguysgame.screens;

import java.awt.BasicStroke;
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
import java.util.ArrayList;
import java.util.List;

import com.github.mimo31.badguysgame.Achievement;
import com.github.mimo31.badguysgame.Gui;
import com.github.mimo31.badguysgame.IntHolder;
import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.Statistics;
import com.github.mimo31.badguysgame.StringDraw;
import com.github.mimo31.badguysgame.StringDraw.TextAlign;
import com.github.mimo31.badguysgame.io.ResourceHandler;
import com.github.mimo31.badguysgame.mechanics.weaponry.UpgradablePropertyImplementation;
import com.github.mimo31.badguysgame.mechanics.weaponry.Weapon;

public final class ShopScreen extends Screen {

	public static final Color GREEN = new Color(114, 241, 46);
	public static final Color DARK_GREEN = GREEN.darker();
	public static final Color CITRINE = new Color(228, 208, 10);
	public static final Color DARK_RED = new Color(170, 56, 30);
	public static final Color CHAMPAGNE = new Color(247, 231, 206);

	private float weaponsListPosition = 0;
	private Weapon[] displayedWeapons;
	private int selectedWeapon;
	private IntHolder realSelectedWeapon;
	private Weapon[] realWeapons;
	private boolean notBoughtWarning = false;
	private float notBoughtStage = 0;
	private float[] listsPosition;
	private boolean showingHelp;
	private boolean secondHelpPage;

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
	private int weaponsSize;
	private Rectangle buyButton;
	private boolean onLeftButton;
	private boolean onRightButton;
	private Dimension contentSize;
	private Graphics2D g;
	private Point usedMousePosition = new Point(0, 0);

	public ShopScreen(Weapon[] weapons, IntHolder selectedWeapon, boolean offerNothing) {
		super();
		this.realWeapons = weapons;
		this.realSelectedWeapon = selectedWeapon;
		if (selectedWeapon.value == -1) {
			this.selectedWeapon = 0;
		}
		else {
			this.selectedWeapon = selectedWeapon.value;
			if (offerNothing) {
				this.selectedWeapon++;
			}
		}
		List<Weapon> weaponList = new ArrayList<Weapon>();
		if (offerNothing) {
			weaponList.add(null);
		}
		for (int i = 0; i < weapons.length; i++) {
			if (weapons[i].doDisplay()) {
				weaponList.add(weapons[i]);
			}
			else {
				if (i < selectedWeapon.value) {
					this.selectedWeapon--;
				}
			}
		}
		this.displayedWeapons = weaponList.toArray(new Weapon[weaponList.size()]);
		this.listsPosition = new float[this.displayedWeapons.length];
	}

	public ShopScreen() {
		this(Main.barrels, Main.selectedBarrel, false);
	}

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

	private void updateComponents(Graphics2D g, Dimension contentSize, Point mousePosition) {
		this.g = g;
		if (!this.showingHelp) {
			this.usedMousePosition = mousePosition;
		}
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

			this.weaponsSize = this.leftArrowButton.height;
			this.buyButton.setBounds(0, (int) (rowHeight * 5), contentSize.width, (int) (rowHeight * 2));
		}
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(g, contentSize, mousePosition);

		// Draws the weapon's properties interface
		Weapon selectedWeapon = this.displayedWeapons[this.selectedWeapon];
		if (selectedWeapon != null) {
			int maxShownIndex = (int) Math.ceil(this.listsPosition[this.selectedWeapon]) + 3;
			if (selectedWeapon.bought) {
				maxShownIndex += 2;
			}
			for (int i = (int) Math.floor(this.listsPosition[this.selectedWeapon]); i < maxShownIndex && i < selectedWeapon.gameProperties.length; i++) {
				g.setColor(Color.orange);
				int rowAbsoluteY = (int) ((i - this.listsPosition[this.selectedWeapon] + 2) * contentSize.height / 8);
				if (selectedWeapon.gameProperties[i] instanceof UpgradablePropertyImplementation) {
					UpgradablePropertyImplementation upgradableImplementation = (UpgradablePropertyImplementation) selectedWeapon.gameProperties[i];
					float fractionToFill = upgradableImplementation.getUpgradedDrawnFraction();
					if (fractionToFill != 0) {
						g.fillRect(0, rowAbsoluteY, (int) (contentSize.width * fractionToFill / 2), contentSize.height / 8);
					}
					Rectangle upgradeColumnRect = new Rectangle(contentSize.width * 3 / 4, rowAbsoluteY, contentSize.width / 4, contentSize.height / 8);
					if (selectedWeapon.bought) {
						if (upgradableImplementation.isFullyUpgraded()) {
							g.setColor(Color.black);
							StringDraw.drawMaxString(g, this.tableBordersSize, "maxed", upgradeColumnRect);
						}
						else {
							PaintUtils.drawChangingRect(g, upgradeColumnRect, Color.cyan, Color.red, this.usedMousePosition);
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
				StringDraw.drawMaxString(g, this.tableBordersSize, selectedWeapon.gameProperties[i].propertyType.name, TextAlign.LEFT, nameColumnRect);
				StringDraw.drawMaxString(g, this.tableBordersSize, toString(selectedWeapon.gameProperties[i].getActualValue()), valueColumnRect);
			}

			g.setColor(Gui.gui.getBackground());
			g.fillRect(0, 0, contentSize.width, contentSize.height / 4);
			if (selectedWeapon.bought) {
				g.fillRect(0, contentSize.height * 7 / 8, contentSize.width, contentSize.height / 8);
			}
		}
		g.setColor(Color.black);
		String nameText;
		if (selectedWeapon == null) {
			nameText = "Nothing selected";
		}
		else {
			nameText = selectedWeapon.name;
		}
		StringDraw.drawMaxString(g, this.tableBordersSize, nameText, this.barrelNameBounds);

		int maxWeaponIndex = (int) Math.floor(this.weaponsListPosition + (contentSize.width - contentSize.height / 8) / (float) (contentSize.height / 8 - contentSize.height / 256));
		for (int i = (int) Math.floor(this.weaponsListPosition); i < this.displayedWeapons.length && i <= maxWeaponIndex; i++) {
			this.drawWeapon(i);
		}
		g.setColor(Color.black);
		g.fillRect(0, contentSize.height / 8 - contentSize.height / 256, contentSize.width, contentSize.height / 256);
		g.fillRect(this.leftArrowButton.width, 0, contentSize.height / 256, this.leftArrowButton.height);
		g.fillRect(this.rightArrowButton.x - contentSize.height / 256, 0, contentSize.height / 256, this.rightArrowButton.height);
		PaintUtils.drawChangingRect(g, this.leftArrowButton, GREEN, DARK_GREEN, this.usedMousePosition);
		if (this.leftArrowButton.contains(this.usedMousePosition)) {
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
		PaintUtils.drawChangingRect(g, this.rightArrowButton, GREEN, DARK_GREEN, this.usedMousePosition);
		if (this.rightArrowButton.contains(this.usedMousePosition)) {
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
		if (selectedWeapon != null) {
			if (!selectedWeapon.bought) {
				PaintUtils.drawChangingRect(g, this.buyButton, DARK_RED, CHAMPAGNE, this.usedMousePosition);
				Color textColor;
				if (this.buyButton.contains(this.usedMousePosition)) {
					textColor = Color.black;
				}
				else {
					textColor = Color.white;
				}
				g.setColor(textColor);
				StringDraw.drawMaxString(g, this.buyButton.height / 4, "Buy - " + String.valueOf(selectedWeapon.cost) + " coins", this.buyButton);
			}
		}

		// Draws the amount of money and the corresponding sign at the
		// bottom
		g.setColor(Color.black);
		g.drawImage(ResourceHandler.getTexture("BasicCoin.png", this.coinIconSize), this.coinIconX, this.coinIconY, null);
		StringDraw.drawMaxString(g, this.tableBordersSize, "Total money", TextAlign.LEFT, this.moneySignBounds);
		g.setColor(PaintUtils.DARK_GREEN2);
		StringDraw.drawMaxString(g, this.tableBordersSize, String.valueOf(Main.money), TextAlign.RIGHT, this.moneyAmountBounds);

		if (this.showingHelp) {
			PaintUtils.paintGenericHelpScreen(g, contentSize);
			if (!this.secondHelpPage) {
				StringDraw.drawMaxString(g, "This is the shop.", new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 8));
				StringDraw.drawMaxString(g, "You can buy new weapons or upgrade the older ones.", new Rectangle(contentSize.width / 4, contentSize.height / 2, contentSize.width / 2, contentSize.height / 16));
				StringDraw.drawMaxString(g, "Click anywhere to see more details.", new Rectangle(contentSize.width / 4, contentSize.height - contentSize.height / 8, contentSize.width / 2, contentSize.height / 16));
			}
			else {
				int lineWidth = contentSize.width / 256;
				g.setStroke(new BasicStroke(lineWidth));
				g.setColor(Color.red);
				g.drawRect(lineWidth / 2, lineWidth / 2, contentSize.width - lineWidth, contentSize.height / 8 - lineWidth);
				StringDraw.drawMaxString(g, this.tableBordersSize, "Choose a weapon here.", new Rectangle(contentSize.width / 2, 0, contentSize.width / 2, contentSize.height / 8));
				g.setColor(Color.green);
				g.drawRect(lineWidth / 2, contentSize.height / 8 + lineWidth / 2, contentSize.width - lineWidth, contentSize.height / 8 - lineWidth);
				StringDraw.drawMaxString(g, this.tableBordersSize, "Weapon name", new Rectangle(0, contentSize.height / 8, contentSize.width / 2, contentSize.height / 8));
				g.setColor(Color.blue);
				int propertiesHeight;
				if (this.displayedWeapons[this.selectedWeapon].bought) {
					propertiesHeight = contentSize.height / 2;
				}
				else {
					propertiesHeight = contentSize.height * 3 / 8;
				}
				g.drawRect(lineWidth / 2, contentSize.height / 4 + lineWidth / 2, contentSize.width - lineWidth, propertiesHeight - lineWidth);
				StringDraw.drawMaxString(g, this.tableBordersSize, "Weapon properties", new Rectangle(0, contentSize.height * 3 / 8 + contentSize.height / 16, contentSize.width / 2, contentSize.height / 8));
			}
		}
	}

	private void drawWeapon(int index) throws IOException {
		int x = (int) ((this.weaponsSize) * (index - this.weaponsListPosition)) + this.contentSize.height / 16;
		if (this.displayedWeapons[index] != null) {
			this.g.drawImage(ResourceHandler.getTexture(this.displayedWeapons[index].textureName, (int) this.weaponsSize), x, 0, null);
			if (!this.displayedWeapons[index].bought) {
				this.g.setColor(PaintUtils.TRANSPARENT_GRAY);
				this.g.fillRect(x, 0, this.weaponsSize, this.weaponsSize);
			}
		}
		if (index == this.selectedWeapon) {
			float cornerSize = this.weaponsSize / (float) 16;
			Area wholeWeapon = new Area(new Rectangle(x, 0, this.weaponsSize, this.weaponsSize));
			wholeWeapon.subtract(new Area(new Rectangle((int) (x + cornerSize), (int) cornerSize, (int) (this.weaponsSize - cornerSize * 2), (int) (this.weaponsSize - cornerSize * 2))));
			this.g.setColor(CITRINE);
			g.fill(wholeWeapon);
			if (this.notBoughtWarning) {
				this.g.setColor(new Color(255, 0, 0, (int) (this.notBoughtStage / (float) 30 * 255)));
				this.g.fillRect(x, 0, this.weaponsSize, this.weaponsSize);
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

	private static String getLastColumnTextNotBought(UpgradablePropertyImplementation barrelProperties) {
		return "best " + toString(barrelProperties.getMaxValue()) + "(" + String.valueOf(barrelProperties.getMaxCost()) + "c)";
	}

	private static String getLastColumnTextBought(UpgradablePropertyImplementation weaponProperties) {
		if (weaponProperties.isFullyUpgraded()) {
			return "maxed";
		}
		else {
			float upgradeValue = weaponProperties.getUpgradeValue();
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
			return valuePart + "(" + String.valueOf(weaponProperties.getUpgradeCost()) + "c)";
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (!this.showingHelp && this.displayedWeapons[selectedWeapon] != null) {
			int bottomYBound;
			if (this.displayedWeapons[this.selectedWeapon].bought) {
				bottomYBound = this.contentSize.height * 7 / 8;
			}
			else {
				bottomYBound = this.contentSize.height * 5 / 8;
			}
			if (event.getY() < bottomYBound && event.getY() >= this.contentSize.height / 4) {
				this.listsPosition[this.selectedWeapon] += event.getWheelRotation() / (float) 4;
				if (this.listsPosition[this.selectedWeapon] < 0) {
					this.listsPosition[this.selectedWeapon] = 0;
				}
				else if (this.listsPosition[this.selectedWeapon] > Main.getSelectedBarrel().gameProperties.length - 2) {
					this.listsPosition[this.selectedWeapon] = Main.getSelectedBarrel().gameProperties.length - 2;
				}
			}
		}
	}

	@Override
	public void update(int time) {
		if (!this.showingHelp) {
			for (int i = 0; i < this.displayedWeapons.length; i++) {
				if (this.displayedWeapons[i] != null) {
					this.displayedWeapons[i].update(time);
				}
			}
			if (this.notBoughtWarning) {
				this.notBoughtStage -= time / (float) 40;
				if (this.notBoughtStage <= 0) {
					this.notBoughtWarning = false;
				}
			}
			if (this.onLeftButton) {
				this.weaponsListPosition += time / (float) 16 / (float) 40;
				if (this.weaponsListPosition > this.displayedWeapons.length - 1) {
					this.weaponsListPosition = this.displayedWeapons.length - 1;
				}
			}
			else if (this.onRightButton) {
				this.weaponsListPosition -= time / (float) 16 / (float) 40;
				if (this.weaponsListPosition < 0) {
					this.weaponsListPosition = 0;
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (this.showingHelp) {
			if (event.getKeyCode() == KeyEvent.VK_H || event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.showingHelp = false;
				this.onLeftButton = false;
				this.onRightButton = false;
				this.secondHelpPage = false;
			}
		}
		else {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				boolean doExit;
				if (this.displayedWeapons[this.selectedWeapon] == null) {
					doExit = true;
				}
				else if (this.displayedWeapons[this.selectedWeapon].bought) {
					doExit = true;
				}
				else {
					doExit = false;
				}
				if (doExit) {
					for (int i = 0; i < this.displayedWeapons.length; i++) {
						if (this.displayedWeapons[i] != null) {
							this.displayedWeapons[i].forceUpgrade();
						}
					}
					if (Main.shopRootNeeded()) {
						Screen.startNew(new ShopRootScreen());
					}
					else {
						Screen.startNew(new StartScreen());
					}
				}
				else {
					boolean isSomeBought = false;
					for (int i = 0; i < this.displayedWeapons.length; i++) {
						if (this.displayedWeapons[i].bought) {
							isSomeBought = true;
							break;
						}
					}
					if (isSomeBought) {
						this.notBoughtWarning = true;
						this.notBoughtStage = 30;
					}
					else {
						this.realSelectedWeapon.value = -1;
						Screen.startNew(new ShopRootScreen());
					}
				}
			}
			else if (event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = true;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!this.showingHelp) {
			if (event.getX() >= this.contentSize.height / 16 && event.getX() < this.contentSize.width - this.contentSize.height / 16 && event.getY() < this.weaponsSize) {
				float relListClickPosition = this.weaponsListPosition + (event.getX() - this.contentSize.height / 16) / (float) this.weaponsSize;
				int clickedWeaponIndex = (int) Math.floor(relListClickPosition);
				if (clickedWeaponIndex < this.displayedWeapons.length) {
					this.selectedWeapon = clickedWeaponIndex;
					this.notBoughtWarning = false;
				}
			}
			// Handles all the clicks on the properties interface
			else if (event.getY() >= this.contentSize.height / 4 && event.getY() < this.contentSize.height / 8 * 7) {
				if (this.displayedWeapons[this.selectedWeapon] != null) {
					Weapon selectedWeapon = this.displayedWeapons[this.selectedWeapon];
					boolean clickedOnUpgrade = false;
					if (!selectedWeapon.bought) {
						if (event.getY() >= this.contentSize.height * 5 / 8) {
							if (this.buyButton.contains(event.getPoint())) {
								if (selectedWeapon.cost <= Main.money) {
									Main.money -= selectedWeapon.cost;
									selectedWeapon.bought = true;
									Statistics.weaponBought(selectedWeapon);
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
						int clickedIndex = (int) ((event.getY() - this.contentSize.height / 4) / (float) (this.contentSize.height / 8) + this.listsPosition[this.selectedWeapon]);
						if (selectedWeapon.gameProperties[clickedIndex] instanceof UpgradablePropertyImplementation) {
							UpgradablePropertyImplementation upgradableImplementation = (UpgradablePropertyImplementation) selectedWeapon.gameProperties[clickedIndex];
							if (!upgradableImplementation.isFullyUpgraded()) {
								int upgradeCost = upgradableImplementation.getUpgradeCost();
								if (Main.money >= upgradeCost) {
									Main.money -= upgradeCost;
									upgradableImplementation.upgrade();
									if (!Achievement.achievements[6].achieved) {
										Achievement.achieve(6);
									}
									if (selectedWeapon.isFullyUpgraded()) {
										Achievement.achieve(7);
									}
								}
							}
						}
					}
				}
			}
			else if (this.buyButton.contains(event.getPoint()) && !Main.getSelectedBarrel().bought && this.displayedWeapons[this.selectedWeapon] != null) {
				Weapon selectedWeapon = this.displayedWeapons[this.selectedWeapon];
				if (selectedWeapon.cost <= Main.money) {
					Main.money -= selectedWeapon.cost;
					selectedWeapon.bought = true;
				}
			}
			else if (this.leftArrowButton.contains(event.getPoint())) {
				this.onLeftButton = true;
			}
			else if (this.rightArrowButton.contains(event.getPoint())) {
				this.onRightButton = true;
			}
		}
		else {
			this.secondHelpPage = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (!this.showingHelp) {
			this.onLeftButton = false;
			this.onRightButton = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (!this.showingHelp) {
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

	@Override
	public void getCloseReady() {
		if (this.displayedWeapons[this.selectedWeapon] == null) {
			this.realSelectedWeapon.value = -1;
		}
		else if (!this.displayedWeapons[this.selectedWeapon].bought) {
			if (this.realWeapons[0].bought) {
				this.realSelectedWeapon.value = 0;
			}
			else {
				this.realSelectedWeapon.value = -1;
			}
		}
		else {
			for (int i = 0; i < this.realWeapons.length; i++) {
				if (this.realWeapons[i] == this.displayedWeapons[this.selectedWeapon]) {
					this.realSelectedWeapon.value = i;
					break;
				}
			}
		}
	}
}
