package game;

import java.io.IOException;

import game.mechanics.Spawner;
import game.mechanics.Stage;
import game.mechanics.barrels.Barrel;
import game.mechanics.barrels.BarrelPropertyImplementation;
import game.mechanics.barrels.BarrelUpgradablePropertyImplementation;
import game.io.IOBase;
import game.io.IOInitialization;
import game.io.Logging;
import game.screens.ConnectionProblemScreen;
import game.screens.InitializationScreen;
import game.screens.StartScreen;

public class Main {

	public static boolean running;
	public static Screen currentScreen;
	public static Stage[] stages;
	public static Barrel[] barrels;
	public static int selectedBarrel;
	public static int money = 0;
	public static String initText;
	/**
	 * Describes the most difficult stage achieved divided by 5.
	 */
	public static int maxReachedStage;
	public static Thread updateThread;

	public static final int stageShowTime = 60;

	public static void main(String[] arg0) {
		Logging.log("Hello!");
		Logging.logStartSectionTag("INIT");
		Logging.log("Initializing");
		Gui.intializeGraphics();
		currentScreen = new InitializationScreen();
		updateThread = new Thread(null, new Updater(), "Bad Guys Game - Refresh Thread");
		updateThread.start();
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
		stages[14] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, fastSpw }, new int[] { 20, 40, 60 });
		stages[15] = new Stage(new Spawner[] { heavyArmoredSpw, basicSpw, fastSpw, heavyArmoredSpw, basicSpw, fastSpw }, new int[] { 20, 20, 20, 80, 80, 80 });
		stages[16] = new Stage(new Spawner[] { heavyArmoredSpw, heavyArmoredSpw, armoredSpw, armoredSpw, armoredSpw, armoredSpw }, new int[] { 20, 20, 100, 100, 100, 100 });
	}

	private static void initializeBarrels() {
		barrels = new Barrel[3];
		BarrelPropertyImplementation loadingTime = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.loadingTimeID], new int[] { 15 }, new float[] { -0.2f }, 1);
		BarrelPropertyImplementation projectilePower = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.projectilePowerID], new int[] { 20 }, new float[] { 0.5f }, 1);
		BarrelPropertyImplementation projectileSpeed = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.projectileSpeedID], new int[] { 20 }, new float[] { 0.5f }, 1);
		barrels[0] = new Barrel(new BarrelPropertyImplementation[] { loadingTime, projectilePower, projectileSpeed }, 0, "BasicBarrel.png", "BasicProjectile.png", true, "Basic Barrel");

		loadingTime = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.loadingTimeID], new int[] { 20, 30 }, new float[] { -0.13f, -0.05f }, 0.8f);
		projectilePower = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.projectilePowerID], new int[] { 50 }, new float[] { 1 }, 1);
		projectileSpeed = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.projectileSpeedID], new int[] { 10, 25, 50 }, new float[] { 0.75f, 0.5f, 0.5f }, 1.75f);
		barrels[1] = new Barrel(new BarrelPropertyImplementation[] { loadingTime, projectilePower, projectileSpeed }, 50, "FastBarrel.png", "BasicProjectile.png", false, "Fast Projectile Barrel");

		loadingTime = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.loadingTimeID], new int[] { 10, 30 }, new float[] { -0.5f, -0.2f }, 1.5f);
		projectilePower = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.projectilePowerID], new int[] { 25, 45 }, new float[] { 1, 0.5f }, 2);
		projectileSpeed = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.projectileSpeedID], new int[] { 10, 25, 50 }, new float[] { 0.5f, 0.5f, 0.3f }, 0.7f);
		BarrelPropertyImplementation coinMagnet = new BarrelUpgradablePropertyImplementation(Barrel.propertiesIndex[Barrel.coinMagnetID], new int[] { 50 }, new float[] { 1 }, 1);
		barrels[2] = new Barrel(new BarrelPropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, coinMagnet }, 75, "MagneticBarrel.png", "MagneticProjectile.png", false, "Magnetic Barrel");
		selectedBarrel = 0;
	}

	public static Barrel getSelectedBarrel() {
		return barrels[selectedBarrel];
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
				currentScreen.update((int) (nsForRefresh / (float) 1000000));
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
