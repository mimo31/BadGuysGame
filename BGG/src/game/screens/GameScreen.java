package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import game.BadGuy;
import game.Coin;
import game.Main;
import game.PaintUtils;
import game.Projectile;
import game.Screen;
import game.barrels.Barrel;
import game.io.ResourceHandler;

public class GameScreen extends Screen {

	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 31);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 31);

	// Components
	private Point usedMousePosition;
	private Graphics2D g;
	private Dimension contentSize;

	private void updateComponents(Graphics2D g, Dimension contentSize, Point mousePosition) {
		if (Main.showingStage) {
			this.usedMousePosition = Main.showingStageMousePos;
		}
		else {
			this.usedMousePosition = mousePosition;
		}
		this.contentSize = contentSize;
		this.g = g;
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(g, contentSize, mousePosition);
		for (int i = 0; i < Main.badGuys.size(); i++) {
			this.drawBadGuy(Main.badGuys.get(i));
		}
		for (int i = 0; i < Main.coins.size(); i++) {
			Coin currentCoin = Main.coins.get(i);
			g.drawImage(ResourceHandler.getTexture(currentCoin.textureName, contentSize.width / 64), (int) (contentSize.width * (currentCoin.x - 1 / (float) 128)), (int) (contentSize.height * currentCoin.y - contentSize.width / 128), null);
		}
		int baseScreenX = contentSize.width / 2 - contentSize.width / 16;
		int baseScreenY = contentSize.height - contentSize.width / 16;
		g.drawImage(ResourceHandler.getTexture("Base.png", contentSize.width / 8), baseScreenX, baseScreenY, null);
		int greenWidth = (int) (contentSize.width / 8 * Main.loadState);
		g.setColor(TRANSPARENT_GREEN);
		g.fillRect(baseScreenX, baseScreenY, greenWidth, contentSize.width / 16);
		g.setColor(TRANSPARENT_RED);
		g.fillRect(baseScreenX + greenWidth, baseScreenY, contentSize.width / 8 - greenWidth, contentSize.width / 16);
		Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
		AffineTransform transform = AffineTransform.getTranslateInstance(barrelCenter.x, barrelCenter.y);
		double vecX = -barrelCenter.x + this.usedMousePosition.x;
		double vecY = -this.usedMousePosition.y + barrelCenter.y;
		transform.rotate(vecY, vecX);
		transform.translate(-contentSize.width / 32, -contentSize.width / 32);
		g.drawImage(ResourceHandler.getTexture(Main.getSelectedBarrel().textureName, contentSize.width / 16), transform, null);
		for (int i = 0; i < Main.projectiles.size(); i++) {
			Projectile currentProjectile = Main.projectiles.get(i);
			AffineTransform projectileTransform = AffineTransform.getTranslateInstance(currentProjectile.x * contentSize.width, currentProjectile.y * contentSize.height);
			projectileTransform.rotate(-currentProjectile.dirY * contentSize.height, currentProjectile.dirX * contentSize.width);
			projectileTransform.translate(-contentSize.width / (float) 128, -contentSize.width / (float) 128);
			g.drawImage(ResourceHandler.getTexture(currentProjectile.textureName, contentSize.width / 64), projectileTransform, null);
		}
		PaintUtils.drawCurrentMoney(g, contentSize);
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

	private void drawBadGuy(BadGuy badGuy) throws IOException {
		int screenX = (int) ((badGuy.x / (float) 4 + 1 / (float) 8 - 1 / (float) 32) * contentSize.width);
		int screenY = (int) (badGuy.y * contentSize.height - contentSize.width / 16);
		this.g.drawImage(ResourceHandler.getTexture(badGuy.textureName, contentSize.width / 16), screenX, screenY, null);
		this.g.setColor(TRANSPARENT_GREEN);
		int filledSize = (int) (badGuy.getShownLive() * contentSize.width / 16);
		this.g.fillRect(screenX, screenY + contentSize.width / 16 - filledSize, contentSize.width / 16, filledSize);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!Main.showingStage) {
			if (Main.loadState == 1) {
				Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
				float vecX = event.getX() - barrelCenter.x;
				float vecY = event.getY() - barrelCenter.y;
				float factor = (float) (contentSize.width / (float) 32 / Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2)));
				Point firePoint = new Point((int) (barrelCenter.x + vecX * factor), (int) (barrelCenter.y + vecY * factor));
				Barrel selectedBarrel = Main.getSelectedBarrel();
				float speed = selectedBarrel.getProperty(Barrel.projectileSpeedID);
				String textureName = selectedBarrel.projectileTextureName;
				float hitPower = selectedBarrel.getProperty(Barrel.projectilePowerID);
				float dirX = vecX / (float) contentSize.width;
				float dirY = vecY / (float) contentSize.height;
				float coinMagnet = selectedBarrel.getProperty(Barrel.coinMagnetID);
				Projectile projectile = new Projectile(firePoint.x / (float) contentSize.width, firePoint.y / (float) contentSize.height, dirX, dirY, speed, textureName, hitPower, coinMagnet);
				Main.projectiles.add(projectile);
				Main.loadState = 0;
			}
		}
	}

	@Override
	public void update() {
		if (!Main.showingStage) {
			for (int i = 0; i < Main.stages[Main.currentStage].spawners.length; i++) {
				if (Main.stages[Main.currentStage].spawnTimes[i] == Main.timeInStage) {
					Main.addBadGuyToBuffer(Main.stages[Main.currentStage].spawners[i].getBadGuy());
				}
			}
			Main.spawnBadGuysFromBuffer(this.contentSize);
			for (int i = 0; i < Main.badGuys.size(); i++) {
				BadGuy currentBadGuy = Main.badGuys.get(i);
				if (currentBadGuy.isBeingHit) {
					currentBadGuy.hittingProgress += 1 / (float) 32;
					if (currentBadGuy.hittingProgress >= 1) {
						currentBadGuy.isBeingHit = false;
						currentBadGuy.live -= currentBadGuy.hitBy;
						if (currentBadGuy.isDead) {
							Main.badGuys.remove(i);
							i--;
							if (Main.isStageCompleted()) {
								Main.startNewStage();
							}
						}
					}
				}
				if (!currentBadGuy.isDead) {
					currentBadGuy.move();
					if (currentBadGuy.y > 1) {
						// Game Over
						Main.showingStage = true;
						Main.gameOver = true;
						Main.showingStageState = 0;
						Main.showingStageMousePos = usedMousePosition;
						Main.updateMaxStage();
					}
				}
			}
			Main.updateCoins(contentSize);
			float heightFraction = contentSize.width / (float) contentSize.height / (float) 128;
			for (int i = 0; i < Main.projectiles.size(); i++) {
				Projectile currentProjectile = Main.projectiles.get(i);
				currentProjectile.x += currentProjectile.dirX / 96;
				currentProjectile.y += currentProjectile.dirY / 96;
				if (currentProjectile.x + 1 / (float) 128 < 0 || currentProjectile.x - 1 / (float) 128 >= 1 || currentProjectile.y + heightFraction < 0 || currentProjectile.y - heightFraction >= 1) {
					Main.projectiles.remove(i);
					i--;
					continue;
				}
				for (int j = 0; j < Main.coins.size(); j++) {
					Coin currentCoin = Main.coins.get(j);
					if (circleCircleCollistion(currentCoin.x, currentCoin.y * this.contentSize.height / this.contentSize.width, currentProjectile.x, currentProjectile.y * this.contentSize.height / this.contentSize.width, 1 / (float) 128, 1 / (float) 128)) {
						Main.money += currentCoin.value;
						Main.coins.remove(j);
						j--;
					}
				}
				for (int j = 0; j < Main.badGuys.size(); j++) {
					BadGuy currentBadGuy = Main.badGuys.get(j);
					if (doesCollide(currentBadGuy.x / (float) 4 + 1 / (float) 8, currentBadGuy.y * contentSize.height / (float) contentSize.width - 1 / (float) 32, 1 / (float) 32, currentProjectile.x, currentProjectile.y * contentSize.height / (float) contentSize.width, 1 / (float) 128)) {
						if (!currentBadGuy.isDead) {
							currentBadGuy.hit(currentProjectile.hitPower);
							if (currentBadGuy.isDead) {
								Coin addedCoin = currentBadGuy.getCoin();
								addedCoin.x = 1 / (float) 8 + currentBadGuy.x / (float) 4;
								if (this.contentSize.width / 32 + this.contentSize.width / 128 >= currentBadGuy.y * this.contentSize.height) {
									addedCoin.y = this.contentSize.width / (float) 128 / this.contentSize.height;
								}
								else {
									addedCoin.y = currentBadGuy.y - this.contentSize.width / (float) 32 / this.contentSize.height;
								}
								Main.coins.add(addedCoin);
							}
						}
						Main.projectiles.remove(i);
						i--;
						break;
					}
				}
			}
			if (Main.loadState != 1) {
				Main.loadState += 1 / (float) (32 * Main.getSelectedBarrel().getProperty(Barrel.loadingTimeID));
				if (Main.loadState > 1) {
					Main.loadState = 1;
				}
			}
			Main.timeInStage++;
		}
		else {
			Main.showingStageState++;
			if (Main.showingStageState == Main.stageShowTime / 2) {
				if (Main.gameOver || Main.noMoreStages) {
					Main.resetTheGame();
					Screen.startNew(new StartScreen());
				}
				else {
					Main.loadState = 1;
				}
			}
			else if (Main.showingStageState == Main.stageShowTime) {
				Main.showingStage = false;
			}
		}
	}
	
	private static boolean circleCircleCollistion(float center1X, float center1Y, float center2X, float center2Y, float radius1, float radius2) {
		float centerDistanceSqr = (float) (Math.pow(center1X - center2X, 2) + Math.pow(center1Y - center2Y, 2));
		return centerDistanceSqr < Math.pow(radius1 + radius2, 2);
	}

	private static boolean doesCollide(float squareCenterX, float squareCenterY, float squareSize, float circleCenterX, float circleCenterY, float circleRadius) {
		float diffX = Math.abs(squareCenterX - circleCenterX);
		float diffY = Math.abs(squareCenterY - circleCenterY);

		if (diffX > squareSize + circleRadius) {
			return false;
		}
		if (diffY > squareSize + circleRadius) {
			return false;
		}

		if (diffX <= squareSize + circleRadius && diffY <= squareSize) {
			return true;
		}
		if (diffY <= squareSize + circleRadius && diffX <= squareSize) {
			return true;
		}

		float distToCornerSqr = (float) (Math.pow(diffX - squareSize, 2) + Math.pow(diffY - squareSize, 2));
		return distToCornerSqr <= Math.pow(circleRadius, 2);
	}
	
	@Override
	public void getCloseReady() {
		Main.updateMaxStage();
	}
}
