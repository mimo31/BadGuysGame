package game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import game.Barrel.BarrelGameProperty;
import game.io.IOBase;
import game.io.IOInitialization;
import game.io.Logging;
import game.screens.ConnectionProblemScreen;
import game.screens.InitializationScreen;
import game.screens.StartScreen;

import javax.swing.Timer;

public class Main {

	public static Screen currentScreen;
	public static Stage[] stages;
	public static int currentStage = 0;
	public static int timeInStage = 0;
	public static Barrel[] barrels;
	public static int selectedBarrel;
	public static ArrayList<BadGuy> badGuys = new ArrayList<BadGuy>();
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private static ArrayList<BadGuy> badGuysBuffer = new ArrayList<BadGuy>();
	public static ArrayList<Coin> coins = new ArrayList<Coin>();
	public static float loadState = 1;
	public static int money = 0;
	public static String initText;
	/**
	 * Describes the most difficult stage achieved divided by 5.
	 */
	public static int maxReachedStage;

	public static Point showingStageMousePos;
	public static boolean showingStage;
	public static int showingStageState = 0;
	public static boolean gameOver = false;
	public static boolean noMoreStages = false;

	public static final int stageShowTime = 60;

	public static Timer updateTimer = new Timer(40, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentScreen.update();
		}

	});

	public static Timer repaintTimer = new Timer(17, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Gui.gui.repaint();
		}

	});

	public static void main(String[] arg0) {
		Logging.log("Hello!");
		Logging.logStartSectionTag("INIT");
		Logging.log("Initializing");
		Gui.intializeGraphics();
		updateTimer.start();
		repaintTimer.start();
		currentScreen = new InitializationScreen();
		boolean IOsuccessfull = false;
		try {
			IOsuccessfull = IOInitialization.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (IOsuccessfull) {
			initializeStages();
			initializeBarrels();
			try {
				IOBase.loadSaveIfPresent();
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentScreen = new StartScreen();
		}
		else {
			currentScreen = new ConnectionProblemScreen();
		}
		if (IOsuccessfull) {
			Logging.log("Initialization complete!");
		}
		else {
			Logging.log("Initialization complete! (unsuccessfully)");
		}
		Logging.logEndSectionTag("INIT");
	}
	
	private static void initializeStages() {
		Spawner basicSpw = new Spawner.BasicSpawner();
		Spawner fastSpw = new Spawner.FastSpawner();
		Spawner armoredSpw = new Spawner.ArmoredSpawner();
		Spawner heavyArmoredSpw = new Spawner.HeavyArmoredSpawner();
		stages = new Stage[17];
		stages[0] = new Stage(new Spawner[] { basicSpw }, new int[] { 10 });
		stages[1] = new Stage(new Spawner[] { basicSpw, basicSpw }, new int[] { 10, 100 });
		stages[2] = new Stage(new Spawner[] { basicSpw, basicSpw, basicSpw }, new int[] { 10, 75, 200 });
		stages[3] = new Stage(new Spawner[] { fastSpw }, new int[] { 50 });
		stages[4] = new Stage(new Spawner[] { fastSpw, basicSpw, basicSpw }, new int[] { 20, 100, 100 });
		stages[5] = new Stage(new Spawner[] { fastSpw, fastSpw }, new int[] { 20, 20 });
		stages[6] = new Stage(new Spawner[] { basicSpw, basicSpw, basicSpw, basicSpw }, new int[] { 20, 20, 20, 20 });
		stages[7] = new Stage(new Spawner[] { armoredSpw }, new int[] { 50 });
		stages[8] = new Stage(new Spawner[] { basicSpw, basicSpw, armoredSpw, armoredSpw }, new int[] { 20, 20, 80, 80 });
		stages[9] = new Stage(new Spawner[] { armoredSpw, armoredSpw, fastSpw, fastSpw }, new int[] { 20, 20, 80, 80 });
		stages[10] = new Stage(new Spawner[] { basicSpw, basicSpw, basicSpw, basicSpw, basicSpw, basicSpw, basicSpw, basicSpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[11] = new Stage(new Spawner[] { armoredSpw, armoredSpw, armoredSpw, fastSpw }, new int[] { 20, 20, 20, 100 });
		stages[12] = new Stage(new Spawner[] { fastSpw, fastSpw, fastSpw, armoredSpw, armoredSpw }, new int[] { 20, 20, 20, 60, 60 });
		stages[13] = new Stage(new Spawner[] { heavyArmoredSpw }, new int[] { 20 });
		stages[14] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw,  fastSpw }, new int[] { 20, 40, 60 });
		stages[15] = new Stage(new Spawner[] { heavyArmoredSpw, basicSpw,  fastSpw, heavyArmoredSpw, basicSpw,  fastSpw }, new int[] { 20, 20, 20, 80, 80, 80 });
		stages[16] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw,  armoredSpw, armoredSpw, armoredSpw,  armoredSpw }, new int[] { 20, 20, 100, 100, 100, 100 });
	}

	private static void initializeBarrels() {
		barrels = new Barrel[2];
		BarrelGameProperty loadingTime = new BarrelGameProperty(new int[] { 15 }, new float[] { -0.2f }, 1);
		BarrelGameProperty projectilePower = new BarrelGameProperty(new int[] { 20 }, new float[] { 0.5f }, 1);
		BarrelGameProperty projectileSpeed = new BarrelGameProperty(new int[] { 20 }, new float[] { 0.5f }, 1);
		barrels[0] = new Barrel(new BarrelGameProperty[] { loadingTime, projectilePower, projectileSpeed }, 0, "BasicBarrel.png", "BasicProjectile.png", true, "Basic Barrel");
		loadingTime = new BarrelGameProperty(new int[] { 20, 30 }, new float[] { -0.13f, -0.05f }, 0.8f);
		projectilePower = new BarrelGameProperty(new int[] { 50 }, new float[] { 1 }, 1);
		projectileSpeed = new BarrelGameProperty(new int[] { 10, 25, 50 }, new float[] { 0.75f, 0.5f, 0.5f }, 1.75f);
		barrels[1] = new Barrel(new BarrelGameProperty[] { loadingTime, projectilePower, projectileSpeed }, 50, "FastBarrel.png", "BasicProjectile.png", false, "Fast Projectile Barrel");
		selectedBarrel = 0;
	}

	public static void startNewStage() {
		if (currentStage == stages.length - 1) {
			noMoreStages = true;
			updateMaxStage();
		}
		else {
			currentStage++;
			timeInStage = 0;
		}
		showingStage = true;
		showingStageMousePos = Gui.getMousePanePosition();
		showingStageState = 0;
	}

	public static Barrel getSelectedBarrel() {
		return barrels[selectedBarrel];
	}

	public static void addBadGuyToBuffer(BadGuy badGuy) {
		badGuysBuffer.add(badGuy);
	}

	public static void spawnBadGuysFromBuffer(Dimension contentSize) {
		boolean[] isColumnOccupied = new boolean[4];
		float heightSizeOfABadGuy = contentSize.width / (float) 16 / contentSize.height;
		for (int i = 0; i < badGuys.size(); i++) {
			BadGuy currentBadGuy = badGuys.get(i);
			if (!currentBadGuy.isDead && currentBadGuy.y < heightSizeOfABadGuy) {
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

	public static boolean isStageCompleted() {
		if (badGuys.isEmpty() && badGuysBuffer.isEmpty() && stages[currentStage].allSpawned(timeInStage)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Resets all the in-game variables to their default values. Should be
	 * called when the game ends.
	 */
	public static void resetTheGame() {
		badGuys.clear();
		badGuysBuffer.clear();
		coins.clear();
		currentStage = 0;
		projectiles.clear();
		timeInStage = 0;
		loadState = 1;
	}

	public static void updateMaxStage() {
		int reachedBy5 = (int)Math.floor(Main.currentStage / 5);
		if (reachedBy5 > maxReachedStage) {
			maxReachedStage = reachedBy5;
		}
	}
}
