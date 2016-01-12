package game;

import java.io.IOException;

import game.mechanics.Spawner;
import game.mechanics.Stage;
import game.mechanics.weaponry.PropertyImplementation;
import game.mechanics.weaponry.UpgradablePropertyImplementation;
import game.mechanics.weaponry.Weapon;
import game.io.IOBase;
import game.io.IOInitialization;
import game.io.Logging;
import game.screens.ConnectionProblemScreen;
import game.screens.InitializationScreen;
import game.screens.Screen;
import game.screens.StartScreen;
import game.screens.WelcomeScreen;

public class Main {

	// Set to false when you're making a public release.
	public static final boolean debugging = true;
	
	public static boolean running;
	public static Screen currentScreen;
	public static Stage[] stages;
	public static Weapon[] barrels;
	public static Weapon[] autoweapons;
	public static IntHolder selectedBarrel = new IntHolder(0);
	public static IntHolder selectedAutoweapon = new IntHolder(-1);
	public static int money = 0;
	public static String initText;
	/**
	 * Describes the most difficult stage achieved divided by 5.
	 */
	public static int maxReachedStage;
	public static Thread updateThread;
	public static boolean firstRun;

	public static final int stageShowTime = 60;

	public static void main(String[] arg0) {
		Logging.log("Hello!");
		Logging.logStartSectionTag("INIT");
		Logging.log("Initializing");
		Gui.intializeGraphics();
		currentScreen = new InitializationScreen();
		updateThread = new Thread(null, new Updater(), "Bad Guys Game - Refresh Thread");
		updateThread.start();
		Achievement.initializeAchievements();
		boolean IOsuccessfull = false;
		try {
			IOsuccessfull = IOInitialization.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (IOsuccessfull) {
			initializeStages();
			initializeBarrels();
			initializeAutoweapons();
			try {
				IOBase.loadSaveIfPresent();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (firstRun) {
				currentScreen = new WelcomeScreen();
			}
			else {
				currentScreen = new StartScreen();
			}
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
		Spawner firstBossSpw = new Spawner.FirstBossSpawner();
		Spawner speedySpw = new Spawner.SpeedySpawner();
		stages = new Stage[26];
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
		stages[14] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, fastSpw }, new int[] { 20, 40, 60 });
		stages[15] = new Stage(new Spawner[] { heavyArmoredSpw, basicSpw, fastSpw, heavyArmoredSpw, basicSpw, fastSpw }, new int[] { 20, 20, 20, 80, 80, 80 });
		stages[16] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, armoredSpw, armoredSpw, armoredSpw, armoredSpw }, new int[] { 20, 20, 100, 100, 100, 100 });
		stages[17] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw }, new int[] { 20, 20, 20, 20 });
		stages[18] = new Stage(new Spawner[] { fastSpw, fastSpw, fastSpw, fastSpw, heavyArmoredSpw, heavyArmoredSpw, basicSpw, basicSpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[19] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, fastSpw, fastSpw }, new int[] { 20, 20, 20, 20, 60, 60 });
		stages[20] = new Stage(new Spawner[] { firstBossSpw }, new int[] { 60 });
		stages[21] = new Stage(new Spawner[] { fastSpw, fastSpw, armoredSpw, armoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw}, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[22] = new Stage(makeHomogenousSpawnerArray(basicSpw, 16), new int[] { 20, 20, 20, 20, 80, 80, 80, 80, 140, 140, 140, 140, 200, 200, 200, 200 });
		stages[23] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, fastSpw, fastSpw}, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[24] = new Stage(new Spawner[] { fastSpw, fastSpw, armoredSpw, armoredSpw, fastSpw, fastSpw, armoredSpw, armoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw, heavyArmoredSpw}, new int[] { 20, 20, 20, 20, 80, 80, 80, 80, 140, 140, 140, 140 });
		stages[25] = new Stage(new Spawner[] { speedySpw }, new int[] { 20 });
	}

	private static void initializeBarrels() {
		barrels = new Weapon[3];
		PropertyImplementation loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 15 }, new float[] { -0.2f }, 1);
		PropertyImplementation projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 20 }, new float[] { 0.5f }, 1);
		PropertyImplementation projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 20 }, new float[] { 0.5f }, 1);
		barrels[0] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed }, 0, "BasicBarrel.png", "BasicProjectile.png", true, "Basic Barrel", -1);

		loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 20, 30 }, new float[] { -0.13f, -0.05f }, 0.8f);
		projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 50 }, new float[] { 1 }, 1);
		projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 10, 25, 50 }, new float[] { 0.75f, 0.5f, 0.5f }, 1.75f);
		barrels[1] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed }, 50, "FastBarrel.png", "BasicProjectile.png", false, "Fast Projectile Barrel", 0);

		loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 10, 30 }, new float[] { -0.5f, -0.2f }, 1.5f);
		projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 25, 45 }, new float[] { 1, 0.5f }, 2);
		projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 10, 25, 50 }, new float[] { 0.5f, 0.5f, 0.3f }, 0.7f);
		PropertyImplementation coinMagnet = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.coinMagnetID], new int[] { 50 }, new float[] { 1 }, 1);
		barrels[2] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, coinMagnet }, 75, "MagneticBarrel.png", "MagneticProjectile.png", false, "Magnetic Barrel", 4);
	}

	private static void initializeAutoweapons() {
		autoweapons = new Weapon[1];
		PropertyImplementation loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 15 }, new float[] { -0.2f }, 1.5f);
		PropertyImplementation projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 20 }, new float[] { 0.5f }, 0.7f);
		PropertyImplementation projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 20 }, new float[] { 0.5f }, 0.7f);
		PropertyImplementation rotationSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.rotationSpeedID], new int[] { 15 }, new float[] { 0.3f }, 1f);
		autoweapons[0] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, rotationSpeed }, 50, "BasicAutoweapon.png", "BasicProjectile.png", false, "Basic autoweapon", 3);
	}
	
	private static Spawner[] makeHomogenousSpawnerArray(Spawner element, int length) {
		Spawner[] array = new Spawner[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = element;
		}
		return array;
	}
	
	public static Weapon getSelectedBarrel() {
		return barrels[selectedBarrel.value];
	}
	
	public static Weapon getSelectedAutoweapon() {
		if (selectedAutoweapon.value == -1) {
			return null;
		}
		else {
			return autoweapons[selectedAutoweapon.value];
		}
	}

	private static class Updater implements Runnable {

		@Override
		public void run() {
			Logging.log("Starting the game loop.");
			running = true;
			Gui.refreshRate = Gui.maxScreenRefreshRate;
			System.out.println("Max refresh rate: " + Gui.refreshRate);
			float nsForRefresh = 1000000000 / (float) Gui.refreshRate;
			while (running) {
				long startTime = System.nanoTime();
				int spentTime = (int) (nsForRefresh / (float) 1000000);
				currentScreen.update(spentTime);
				Achievement.update(spentTime);
				long endTime = System.nanoTime();
				int delayNs = (int) (endTime - startTime);
				int remaingNs = (int) (nsForRefresh - delayNs);
				if (remaingNs > 0) {
					try {
						Thread.sleep(remaingNs / 1000000, remaingNs % 1000000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (delayNs == 0) {
					Gui.refreshRate = Gui.maxScreenRefreshRate;
				}
				else {
					Gui.refreshRate = 1000000000 / delayNs;
				}
				if (Gui.refreshRate == 0) {
					Gui.refreshRate = 1;
				}
				else if (Gui.refreshRate > Gui.maxScreenRefreshRate) {
					Gui.refreshRate = Gui.maxScreenRefreshRate;
				}
				nsForRefresh = 1000000000 / (float) Gui.refreshRate;
				Gui.gui.repaint();
			}
			Logging.log("Game loop has been broken.");
		}

	}
}
