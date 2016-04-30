package com.github.mimo31.badguysgame.mechanics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.Statistics;
import com.github.mimo31.badguysgame.io.ResourceHandler;
import com.github.mimo31.badguysgame.mechanics.GameReturnData.GameActionType;
import com.github.mimo31.badguysgame.mechanics.weaponry.Crusher;
import com.github.mimo31.badguysgame.mechanics.weaponry.Weapon;

public class Game {

	private int currentStage;
	private float timeInStage;
	private ArrayList<FallingObject> fallingObjects;
	private ArrayList<Projectile> projectiles;
	private ArrayList<FallingObject> fallingObjectsBuffer;
	private ArrayList<Coin> coins;

	private float loadState = 1;
	private float loadingTime;
	private float projectilePower;
	private float projectileSpeed;
	private float coinMagnet;
	private float unblockSpeed;
	private String textureName;
	private String projectileTextureName;
	private float mainBarrelBlockState;
	private float mainBarrelBlockOriginal;
	private String shootSoundName;

	private float autoLeftLoadState = 1;
	private float autoRightLoadState = 1;
	private float autoLoadingTime;
	private float autoProjectilePower;
	private float autoProjectileSpeed;
	private float autoCoinMagnet;
	private float autoRotationSpeed;
	private float autoUnblockSpeed;
	private String autoTextureName;
	private String autoProjectileTextureName;
	private boolean usingAutoweapon;
	private float autoLeftBlockState;
	private float autoLeftBlockOriginal;
	private float autoRightBlockState;
	private float autoRightBlockOriginal;
	/**
	 * Rotation in radians. Relative to the upward angle.
	 */
	private double leftAutoRotation = 0;
	/**
	 * Rotation in radians. Relative to the upward angle.
	 */
	private double rightAutoRotation = 0;
	
