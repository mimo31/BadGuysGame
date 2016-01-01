package game.mechanics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;

import game.Main;
import game.Statistics;
import game.io.ResourceHandler;
import game.mechanics.GameReturnData.GameActionType;
import game.mechanics.barrels.Barrel;

public class Game {

	private int currentStage;
	private float timeInStage;
	private ArrayList<BadGuy> badGuys;
	private ArrayList<Projectile> projectiles;
	private ArrayList<BadGuy> badGuysBuffer;
	private ArrayList<Coin> coins;
	private float loadState = 1;
	private float loadingTime;
	private float projectilePower;
	private float projectileSpeed;
	private float coinMagnet;
	private String projectileTextureName;

	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 31);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 31);
	public static final Color LIFE_GREEN = new Color(0, 255, 127, 63);

	public Game(int stage, Barrel barrel) {
		this.currentStage = stage;
		this.timeInStage = 0;
		this.badGuys = new ArrayList<BadGuy>();
		this.projectiles = new ArrayList<Projectile>();
		this.badGuysBuffer = new ArrayList<BadGuy>();
		this.coins = new ArrayList<Coin>();
		this.updateBarrel(barrel);
	}

	public void updateBarrel(Barrel barrel) {
		this.loadingTime = barrel.getProperty(Barrel.loadingTimeID);
		this.projectilePower = barrel.getProperty(Barrel.projectilePowerID);
		this.projectileSpeed = barrel.getProperty(Barrel.projectileSpeedID);
		this.coinMagnet = barrel.getProperty(Barrel.coinMagnetID);
		this.projectileTextureName = barrel.projectileTextureName;
	}

	private void addBadGuysToBuffer(int time) {
		for (int i = 0; i < Main.stages[this.currentStage].spawners.length; i++) {
			int spawnTime = Main.stages[this.currentStage].spawnTimes[i];
			if (this.timeInStage - (time / (float) 40) <= spawnTime && spawnTime < this.timeInStage) {
				this.badGuysBuffer.add(Main.stages[this.currentStage].spawners[i].getBadGuy());
			}
		}
	}

	private void spawnBadGuysFromBuffer(Dimension contentSize) {
		boolean[] isColumnOccupied = new boolean[4];
		for (int i = 0; i < this.badGuys.size(); i++) {
			BadGuy currentBadGuy = this.badGuys.get(i);
			float heightSize;
			if (currentBadGuy.isBig) {
				heightSize = contentSize.width / (float) 8 / contentSize.height;
			}
			else {
				heightSize = contentSize.width / (float) 16 / contentSize.height;
			}
			if (!currentBadGuy.isDead && currentBadGuy.y < heightSize) {
				isColumnOccupied[currentBadGuy.x] = true;
			}
		}
		while (!isFull(isColumnOccupied) && !(badGuysBuffer.size() == 0)) {
			badGuysBuffer.get(0).x = takeFree(isColumnOccupied);
			badGuys.add(badGuysBuffer.get(0));
			badGuysBuffer.remove(0);
		}
	}

	private static boolean isFull(boolean[] array) {
		for (int i = 0; i < array.length; i++) {
			if (!array[i]) {
				return false;
			}
		}
		return true;
	}

	private static int takeFree(boolean[] array) {
		int totalFree = 0;
		for (int i = 0; i < 4; i++) {
			if (!array[i]) {
				totalFree++;
			}
		}
		int[] freeIndexes = new int[totalFree];
		int nextIndex = 0;
		for (int i = 0; i < 4; i++) {
			if (!array[i]) {
				freeIndexes[nextIndex] = i;
				nextIndex++;
			}
		}
		int indexTaken = freeIndexes[(int) Math.floor(Math.random() * freeIndexes.length)];
		array[indexTaken] = true;
		return indexTaken;
	}

	private boolean isStageCompleted() {
		return this.badGuys.isEmpty() && this.badGuysBuffer.isEmpty() && Main.stages[currentStage].allSpawned(this.timeInStage);
	}

	private void updateCoins(Dimension contentSize, int time) {
		for (int i = 0; i < this.coins.size(); i++) {
			boolean collected = false;
			for (Projectile projectile : this.projectiles) {
				if (projectile.coinMagnet != 0) {
					if (coins.get(i).attractTo(projectile.x, projectile.y, projectile.coinMagnet, time)) {
						collected = true;
						break;
					}
				}
			}
			if (collected) {
				Main.money += coins.get(i).value;
				coins.remove(i);
				i--;
			}
			else {
				coins.get(i).resolveEdgeCollisions(contentSize);
			}
		}
	}

	public GameReturnData update(int time, Dimension contentSize) {
		boolean nextStage = false;
		boolean gameOver = false;
		boolean noMoreStages = false;
		this.addBadGuysToBuffer(time);
		this.spawnBadGuysFromBuffer(contentSize);
		for (int i = 0; i < this.badGuys.size(); i++) {
			BadGuy currentBadGuy = this.badGuys.get(i);
			if (currentBadGuy.isBeingHit) {
				currentBadGuy.hittingProgress += time / (float) 32 / (float) 40;
				if (currentBadGuy.hittingProgress >= 1) {
					currentBadGuy.isBeingHit = false;
					currentBadGuy.live -= currentBadGuy.hitBy;
					if (currentBadGuy.isDead) {
						this.badGuys.remove(i);
						Statistics.badGuyKilled(currentBadGuy.name);
						i--;
						if (this.isStageCompleted()) {
							if (Main.stages.length == this.currentStage + 1) {
								noMoreStages = true;
							}
							else {
								this.timeInStage = 0;
								this.currentStage++;
								nextStage = true;
							}
						}
					}
				}
			}
			if (!currentBadGuy.isDead) {
				currentBadGuy.move(time);
				if (currentBadGuy.y > 1) {
					gameOver = true;
				}
			}
		}
		this.updateCoins(contentSize, time);
		float heightFraction = contentSize.width / (float) contentSize.height / (float) 128;
		for (int i = 0; i < this.projectiles.size(); i++) {
			Projectile currentProjectile = this.projectiles.get(i);
			currentProjectile.x += currentProjectile.dirX * time / (float) 96 / (float) 40;
			currentProjectile.y += currentProjectile.dirY * time / (float) 96 / (float) 40;
			if (currentProjectile.x + 1 / (float) 128 < 0 || currentProjectile.x - 1 / (float) 128 >= 1 || currentProjectile.y + heightFraction < 0 || currentProjectile.y - heightFraction >= 1) {
				this.projectiles.remove(i);
				i--;
				continue;
			}
			for (int j = 0; j < this.coins.size(); j++) {
				Coin currentCoin = this.coins.get(j);
				if (circleCircleCollistion(currentCoin.x, currentCoin.y * contentSize.height / contentSize.width, currentProjectile.x, currentProjectile.y * contentSize.height / contentSize.width, 1 / (float) 128, 1 / (float) 128)) {
					Main.money += currentCoin.value;
					Statistics.moneyCollected(currentCoin.value);
					this.coins.remove(j);
					j--;
				}
			}
			for (int j = 0; j < this.badGuys.size(); j++) {
				BadGuy currentBadGuy = this.badGuys.get(j);
				float badGuySize;
				if (currentBadGuy.isBig) {
					badGuySize = 1 / (float) 16;
				}
				else {
					badGuySize = 1 / (float) 32;
				}
				if (doesCollide(currentBadGuy.x / (float) 4 + 1 / (float) 8, currentBadGuy.y * contentSize.height / (float) contentSize.width - badGuySize, badGuySize, currentProjectile.x, currentProjectile.y * contentSize.height / (float) contentSize.width, 1 / (float) 128)) {
					if (!currentBadGuy.isDead) {
						currentBadGuy.hit(currentProjectile.hitPower);
						if (currentBadGuy.isDead) {
							Coin addedCoin = currentBadGuy.getCoin();
							addedCoin.x = 1 / (float) 8 + currentBadGuy.x / (float) 4;
							if (badGuySize + contentSize.width / 128 >= currentBadGuy.y * contentSize.height) {
								addedCoin.y = contentSize.width / (float) 128 / contentSize.height;
							}
							else {
								addedCoin.y = currentBadGuy.y - badGuySize * contentSize.width / contentSize.height;
							}
							this.coins.add(addedCoin);
						}
					}
					this.projectiles.remove(i);
					i--;
					break;
				}
			}
		}
		if (this.loadState != 1) {
			this.loadState += time / (float) (32 * this.loadingTime * 40);
			if (this.loadState > 1) {
				this.loadState = 1;
			}
		}
		this.timeInStage += time / (float) 40;
		if (nextStage) {
			return new GameReturnData(GameActionType.NEXT_STAGE, this.currentStage);
		}
		else if (gameOver) {
			return new GameReturnData(GameActionType.GAME_OVER, this.currentStage);
		}
		else if (noMoreStages) {
			return new GameReturnData(GameActionType.NO_MORE_STAGES, this.currentStage);
		}
		else {
			return null;
		}
	}

	public void onMiddleChangingStage() {
		this.loadState = 1;
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

	private void drawBadGuy(Graphics2D g, BadGuy badGuy, Dimension contentSize) throws IOException {
		float badGuyPixelSize;
		if (badGuy.isBig) {
			badGuyPixelSize = contentSize.width / 8;
		}
		else {
			badGuyPixelSize = contentSize.width / 16;
		}
		int screenX = (int) ((badGuy.x / (float) 4 + 1 / (float) 8) * contentSize.width - badGuyPixelSize / 2);
		int screenY = (int) (badGuy.y * contentSize.height - badGuyPixelSize);
		g.drawImage(ResourceHandler.getTexture(badGuy.textureName, (int)badGuyPixelSize), screenX, screenY, null);
		g.setColor(LIFE_GREEN);
		int filledSize = (int) (badGuy.getShownLive() * badGuyPixelSize);
		g.fillRect((int) (screenX + badGuyPixelSize * 7 / 8), (int) (screenY + badGuyPixelSize - filledSize), (int) (badGuyPixelSize / 8), filledSize);
	}

	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		for (int i = 0; i < this.badGuys.size(); i++) {
			this.drawBadGuy(g, this.badGuys.get(i), contentSize);
		}
		for (int i = 0; i < this.coins.size(); i++) {
			Coin currentCoin = this.coins.get(i);
			g.drawImage(ResourceHandler.getTexture(currentCoin.textureName, contentSize.width / 64), (int) (contentSize.width * (currentCoin.x - 1 / (float) 128)), (int) (contentSize.height * currentCoin.y - contentSize.width / 128), null);
		}
		int baseScreenX = contentSize.width / 2 - contentSize.width / 16;
		int baseScreenY = contentSize.height - contentSize.width / 16;
		g.drawImage(ResourceHandler.getTexture("Base.png", contentSize.width / 8), baseScreenX, baseScreenY, null);
		int greenWidth = (int) (contentSize.width / 8 * this.loadState);
		g.setColor(TRANSPARENT_GREEN);
		g.fillRect(baseScreenX, baseScreenY, greenWidth, contentSize.width / 16);
		g.setColor(TRANSPARENT_RED);
		g.fillRect(baseScreenX + greenWidth, baseScreenY, contentSize.width / 8 - greenWidth, contentSize.width / 16);
		Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
		AffineTransform transform = AffineTransform.getTranslateInstance(barrelCenter.x, barrelCenter.y);
		double vecX = -barrelCenter.x + mousePosition.x;
		double vecY = -mousePosition.y + barrelCenter.y;
		transform.rotate(vecY, vecX);
		transform.translate(-contentSize.width / 32, -contentSize.width / 32);
		g.drawImage(ResourceHandler.getTexture(Main.getSelectedBarrel().textureName, contentSize.width / 16), transform, null);
		for (int i = 0; i < this.projectiles.size(); i++) {
			Projectile currentProjectile = this.projectiles.get(i);
			AffineTransform projectileTransform = AffineTransform.getTranslateInstance(currentProjectile.x * contentSize.width, currentProjectile.y * contentSize.height);
			projectileTransform.rotate(-currentProjectile.dirY * contentSize.height, currentProjectile.dirX * contentSize.width);
			projectileTransform.translate(-contentSize.width / (float) 128, -contentSize.width / (float) 128);
			g.drawImage(ResourceHandler.getTexture(currentProjectile.textureName, contentSize.width / 64), projectileTransform, null);
		}
	}

	public void clicked(int x, int y, Dimension contentSize) {
		if (this.loadState == 1) {
			Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
			float vecX = x - barrelCenter.x;
			float vecY = y - barrelCenter.y;
			float factor = (float) (contentSize.width / (float) 32 / Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2)));
			Point firePoint = new Point((int) (barrelCenter.x + vecX * factor), (int) (barrelCenter.y + vecY * factor));
			float dirX = vecX / (float) contentSize.width;
			float dirY = vecY / (float) contentSize.height;
			Projectile projectile = new Projectile(firePoint.x / (float) contentSize.width, firePoint.y / (float) contentSize.height, dirX, dirY, this.projectileSpeed, this.projectileTextureName, this.projectilePower, this.coinMagnet);
			this.projectiles.add(projectile);
			this.loadState = 0;
		}
	}
	
	public GameExitData getExitData() {
		return new GameExitData(this.currentStage);
	}
	
	public static class GameExitData {
		
		public int stage;
		
		public GameExitData(int stage) {
			this.stage = stage;
		}
	}
}
