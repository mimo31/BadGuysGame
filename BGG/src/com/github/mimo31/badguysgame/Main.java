package com.github.mimo31.badguysgame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;

import com.github.mimo31.badguysgame.io.IOBase;
import com.github.mimo31.badguysgame.io.IOInitialization;
import com.github.mimo31.badguysgame.io.Logging;
import com.github.mimo31.badguysgame.mechanics.Spawner;
import com.github.mimo31.badguysgame.mechanics.Stage;
import com.github.mimo31.badguysgame.mechanics.weaponry.Crusher;
import com.github.mimo31.badguysgame.mechanics.weaponry.NotUpgradablePropertyImplementation;
import com.github.mimo31.badguysgame.mechanics.weaponry.PropertyImplementation;
import com.github.mimo31.badguysgame.mechanics.weaponry.UpgradablePropertyImplementation;
import com.github.mimo31.badguysgame.mechanics.weaponry.Weapon;
import com.github.mimo31.badguysgame.screens.ConnectionProblemScreen;
import com.github.mimo31.badguysgame.screens.InitializationScreen;
import com.github.mimo31.badguysgame.screens.Screen;
import com.github.mimo31.badguysgame.screens.StartScreen;
import com.github.mimo31.badguysgame.screens.WelcomeScreen;

public class Main {

	// Set to false when you're making a public release.
	public static final boolean debugging = true;

