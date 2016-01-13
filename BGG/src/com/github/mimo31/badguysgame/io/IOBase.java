package com.github.mimo31.badguysgame.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.mimo31.badguysgame.Achievement;
import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.Statistics;
import com.github.mimo31.badguysgame.mechanics.weaponry.Weapon;

public class IOBase {
	public static final Version version = new Version();
	public final static String rootDirectory = System.getProperty("user.dir") + "\\BadGuysGame";
	public final static String resourcesDirectory = rootDirectory + "\\Resources";
	public final static String serverRootDirectory = "http://mimo31.github.io/BadGuysGame";

	public static void loadSaveIfPresent() throws IOException {
		Logging.logStartSectionTag("GAMELOAD");
		Logging.log("Looking for a save.");
		if (Files.exists(Paths.get(rootDirectory + "\\Save.dat"))) {
			FileInputStream fileIn = new FileInputStream(new File(rootDirectory + "\\Save.dat"));
			DataInputStream dataIn = new DataInputStream(fileIn);
			Main.money = dataIn.readInt();
			Main.maxReachedStage = dataIn.readInt();
			Main.selectedBarrel.value = dataIn.readInt();
			Main.selectedAutoweapon.value = dataIn.readInt();
			Statistics.load(dataIn);
			Achievement.load(dataIn);
			int barrelsLength = dataIn.readInt();
			for (int i = 0; i < barrelsLength; i++) {
				Weapon currentBarrel = Main.barrels[i];
				currentBarrel.bought = dataIn.readBoolean();
				for (int j = 0; j < currentBarrel.gameProperties.length; j++) {
					byte[] data = new byte[dataIn.readInt()];
					dataIn.read(data, 0, data.length);
					currentBarrel.gameProperties[j].loadFromBytes(data);
				}
			}
			int autoweaponsLength = dataIn.readInt();
			for (int i = 0; i < autoweaponsLength; i++) {
				Weapon currentWeapon = Main.autoweapons[i];
				currentWeapon.bought = dataIn.readBoolean();
				for (int j = 0; j < currentWeapon.gameProperties.length; j++) {
					byte[] data = new byte[dataIn.readInt()];
					dataIn.read(data, 0, data.length);
					currentWeapon.gameProperties[j].loadFromBytes(data);
				}
			}
			fileIn.close();
			dataIn.close();
			Logging.log("The save was loaded.");
		}
		else {
			Main.firstRun = true;
		}
		Logging.logEndSectionTag("GAMELOAD");
	}

	public static void save() throws IOException {
		Logging.logStartSectionTag("GAMESAVE");
		FileOutputStream fileOut = new FileOutputStream(new File(rootDirectory + "\\Save.dat"));
		DataOutputStream dataOut = new DataOutputStream(fileOut);
		dataOut.writeInt(Main.money);
		dataOut.writeInt(Main.maxReachedStage);
		dataOut.writeInt(Main.selectedBarrel.value);
		dataOut.writeInt(Main.selectedAutoweapon.value);
		Statistics.save(dataOut);
		Achievement.save(dataOut);
		dataOut.writeInt(Main.barrels.length);
		for (int i = 0; i < Main.barrels.length; i++) {
			dataOut.writeBoolean(Main.barrels[i].bought);
			for (int j = 0; j < Main.barrels[i].gameProperties.length; j++) {
				byte[] propData = Main.barrels[i].gameProperties[j].getBytes();
				dataOut.writeInt(propData.length);
				dataOut.write(propData);
			}
		}
		dataOut.writeInt(Main.autoweapons.length);
		for (int i = 0; i < Main.autoweapons.length; i++) {
			dataOut.writeBoolean(Main.autoweapons[i].bought);
			for (int j = 0; j < Main.autoweapons[i].gameProperties.length; j++) {
				byte[] propData = Main.autoweapons[i].gameProperties[j].getBytes();
				dataOut.writeInt(propData.length);
				dataOut.write(propData);
			}
		}
		fileOut.close();
		dataOut.close();
		Logging.log("The Game was saved.");
		Logging.logEndSectionTag("GAMESAVE");
	}
}