	private boolean usingCrusher;
	private float crushPower;
	private float crushFrequency;
	private float[] crushersStates;
	private Crusher crusher;

	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 31);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 31);
	public static final Color LIFE_GREEN = new Color(0, 255, 127, 63);

	public Game(int stage, Weapon barrel, Weapon autoweapon, Crusher crusher) {
		this.currentStage = stage;
		this.timeInStage = 0;
		this.fallingObjects = new ArrayList<FallingObject>();
		this.projectiles = new ArrayList<Projectile>();
		this.fallingObjectsBuffer = new ArrayList<FallingObject>();
		this.coins = new ArrayList<Coin>();
		this.updateBarrel(barrel);
		this.updateAutoweapon(autoweapon);
		this.updateCrusher(crusher);
	}

	public void updateBarrel(Weapon barrel) {
		this.loadingTime = barrel.getProperty(Weapon.loadingTimeID);
		this.projectilePower = barrel.getProperty(Weapon.projectilePowerID);
		this.projectileSpeed = barrel.getProperty(Weapon.projectileSpeedID);
		this.coinMagnet = barrel.getProperty(Weapon.coinMagnetID);
		this.unblockSpeed = barrel.getProperty(Weapon.getPropertyID("Unblock speed"));
		this.textureName = barrel.textureName;
		this.projectileTextureName = barrel.projectileTextureName;
		this.shootSoundName = barrel.shootSoundName;
	}

	public void updateAutoweapon(Weapon autoweapon) {
		if (autoweapon != null) {
			this.usingAutoweapon = true;
			this.autoLoadingTime = autoweapon.getProperty(Weapon.loadingTimeID);
			this.autoProjectilePower = autoweapon.getProperty(Weapon.projectilePowerID);
			this.autoProjectileSpeed = autoweapon.getProperty(Weapon.projectileSpeedID);
			this.autoCoinMagnet = autoweapon.getProperty(Weapon.coinMagnetID);
			this.autoRotationSpeed = autoweapon.getProperty(Weapon.rotationSpeedID);
			this.autoUnblockSpeed = autoweapon.getProperty(Weapon.getPropertyID("Unblock speed"));
			this.autoTextureName = autoweapon.textureName;
			this.autoProjectileTextureName = autoweapon.projectileTextureName;
		}
		else {
			this.usingAutoweapon = false;
		}
	}
	
	public void updateCrusher(Crusher crusher) {
		if (crusher != null) {
			this.usingCrusher = true;
			this.crushersStates = new float[4];
			this.crushPower = crusher.getProperty(Weapon.getPropertyID("Crush power"));
			this.crushFrequency = crusher.getProperty(Weapon.getPropertyID("Crush frequency"));
			this.crusher = crusher;
		}
		else {
			this.usingCrusher = false;
		}
	}

	private void addBadGuysToBuffer(int time) {
		for (int i = 0; i < Main.stages[this.currentStage].spawners.length; i++) {
			int spawnTime = Main.stages[this.currentStage].spawnTimes[i];
			if (this.timeInStage - (time / (float) 40) <= spawnTime && spawnTime < this.timeInStage) {
				this.fallingObjectsBuffer.add(Main.stages[this.currentStage].spawners[i].getFallingObject());
			}
		}
	}

	private void spawnObjectsFromBuffer(Dimension contentSize) {
		boolean[] isColumnOccupied = new boolean[4];
		for (int i = 0; i < this.fallingObjects.size(); i++) {
			FallingObject currentObject = this.fallingObjects.get(i);
			float heightSize = contentSize.width / 4 * currentObject.size / contentSize.height;
			if (!currentObject.isDead && currentObject.y < heightSize) {
				isColumnOccupied[currentObject.x] = true;
			}
		}
		while (!isFull(isColumnOccupied) && !(fallingObjectsBuffer.size() == 0)) {
			fallingObjectsBuffer.get(0).x = takeFree(isColumnOccupied);
			fallingObjects.add(fallingObjectsBuffer.get(0));
			fallingObjectsBuffer.remove(0);
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
		for (int i = 0; i < this.fallingObjects.size(); i++) {
			if (this.fallingObjects.get(i).isGameEnding) {
				return false;
			}
		}
		return this.fallingObjectsBuffer.isEmpty() && Main.stages[currentStage].allSpawned(this.timeInStage);
	}

	private void updateCoins(Dimension contentSize, int time) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
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
				ResourceHandler.playSound(coins.get(i).collectSoundName);
				Statistics.moneyCollected(coins.get(i).value);
				coins.remove(i);
				i--;
			}
			else {
				coins.get(i).resolveEdgeCollisions(contentSize);
			}
		}
	}

	public GameReturnData update(int time, Dimension contentSize) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		boolean nextStage = false;
		boolean gameOver = false;
		boolean noMoreStages = false;
		this.addBadGuysToBuffer(time);
		this.spawnObjectsFromBuffer(contentSize);
		for (int i = 0; i < this.fallingObjects.size(); i++) {
			FallingObject currentObject = this.fallingObjects.get(i);
			if (currentObject.isBeingHit) {
				currentObject.hittingProgress += time * currentObject.hitProgressStep;
				if (currentObject.hittingProgress >= 1) {
					currentObject.isBeingHit = false;
					currentObject.live -= currentObject.hitBy;
					if (currentObject.isDead) {
						this.fallingObjects.remove(i);
						Statistics.badGuyKilled(currentObject.name);
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
			if (!currentObject.isDead) {
				currentObject.move(time);
				if (currentObject.y > 1 && currentObject.isGameEnding) {
					gameOver = true;
				}
				if (currentObject instanceof ShootingBadGuy) {
					ShootingBadGuy shootingGuy = (ShootingBadGuy) currentObject;
					shootingGuy.loadState += time / (32 * shootingGuy.loadingTime * 40);
					if (shootingGuy.loadState >= 1) {
						shootingGuy.loadState--;
						float targetX;
						if (!this.usingAutoweapon) {
							targetX = 1 / (float) 2;
						}
						else {
							if (shootingGuy.x == 0) {
								targetX = 1 / (float) 4;
							}
							else if (shootingGuy.x == 3) {
								targetX = 3 / (float) 4;
							}
							else {
								targetX = 1 / (float) 2;
							}
						}
						float targetY = 1 - contentSize.width / (float) 32 / contentSize.height;
						float shootPositionX = shootingGuy.x * 1 / (float) 4 + 1 / (float) 8;
						float shootPositionY = shootingGuy.y;
						ResourceHandler.playSound(shootingGuy.shootSoundName);
						Projectile projectile = new Projectile(shootPositionX, shootPositionY, targetX - shootPositionX, targetY - shootPositionY, shootingGuy.projectileSpeed, shootingGuy.projectileTextureName, shootingGuy.projectilePower, 0, true);
						this.projectiles.add(projectile);
					}
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
			if (!currentProjectile.isFromBadGuy) {
				for (int j = 0; j < this.coins.size(); j++) {
					Coin currentCoin = this.coins.get(j);
					if (circleCircleCollistion(currentCoin.x, currentCoin.y * contentSize.height / contentSize.width, currentProjectile.x, currentProjectile.y * contentSize.height / contentSize.width, 1 / (float) 128, 1 / (float) 128)) {
						Main.money += currentCoin.value;
						Statistics.moneyCollected(currentCoin.value);
						ResourceHandler.playSound(currentCoin.collectSoundName);
						this.coins.remove(j);
						j--;
					}
				}
				for (int j = 0; j < this.fallingObjects.size(); j++) {
					FallingObject currentObject = this.fallingObjects.get(j);
					float objectSize = currentObject.size / 8;
					if (doesCollide(currentObject.x / (float) 4 + 1 / (float) 8, currentObject.y * contentSize.height / (float) contentSize.width - objectSize, objectSize, currentProjectile.x, currentProjectile.y * contentSize.height / (float) contentSize.width, 1 / (float) 128)) {
						currentObject.hit(currentProjectile.hitPower);
						if (currentObject.isDead) {
							Coin addedCoin = currentObject.getCoin();
							if (addedCoin != null) {
								addedCoin.x = 1 / (float) 8 + currentObject.x / (float) 4;
								if (objectSize * contentSize.width + contentSize.width / 128 >= currentObject.y * contentSize.height) {
									addedCoin.y = contentSize.width / (float) 128 / contentSize.height;
								}
								else {
									addedCoin.y = currentObject.y - objectSize * contentSize.width / contentSize.height;
								}
								this.coins.add(addedCoin);
							}
						}
						this.projectiles.remove(i);
						i--;
						break;
					}
				}
				for (int j = 0; j < this.projectiles.size(); j++) {
					Projectile currentBadProjectile = this.projectiles.get(j);
					if (currentBadProjectile.isFromBadGuy) {
						if (circleCircleCollistion(currentProjectile.x, currentProjectile.y * contentSize.height / contentSize.width, currentBadProjectile.x, currentBadProjectile.y * contentSize.height / contentSize.width, 1 / (float) 128, 1 / (float) 128)) {
							this.projectiles.remove(j);
							Statistics.projectileEliminated();
							if (j < i) {
								i--;
							}
							j--;
						}
					}
				}
			}
			else {
				float barrelsY = (contentSize.height - contentSize.width / (float) 32) / (float) contentSize.width;
				float projectileY = currentProjectile.y * contentSize.height / contentSize.width;
				if (doesCollide(1 / (float) 2, barrelsY, 1 / (float) 16, 1 / (float) 32, currentProjectile.x, projectileY, 1 / (float) 128)) {
					this.mainBarrelBlockState += currentProjectile.hitPower;
					this.mainBarrelBlockOriginal = this.mainBarrelBlockState;
					this.projectiles.remove(i);
					i--;
					continue;
				}
				if (this.usingAutoweapon) {
					if (doesCollide(1 / (float) 4, barrelsY, 1 / (float) 32, currentProjectile.x, projectileY, 1 / (float) 128)) {
						this.autoLeftBlockState += currentProjectile.hitPower;
						this.autoLeftBlockOriginal = this.autoLeftBlockState;
						this.projectiles.remove(i);
						i--;
						continue;
					}
					if (doesCollide(3 / (float) 4, barrelsY, 1 / (float) 32, currentProjectile.x, projectileY, 1 / (float) 128)) {
						this.autoRightBlockState += currentProjectile.hitPower;
						this.autoRightBlockOriginal = this.autoRightBlockState;
						this.projectiles.remove(i);
						i--;
						continue;
					}
				}
			}
		}
		float mainLoadStep = time / (float) (32 * this.loadingTime * 40);
		float mainUnblockStep = this.unblockSpeed * time / (float) (32 * 40);
		float autoLoadStep = time / (float) (32 * this.autoLoadingTime * 40);
		float autoUnblockStep = this.autoUnblockSpeed * time / (float) (32 * 40);
		if (this.mainBarrelBlockState == 0) {
			if (this.loadState != 1) {
				this.loadState += mainLoadStep;
				if (this.loadState > 1) {
					this.loadState = 1;
				}
			}
		}
		else {
			this.mainBarrelBlockState -= mainUnblockStep;
			if (this.mainBarrelBlockState <= 0) {
				this.mainBarrelBlockState = 0;
				this.mainBarrelBlockOriginal = 0;
			}
		}
		if (this.usingAutoweapon) {
			if (this.autoLeftBlockState == 0) {
				if (this.autoLeftLoadState != 1) {
					this.autoLeftLoadState += autoLoadStep;
					if (this.autoLeftLoadState > 1) {
						this.autoLeftLoadState = 1;
					}
				}
			}
			else {
				this.autoLeftBlockState -= autoUnblockStep;
				if (this.autoLeftBlockState <= 0) {
					this.autoLeftBlockState = 0;
					this.autoLeftBlockOriginal = 0;
				}
			}
			if (this.autoRightBlockState == 0) {
				if (this.autoRightLoadState != 1) {
					this.autoRightLoadState += autoLoadStep;
					if (this.autoRightLoadState > 1) {
						this.autoRightLoadState = 1;
					}
				}
			}
			else {
				this.autoRightBlockState -= autoUnblockStep;
				if (this.autoRightBlockState <= 0) {
					this.autoRightBlockState = 0;
					this.autoRightBlockOriginal = 0;
				}
			}

			// Use the left autoweapon
			float biggestYValue = 0;
			int biggestYIndex = -1;
			boolean isGameEnding = false;
			boolean shoot;
			for (int i = 0; i < this.fallingObjects.size(); i++) {
				FallingObject currentObject = this.fallingObjects.get(i);
				boolean hasPriority = (currentObject.y > biggestYValue && !(isGameEnding && !currentObject.isGameEnding)) || (currentObject.isGameEnding && !isGameEnding);
				if ((currentObject.x == 0 || currentObject.x == 1) && !currentObject.isDead && hasPriority) {
					biggestYValue = currentObject.y;
					biggestYIndex = i;
					isGameEnding = currentObject.isGameEnding;
				}
			}
			if (biggestYIndex == -1) {
				shoot = false;
			}
			else {
				int targetedGuyX = this.fallingObjects.get(biggestYIndex).x * contentSize.width / 4 + contentSize.width / 8;
				int targetedGuyY = (int) (this.fallingObjects.get(biggestYIndex).y * contentSize.height);
				int diffX = targetedGuyX - contentSize.width / 4;
				int diffY = contentSize.height - contentSize.width / 16 - targetedGuyY;
				double appropriateAngle = Math.atan2(diffX, diffY);
				double stepSize = Math.PI * this.autoRotationSpeed * time / 20 / 1024;
				shoot = this.rotateAutoweapon(true, stepSize, appropriateAngle);
			}
			if (shoot && this.autoLeftLoadState == 1 && this.autoLeftBlockState == 0) {
				this.autoLeftLoadState = 0;
				float xDistance = (float) Math.sin(this.leftAutoRotation);
				float yDistance = (float) Math.cos(this.leftAutoRotation);
				float projectileX = 1 / (float) 4 + xDistance / 32;
				float projectileY = 1 - contentSize.width / 16 / (float) contentSize.height - yDistance * contentSize.width / 32 / (float) contentSize.height;
				Projectile projectile = new Projectile(projectileX, projectileY, xDistance / contentSize.width, -yDistance / contentSize.height, this.autoProjectileSpeed, this.autoProjectileTextureName, this.autoProjectilePower, this.autoCoinMagnet, false);
				projectiles.add(projectile);
			}

			// Use the right autoweapon
			biggestYValue = 0;
			biggestYIndex = -1;
			isGameEnding = false;
			shoot = false;
			for (int i = 0; i < this.fallingObjects.size(); i++) {
				FallingObject currentObject = this.fallingObjects.get(i);
				boolean hasPriority = (currentObject.y > biggestYValue && !(isGameEnding && !currentObject.isGameEnding)) || (currentObject.isGameEnding && !isGameEnding);
				if ((currentObject.x == 2 || currentObject.x == 3) && !currentObject.isDead && hasPriority) {
					biggestYValue = currentObject.y;
					biggestYIndex = i;
					isGameEnding = currentObject.isGameEnding;
				}
			}
			if (biggestYIndex == -1) {
				shoot = false;
			}
			else {
				int targetedGuyX = this.fallingObjects.get(biggestYIndex).x * contentSize.width / 4 + contentSize.width / 8;
				int targetedGuyY = (int) (this.fallingObjects.get(biggestYIndex).y * contentSize.height);
				int diffX = targetedGuyX - contentSize.width * 3 / 4;
				int diffY = contentSize.height - contentSize.width / 16 - targetedGuyY;
				double appropriateAngle = Math.atan2(diffX, diffY);
				double stepSize = Math.PI * this.autoRotationSpeed * time / 20 / 1024;
				shoot = this.rotateAutoweapon(false, stepSize, appropriateAngle);
			}
			if (shoot && this.autoRightLoadState == 1 && this.autoRightBlockState == 0) {
				this.autoRightLoadState = 0;
				float xDistance = (float) Math.sin(this.rightAutoRotation);
				float yDistance = (float) Math.cos(this.rightAutoRotation);
				float projectileX = 3 / (float) 4 + xDistance / 32;
				float projectileY = 1 - contentSize.width / 16 / (float) contentSize.height - yDistance * contentSize.width / 32 / (float) contentSize.height;
				Projectile projectile = new Projectile(projectileX, projectileY, xDistance / contentSize.width, -yDistance / contentSize.height, this.autoProjectileSpeed, this.autoProjectileTextureName, this.autoProjectilePower, this.autoCoinMagnet, false);
				projectiles.add(projectile);
			}
		}
		
		// Use the crushers
		if (this.usingCrusher) {
			for (int i = 0; i < 4; i++) {
				List<FallingObject> presentObjects = new ArrayList<FallingObject>();
				for (int j = 0; j < this.fallingObjects.size(); j++) {
					FallingObject currentObject = this.fallingObjects.get(j);
					if (currentObject.isGameEnding) {
						continue;
					}
					if (currentObject.x != i) {
						continue;
					}
					float objectSizeInY = contentSize.width / 4 * currentObject.size / contentSize.height;
					float minPos = (contentSize.height / 2 - contentSize.width / 32) / (float) contentSize.height;
					float maxPos = (contentSize.height / 2 + contentSize.width / 32) / (float) contentSize.height + objectSizeInY;
					if (currentObject.y < minPos || currentObject.y > maxPos) {
						continue;
					}
					presentObjects.add(currentObject);
				}
				if (presentObjects.isEmpty()) {
					if (this.crushersStates[i] >= 0.5) {
						this.crushersStates[i] += time * this.crushFrequency / 1536;
						while (this.crushersStates[i] >= 1) {
							this.crushersStates[i]--;
						}
					}
				}
				else {
					boolean wasBelow = this.crushersStates[i] < 0.5;
					this.crushersStates[i] += time * this.crushFrequency / 1536;
					if (wasBelow && (this.crushersStates[i] >= 0.5)) {
						for (int j = 0; j < presentObjects.size(); j++) {
							presentObjects.get(j).hit(this.crushPower);
						}
					}
					while (this.crushersStates[i] >= 1) {
						this.crushersStates[i]--;
					}
				}
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
	
	/*
	 * @return Whether is the Autoweapon is shoot-ready.
	 */
	private boolean rotateAutoweapon(boolean isLeft, double stepSize, double appropriateRotation) {
		double currentRotation = isLeft ? this.leftAutoRotation : this.rightAutoRotation;
		boolean shoot;
		double initialDifference = smallestAngleDiffernce(currentRotation, appropriateRotation);
		if (initialDifference <= stepSize) {
			shoot = true;
			currentRotation = appropriateRotation;
		}
		else {
			double rotAfterAdding = currentRotation + stepSize;
			if (rotAfterAdding > Math.PI) {
				rotAfterAdding -= 2 * Math.PI;
			}
			if (smallestAngleDiffernce(rotAfterAdding, appropriateRotation) < initialDifference) {
				currentRotation = rotAfterAdding;
			}
			else {
				double rotAfterSubtracting = currentRotation - stepSize;
				if (rotAfterSubtracting < -Math.PI) {
					rotAfterSubtracting += 2 * Math.PI;
				}
				currentRotation = rotAfterSubtracting;
			}
			shoot = false;
		}
		if (isLeft) {
			this.leftAutoRotation = currentRotation;
		}
		else {
			this.rightAutoRotation = currentRotation;
		}
		return shoot;
	}

	private static double smallestAngleDiffernce(double angle1, double angle2) {
		if (Math.abs(angle1 - angle2) > Math.PI) {
			if (angle1 > 0) {
				return 2 * Math.PI - angle1 + angle2;
			}
			else {
				return 2 * Math.PI + angle1 - angle2;
			}
		}
		else {
			return Math.abs(angle1 - angle2);
		}
	}

	public void onMiddleChangingStage() {
		this.loadState = 1;
		this.autoLeftLoadState = 1;
		this.autoRightLoadState = 1;
		this.autoLeftBlockState = 0;
		this.autoRightBlockState = 0;
		this.mainBarrelBlockState = 0;
		this.autoLeftBlockOriginal = 0;
		this.autoRightBlockOriginal = 0;
		this.mainBarrelBlockOriginal = 0;
		for (int i = 0; i < this.projectiles.size(); i++) {
			if (this.projectiles.get(i).isFromBadGuy) {
				this.projectiles.remove(i);
				i--;
			}
		}
		if (this.usingCrusher) {
			this.crushersStates[0] = this.crushersStates[1] = this.crushersStates[2] = this.crushersStates[3] = 0;
		}
		this.fallingObjects.clear();
	}

	private static boolean circleCircleCollistion(float center1X, float center1Y, float center2X, float center2Y, float radius1, float radius2) {
		float centerDistanceSqr = (float) (Math.pow(center1X - center2X, 2) + Math.pow(center1Y - center2Y, 2));
		return centerDistanceSqr < Math.pow(radius1 + radius2, 2);
	}

	private static boolean doesCollide(float squareCenterX, float squareCenterY, float squareSize, float circleCenterX, float circleCenterY, float circleRadius) {
		return doesCollide(squareCenterX, squareCenterY, squareSize, squareSize, circleCenterX, circleCenterY, circleRadius);
	}

	private static boolean doesCollide(float rectCenterX, float rectCenterY, float rectXSize, float rectYSize, float circleCenterX, float circleCenterY, float circleRadius) {
		float diffX = Math.abs(rectCenterX - circleCenterX);
		float diffY = Math.abs(rectCenterY - circleCenterY);

		if (diffX > rectXSize + circleRadius) {
			return false;
		}
		if (diffY > rectYSize + circleRadius) {
			return false;
		}

		if (diffX <= rectXSize + circleRadius && diffY <= rectYSize) {
			return true;
		}
		if (diffY <= rectYSize + circleRadius && diffX <= rectXSize) {
			return true;
		}

		float distToCornerSqr = (float) (Math.pow(diffX - rectXSize, 2) + Math.pow(diffY - rectYSize, 2));
		return distToCornerSqr <= Math.pow(circleRadius, 2);
	}

	private void drawFallingObject(Graphics2D g, FallingObject fallingObject, Dimension contentSize) throws IOException {
		float objectPixelSize = contentSize.width * fallingObject.size / 4;
		int screenX = (int) ((fallingObject.x / (float) 4 + 1 / (float) 8) * contentSize.width - objectPixelSize / 2);
		int screenY = (int) (fallingObject.y * contentSize.height - objectPixelSize);
		g.drawImage(ResourceHandler.getTexture(fallingObject.textureName, (int) objectPixelSize), screenX, screenY, null);
		g.setColor(LIFE_GREEN);
		int filledSize = (int) (fallingObject.getShownLive() * objectPixelSize);
		g.fillRect((int) (screenX + objectPixelSize * 7 / 8), (int) (screenY + objectPixelSize - filledSize), (int) (objectPixelSize / 8), filledSize);
	}

	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		if (this.usingCrusher) {
			int crushersSize = contentSize.width / 16;
			float crushersY = (contentSize.height - crushersSize) / 2;
			float firstCrusherX = (contentSize.width / 4 - crushersSize) / 2;
			for (int i = 0; i < 4; i++) {
				this.crusher.draw(g, new Point((int) (firstCrusherX + i * contentSize.width / 4), (int) crushersY), crushersSize, this.crushersStates[i]);
			}
		}
		for (int i = 0; i < this.fallingObjects.size(); i++) {
			this.drawFallingObject(g, this.fallingObjects.get(i), contentSize);
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
		if (this.mainBarrelBlockState != 0) {
			g.setColor(new Color(0, 0, 0, this.mainBarrelBlockState / this.mainBarrelBlockOriginal));
			g.fillRect(baseScreenX, baseScreenY, contentSize.width / 8, contentSize.width / 16);
		}
		Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
		AffineTransform transform = AffineTransform.getTranslateInstance(barrelCenter.x, barrelCenter.y);
		double vecX = -barrelCenter.x + mousePosition.x;
		double vecY = -mousePosition.y + barrelCenter.y;
		transform.rotate(vecY, vecX);
		transform.translate(-contentSize.width / 32, -contentSize.width / 32);
		g.drawImage(ResourceHandler.getTexture(this.textureName, contentSize.width / 16), transform, null);
		if (this.usingAutoweapon) {
			int leftBaseX = contentSize.width / 4 - contentSize.width / 32;
			int baseY = contentSize.height - contentSize.width / 16;
			Image autoBaseTexture = ResourceHandler.getTexture("AutoweaponBase.png", contentSize.width / 16);
			g.drawImage(autoBaseTexture, leftBaseX, baseY, null);
			greenWidth = (int) (contentSize.width / 16 * this.autoLeftLoadState);
			g.setColor(TRANSPARENT_GREEN);
			g.fillRect(leftBaseX, baseY, greenWidth, contentSize.width / 16);
			g.setColor(TRANSPARENT_RED);
			g.fillRect(leftBaseX + greenWidth, baseY, contentSize.width / 16 - greenWidth, contentSize.width / 16);
			if (this.autoLeftBlockState != 0) {
				g.setColor(new Color(0, 0, 0, this.autoLeftBlockState / this.autoLeftBlockOriginal));
				g.fillRect(leftBaseX, baseY, contentSize.width / 16, contentSize.width / 16);
			}
			int rightBaseX = contentSize.width * 3 / 4 - contentSize.width / 32;
			g.drawImage(autoBaseTexture, rightBaseX, baseY, null);
			greenWidth = (int) (contentSize.width / 16 * this.autoRightLoadState);
			g.setColor(TRANSPARENT_GREEN);
			g.fillRect(rightBaseX, baseY, greenWidth, contentSize.width / 16);
			g.setColor(TRANSPARENT_RED);
			g.fillRect(rightBaseX + greenWidth, baseY, contentSize.width / 16 - greenWidth, contentSize.width / 16);
			if (this.autoRightBlockState != 0) {
				g.setColor(new Color(0, 0, 0, this.autoRightBlockState / this.autoRightBlockOriginal));
				g.fillRect(rightBaseX, baseY, contentSize.width / 16, contentSize.width / 16);
			}

			int leftBarrelCenterX = contentSize.width / 4;
			int rightBarrelCenterX = contentSize.width * 3 / 4;
			AffineTransform leftBarrelTransfom = AffineTransform.getTranslateInstance(leftBarrelCenterX, baseY);
			leftBarrelTransfom.rotate(this.leftAutoRotation);
			leftBarrelTransfom.translate(-contentSize.width / 32, -contentSize.width / 32);
			Image autoBarrelTexture = ResourceHandler.getTexture(this.autoTextureName, contentSize.width / 16);
			g.drawImage(autoBarrelTexture, leftBarrelTransfom, null);

			AffineTransform rightBarrelTransfom = AffineTransform.getTranslateInstance(rightBarrelCenterX, baseY);
			rightBarrelTransfom.rotate(this.rightAutoRotation);
			rightBarrelTransfom.translate(-contentSize.width / 32, -contentSize.width / 32);
			g.drawImage(autoBarrelTexture, rightBarrelTransfom, null);
		}
		for (int i = 0; i < this.projectiles.size(); i++) {
			Projectile currentProjectile = this.projectiles.get(i);
			AffineTransform projectileTransform = AffineTransform.getTranslateInstance(currentProjectile.x * contentSize.width, currentProjectile.y * contentSize.height);
			projectileTransform.rotate(-currentProjectile.dirY * contentSize.height, currentProjectile.dirX * contentSize.width);
			projectileTransform.translate(-contentSize.width / (float) 128, -contentSize.width / (float) 128);
			g.drawImage(ResourceHandler.getTexture(currentProjectile.textureName, contentSize.width / 64), projectileTransform, null);
		}
	}

	public void clicked(int x, int y, Dimension contentSize) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		if (this.loadState == 1 && this.mainBarrelBlockState == 0) {
			Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
			float vecX = x - barrelCenter.x;
			float vecY = y - barrelCenter.y;
			float factor = (float) (contentSize.width / (float) 32 / Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2)));
			Point firePoint = new Point((int) (barrelCenter.x + vecX * factor), (int) (barrelCenter.y + vecY * factor));
			float dirX = vecX / (float) contentSize.width;
			float dirY = vecY / (float) contentSize.height;
			Projectile projectile = new Projectile(firePoint.x / (float) contentSize.width, firePoint.y / (float) contentSize.height, dirX, dirY, this.projectileSpeed, this.projectileTextureName, this.projectilePower, this.coinMagnet, false);
			this.projectiles.add(projectile);
			ResourceHandler.playSound(this.shootSoundName);
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