	public static boolean running;
	public static Screen currentScreen;
	public static Stage[] stages;
	public static Weapon[] barrels;
	public static Weapon[] autoweapons;
	public static Crusher[] crushers;
	public static IntHolder selectedBarrel = new IntHolder(0);
	public static IntHolder selectedAutoweapon = new IntHolder(-1);
	public static IntHolder selectedCrusher = new IntHolder(-1);
	public static int money = 0;
	public static String initText;
	/**
	 * Describes the most difficult stage achieved divided by 5.
	 */
	public static int maxReachedStage;
	public static Thread updateThread;
	public static boolean firstRun;
	public static boolean inDebugMode;

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
			initializeCrushers();
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
		Spawner heavilyArmoredSpw = new Spawner.HeavyArmoredSpawner();
		Spawner firstBossSpw = new Spawner.FirstBossSpawner();
		Spawner secondBossSpw = new Spawner.SecondBossSpawner();
		Spawner speedySpw = new Spawner.SpeedySpawner();
		Spawner shootingSpw = new Spawner.ShootingSpawner();
		Spawner woodenBlockerSpw = new Spawner.WoodenBlockSpawner();
		stages = new Stage[40];
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
		stages[13] = new Stage(new Spawner[] { heavilyArmoredSpw }, new int[] { 20 });
		stages[14] = new Stage(new Spawner[] { heavilyArmoredSpw, heavilyArmoredSpw, fastSpw }, new int[] { 20, 40, 60 });
		stages[15] = new Stage(new Spawner[] { heavilyArmoredSpw, basicSpw, fastSpw, heavilyArmoredSpw, basicSpw, fastSpw }, new int[] { 20, 20, 20, 80, 80, 80 });
		stages[16] = new Stage(new Spawner[] { heavilyArmoredSpw, heavilyArmoredSpw, armoredSpw, armoredSpw, armoredSpw, armoredSpw }, new int[] { 20, 20, 100, 100, 100, 100 });
		stages[17] = new Stage(new Spawner[] { heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw }, new int[] { 20, 20, 20, 20 });
		stages[18] = new Stage(new Spawner[] { fastSpw, fastSpw, fastSpw, fastSpw, heavilyArmoredSpw, heavilyArmoredSpw, basicSpw, basicSpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[19] = new Stage(new Spawner[] { heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, fastSpw, fastSpw }, new int[] { 20, 20, 20, 20, 60, 60 });
		stages[20] = new Stage(new Spawner[] { firstBossSpw }, new int[] { 60 });
		stages[21] = new Stage(new Spawner[] { fastSpw, fastSpw, armoredSpw, armoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[22] = new Stage(makeHomogenousSpawnerArray(basicSpw, 16), new int[] { 20, 20, 20, 20, 80, 80, 80, 80, 140, 140, 140, 140, 200, 200, 200, 200 });
		stages[23] = new Stage(new Spawner[] { heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, fastSpw, fastSpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80 });
		stages[24] = new Stage(new Spawner[] { fastSpw, fastSpw, armoredSpw, armoredSpw, fastSpw, fastSpw, armoredSpw, armoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80, 140, 140, 140, 140 });
		stages[25] = new Stage(new Spawner[] { speedySpw }, new int[] { 20 });
		stages[26] = new Stage(new Spawner[] { speedySpw, speedySpw, speedySpw, speedySpw }, new int[] { 20, 20, 20, 20 });
		stages[27] = new Stage(new Spawner[] { heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, speedySpw, speedySpw, speedySpw, speedySpw }, new int[] { 20, 20, 20, 20, 60, 60, 60, 60 });
		stages[28] = new Stage(new Spawner[] { basicSpw, basicSpw, armoredSpw, armoredSpw, heavilyArmoredSpw, heavilyArmoredSpw, speedySpw, speedySpw, heavilyArmoredSpw, heavilyArmoredSpw, speedySpw, speedySpw }, new int[] { 20, 20, 20, 20, 80, 80, 80, 80, 140, 140, 140, 140 });
		stages[29] = new Stage(new Spawner[] { speedySpw, speedySpw, speedySpw, speedySpw, speedySpw, speedySpw, speedySpw, speedySpw }, new int[] { 20, 20, 20, 20, 100, 100, 100, 100 });
		stages[30] = new Stage(new Spawner[] { secondBossSpw }, new int[] { 60 });
		stages[31] = new Stage(new Spawner[] { shootingSpw, shootingSpw }, new int[] { 30, 30 });
		stages[32] = new Stage(new Spawner[] { shootingSpw, shootingSpw, speedySpw, speedySpw }, new int[] { 30, 30, 30, 30 });
		stages[33] = new Stage(new Spawner[] { shootingSpw, shootingSpw, shootingSpw, shootingSpw }, new int[] { 30, 30, 30, 30 });
		stages[34] = new Stage(new Spawner[] { basicSpw, basicSpw, basicSpw, shootingSpw, speedySpw, shootingSpw, basicSpw, basicSpw }, new int[] { 30, 30, 30, 30, 70, 70, 70, 70 });
		stages[35] = new Stage(new Spawner[] { armoredSpw, armoredSpw, shootingSpw, shootingSpw, speedySpw, speedySpw, fastSpw, fastSpw }, new int[] { 30, 30, 30, 30, 70, 70, 70, 70 });
		stages[36] = new Stage(new Spawner[] { woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, speedySpw }, new int[] { 20, 20, 20, 20, 200 } );
		stages[37] = new Stage(new Spawner[] { woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, speedySpw, speedySpw, heavilyArmoredSpw, heavilyArmoredSpw }, new int[] { 20, 20, 20, 20, 200, 200, 200, 200 } );
		stages[38] = new Stage(new Spawner[] { woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, speedySpw, speedySpw, shootingSpw, shootingSpw }, new int[] { 20, 20, 20, 20, 200, 200, 250, 250 } );
		stages[39] = new Stage(new Spawner[] { woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, woodenBlockerSpw, speedySpw, speedySpw, shootingSpw, shootingSpw, fastSpw, fastSpw }, new int[] { 20, 20, 20, 20, 200, 200, 250, 250, 280, 280 } );
	}

	private static void initializeBarrels() {
		barrels = new Weapon[4];
		PropertyImplementation loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 15 }, new float[] { -0.2f }, 1);
		PropertyImplementation projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 20 }, new float[] { 0.5f }, 1);
		PropertyImplementation projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 20 }, new float[] { 0.5f }, 1);
		barrels[0] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed }, 0, "BasicBarrel.png", "BasicProjectile.png", true, "Basic Barrel", -1);

		loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 20, 30 }, new float[] { -0.13f, -0.05f }, 0.8f);
		projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 50 }, new float[] { 1 }, 1);
		projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 10, 25, 50 }, new float[] { 0.75f, 0.5f, 0.5f }, 1.75f);
		barrels[1] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed }, 50, "FastBarrel.png", "BasicProjectile.png", false, "Fast Projectile Barrel", 0);

		loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 10, 30, 40 }, new float[] { -0.5f, -0.2f, -0.1f }, 1.5f);
		projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 25, 45 }, new float[] { 1, 0.5f }, 2);
		projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 10, 25, 50 }, new float[] { 0.5f, 0.5f, 0.3f }, 0.7f);
		PropertyImplementation coinMagnet = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.coinMagnetID], new int[] { 50 }, new float[] { 1 }, 1);
		barrels[2] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, coinMagnet }, 75, "MagneticBarrel.png", "MagneticProjectile.png", false, "Magnetic Barrel", 4);

		loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 30, 55 }, new float[] { -0.15f, -0.1f }, 0.9f);
		projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 20, 60 }, new float[] { 1.5f, 1 }, 2);
		projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 25, 45, 65 }, new float[] { 1f, 0.8f, 0.7f }, 1f);
		coinMagnet = new NotUpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.coinMagnetID], 1);
		barrels[3] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, coinMagnet }, 150, "ElectroBarrel.png", "ElectricProjectile.png", false, "Electro Barrel", 8);
	}

	private static void initializeAutoweapons() {
		autoweapons = new Weapon[2];
		PropertyImplementation loadingTime = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], new int[] { 15 }, new float[] { -0.2f }, 1.5f);
		PropertyImplementation projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 20, 40 }, new float[] { 0.5f, 0.5f }, 0.7f);
		PropertyImplementation projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 20 }, new float[] { 0.5f }, 0.7f);
		PropertyImplementation rotationSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.rotationSpeedID], new int[] { 15, 30 }, new float[] { 0.3f, 0.2f }, 1f);
		autoweapons[0] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, rotationSpeed }, 50, "BasicAutoweapon.png", "BasicProjectile.png", false, "Basic autoweapon", 3);
		
		loadingTime = new NotUpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.loadingTimeID], 0.9f);
		projectilePower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectilePowerID], new int[] { 40 }, new float[] { 0.5f }, 1f);
		projectileSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.projectileSpeedID], new int[] { 45 }, new float[] { 0.8f }, 1.15f);
		rotationSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.rotationSpeedID], new int[] { 40, 55 }, new float[] { 0.5f, 0.35f }, 1.5f);
		PropertyImplementation unBlockSpeed = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.unblockSpeedID], new int[] { 40 }, new float[] { 0.35f }, 1.5f);
		autoweapons[1] = new Weapon(new PropertyImplementation[] { loadingTime, projectilePower, projectileSpeed, rotationSpeed, unBlockSpeed }, 100, "ProtectedAutoweapon.png", "BasicProjectile.png", false, "Protected autoweapon", 10);
	}
	
	private static void initializeCrushers() {
		crushers = new Crusher[1];
		PropertyImplementation crushPower = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.crushPowerID], new int[] { 40 }, new float[] { 0.5f }, 2);
		PropertyImplementation crushFrequency = new UpgradablePropertyImplementation(Weapon.propertiesIndex[Weapon.crushFrequencyID], new int[] { 50 }, new float[] { 0.5f }, 1);
		crushers[0] = new Crusher(new PropertyImplementation[] { crushPower, crushFrequency }, 120, "BasicCrusher.png", false, "Basic crusher", 11) {

			@Override
			public void draw(Graphics2D g, Point position, int size, float state) {
				g.setColor(Color.black);
				g.fillRect(position.x, position.y, size, size);
				g.setColor(Color.gray);
				g.fillRect(position.x + size / 16, position.y + size / 16, size - size / 8, size - size / 8);
				int secondBlackDist = size / 8 + size / 32;
				g.setColor(Color.black);
				g.fillRect(position.x + secondBlackDist, position.y + secondBlackDist, size - secondBlackDist * 2, size - secondBlackDist * 2);
				int redDist = secondBlackDist + size / 16;
				g.setColor(Color.red);
				g.fillRect(position.x + redDist, position.y + redDist, size - redDist * 2, size - redDist * 2);
				float actualState = (float) (1 - (Math.abs(state - 0.5) * 2));
				int sizeInside = size - 2 * redDist;
				g.setColor(Color.black);
				g.fillRect(position.x + redDist, position.y + redDist + sizeInside / 12, (int) (sizeInside * actualState), sizeInside / 6);
				g.fillRect(position.x + redDist + sizeInside - (int) (sizeInside * actualState), position.y + redDist + sizeInside / 3 + sizeInside / 12, (int) (sizeInside * actualState), sizeInside / 6);
				g.fillRect(position.x + redDist, position.y + redDist + sizeInside / 3 * 2 + sizeInside / 12, (int) (sizeInside * actualState), sizeInside / 6);
			}
			
		};
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
	
	public static Crusher getSelectedCrusher() {
		if (selectedCrusher.value == -1) {
			return null;
		}
		else {
			return crushers[selectedCrusher.value];
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
				Gui.gui.getContentPane().repaint();
			}
			Logging.log("The Game loop has been broken.");
		}

	}
}
