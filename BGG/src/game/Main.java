package game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class Main {

	public static boolean inStartScreen;
	public static Stage[] stages;
	public static int currentStage = 0;
	public static int timeInStage = 0;
	public static ArrayList<BadGuy> badGuys = new ArrayList<BadGuy>();
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private static ArrayList<BadGuy> badGuysBuffer = new ArrayList<BadGuy>();
	public static float loadState = 1;

	public static Point showingStageMousePos;
	public static boolean showingStage;
	public static int showingStageState = 0;
	public static boolean gameOver = false;
	public static boolean noMoreStages = false;

	public static Timer updateTimer = new Timer(40, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			update(Gui.getContentSize());
		}

	});

	public static Timer repaintTimer = new Timer(17, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Gui.gui.repaint();
		}

	});

	public static void main(String[] arg0) {
		Gui.intializeGraphics();
		initializeStages();
		inStartScreen = true;
		updateTimer.start();
		repaintTimer.start();
	}

	private static void initializeStages() {
		Spawner basicSpw = new Spawner.BasicSpawner();
		Spawner fastSpw = new Spawner.FastSpawner();
		Spawner armoredSpw = new Spawner.ArmoredSpawner();
		stages = new Stage[9];
		stages[0] = new Stage(new Spawner[] { basicSpw }, new int[]{10});
		stages[1] = new Stage(new Spawner[] { basicSpw, basicSpw }, new int[]{10, 100});
		stages[2] = new Stage(new Spawner[] { basicSpw, basicSpw, basicSpw }, new int[]{10, 75, 200});
		stages[3] = new Stage(new Spawner[] { fastSpw }, new int[]{50});
		stages[4] = new Stage(new Spawner[] { fastSpw, basicSpw, basicSpw }, new int[]{20, 100, 100});
		stages[5] = new Stage(new Spawner[] { fastSpw, fastSpw }, new int[]{20, 20});
		stages[6] = new Stage(new Spawner[] { basicSpw, basicSpw, basicSpw, basicSpw }, new int[]{20, 20, 20, 20});
		stages[7] = new Stage(new Spawner[] { armoredSpw }, new int[50]);
		stages[8] = new Stage(new Spawner[] { basicSpw, basicSpw, armoredSpw, armoredSpw }, new int[]{20, 20, 80, 80});
	}

	public static void startPlaying() {
		showingStage = true;
		showingStageMousePos = Gui.getMousePanePosition();
	}

	private static void startNewStage() {
		projectiles.clear();
		if (currentStage == stages.length - 1) {
			noMoreStages = true;
		}
		else {
			currentStage++;
			timeInStage = 0;
		}
		showingStage = true;
		showingStageMousePos = Gui.getMousePanePosition();
		showingStageState = 0;
	}

	private static void update(Dimension contentSize) {
		if (!inStartScreen && !showingStage) {
			for (int i = 0; i < stages[currentStage].spawners.length; i++) {
				if (stages[currentStage].spawnTimes[i] == timeInStage) {
					badGuysBuffer.add(stages[currentStage].spawners[i].getBadGuy());
				}
			}
			boolean[] isColumnOccupied = new boolean[4];
			float heightSizeOfABadGuy = contentSize.width / (float) 16 / contentSize.height;
			for (int i = 0; i < badGuys.size(); i++) {
				BadGuy currentBadGuy = badGuys.get(i);
				currentBadGuy.hittingProgress += 1 / (float) 32;
				if (currentBadGuy.isBeingHit && currentBadGuy.hittingProgress >= 1) {
					currentBadGuy.isBeingHit = false;
					currentBadGuy.live -= currentBadGuy.hitBy;
					if (currentBadGuy.isDead) {
						badGuys.remove(i);
						i--;
						if (badGuys.isEmpty()) {
							if (badGuysBuffer.isEmpty() && stages[currentStage].allSpawned(timeInStage)) {
								startNewStage();
							}
						}
					}
				}
				if (!currentBadGuy.isDead) {
					currentBadGuy.move();
					if (currentBadGuy.y > 1) {
						// Game Over
						showingStage = true;
						gameOver = true;
						showingStageState = 0;
						showingStageMousePos = Gui.getMousePanePosition();
					}
					if (currentBadGuy.y < heightSizeOfABadGuy) {
						isColumnOccupied[currentBadGuy.x] = true;
					}
				}
			}
			while (!isFull(isColumnOccupied) && !(badGuysBuffer.size() == 0)) {
				badGuysBuffer.get(0).x = takeFree(isColumnOccupied);
				badGuys.add(badGuysBuffer.get(0));
				badGuysBuffer.remove(0);
			}
			float heightFraction = contentSize.width / (float) contentSize.height / (float) 128;
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile currentProjectile = projectiles.get(i);
				currentProjectile.x += currentProjectile.dirX / 128;
				currentProjectile.y += currentProjectile.dirY / 128;
				if (currentProjectile.x + 1 / (float) 128 < 0 || currentProjectile.x - 1 / (float) 128 >= 1 || currentProjectile.y + heightFraction < 0 || currentProjectile.y - heightFraction >= 1) {
					projectiles.remove(i);
					i--;
				}
				for (int j = 0; j < badGuys.size(); j++) {
					BadGuy currentBadGuy = badGuys.get(j);
					if (doesCollide(currentBadGuy.x / (float) 4 + 1 / (float) 8, currentBadGuy.y * contentSize.height / (float) contentSize.width - 1 / (float) 32, 1 / (float) 32, currentProjectile.x, currentProjectile.y * contentSize.height / (float) contentSize.width, 1 / (float) 128)) {
						currentBadGuy.hit(currentProjectile.hitPower);
						projectiles.remove(i);
						i--;
						break;
					}
				}
			}
			if (loadState != 1) {
				loadState += 1 / (float) 32;
				if (loadState > 1) {
					loadState = 1;
				}
			}
			timeInStage++;
		}
		else {
			if (showingStage) {
				showingStageState++;
				if (currentStage == 0 && showingStageState == 60) {
					inStartScreen = false;
				}
				if (showingStageState == 120) {
					showingStage = false;
					loadState = 1;
				}
				if (gameOver || noMoreStages) {
					if (showingStageState == 60) {
						loadState = 1;
						badGuys.clear();
						badGuysBuffer.clear();
						projectiles.clear();
						inStartScreen = true;
						currentStage = 0;
						timeInStage = 0;
					}
					else if (showingStageState == 120) {
						showingStage = false;
						gameOver = false;
						noMoreStages = false;
						showingStageState = 0;
					}
				}
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
}
