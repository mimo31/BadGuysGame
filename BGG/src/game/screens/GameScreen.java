package game.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import game.BadGuy;
import game.Barrel;
import game.Gui;
import game.IO;
import game.Main;
import game.PaintUtils;
import game.Projectile;
import game.Screen;

public class GameScreen extends Screen{
	
	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 127, 127);
	public static final Color TRANSPARENT_RED = new Color(255, 0, 0, 127);

	//Components
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
		this.g = g;
	}
	
	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(g, contentSize, mousePosition);
		for (int i = 0; i < Main.badGuys.size(); i++) {
			this.drawBadGuy(Main.badGuys.get(i));
		}
		int baseScreenX = contentSize.width / 2 - contentSize.width / 16;
		int baseScreenY = contentSize.height - contentSize.width / 16;
		g.drawImage(IO.getTexture("Base.png", contentSize.width / 8), baseScreenX, baseScreenY, null);
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
		g.drawImage(IO.getTexture(Main.getSelectedBarrel().textureName, contentSize.width / 16), transform, null);
		for (int i = 0; i < Main.projectiles.size(); i++) {
			Projectile currentProjectile = Main.projectiles.get(i);
			g.drawImage(IO.getTexture(currentProjectile.textureName, contentSize.width / 64), (int) (currentProjectile.x * contentSize.width - contentSize.width / (float) 128), (int) (currentProjectile.y * contentSize.height - contentSize.width / (float) 128), null);
		}
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
		this.g.drawImage(IO.getTexture(badGuy.textureName, contentSize.width / 16), screenX, screenY, null);
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
				float speed = selectedBarrel.getProjectileSpeed();
				String textureName = selectedBarrel.projectileTextureName;
				float hitPower = selectedBarrel.getProjectilePower();
				float dirX = vecX / (float) contentSize.width;
				float dirY =  vecY / (float) contentSize.height;
				Projectile projectile = new Projectile(firePoint.x / (float) contentSize.width, firePoint.y / (float) contentSize.height, dirX, dirY, speed, textureName, hitPower);
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
						Main.showingStageMousePos = Gui.getMousePanePosition();
					}
				}
			}
			float heightFraction = contentSize.width / (float) contentSize.height / (float) 128;
			for (int i = 0; i < Main.projectiles.size(); i++) {
				Projectile currentProjectile = Main.projectiles.get(i);
				currentProjectile.x += currentProjectile.dirX / 128;
				currentProjectile.y += currentProjectile.dirY / 128;
				if (currentProjectile.x + 1 / (float) 128 < 0 || currentProjectile.x - 1 / (float) 128 >= 1 || currentProjectile.y + heightFraction < 0 || currentProjectile.y - heightFraction >= 1) {
					Main.projectiles.remove(i);
					i--;
				}
				for (int j = 0; j < Main.badGuys.size(); j++) {
					BadGuy currentBadGuy = Main.badGuys.get(j);
					if (doesCollide(currentBadGuy.x / (float) 4 + 1 / (float) 8, currentBadGuy.y * contentSize.height / (float) contentSize.width - 1 / (float) 32, 1 / (float) 32, currentProjectile.x, currentProjectile.y * contentSize.height / (float) contentSize.width, 1 / (float) 128)) {
						currentBadGuy.hit(currentProjectile.hitPower);
						Main.projectiles.remove(i);
						i--;
						break;
					}
				}
			}
			if (Main.loadState != 1) {
				Main.loadState += 1 / (float) 32;
				if (Main.loadState > 1) {
					Main.loadState = 1;
				}
			}
			Main.timeInStage++;
		}
		else {
			Main.showingStageState++;
			if (Main.showingStageState == 60 && (Main.gameOver || Main.noMoreStages)) {
				Screen.startNew(new StartScreen());
			}
			else if (Main.showingStageState == 120) {
				Main.showingStage = false;
			}
		}
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
}
