package game.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import game.Main;
import game.barrels.Barrel;

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
			Main.selectedBarrel = dataIn.readInt();
			int index = 0;
			while (dataIn.available() != 0 && index < Main.barrels.length) {
				Barrel currentBarrel = Main.barrels[index];
				currentBarrel.bought = dataIn.readBoolean();
				for (int i = 0; i < currentBarrel.gameProperties.length; i++) {
					byte[] data = new byte[dataIn.readInt()];
					dataIn.read(data, 0, data.length);
					currentBarrel.gameProperties[i].loadFromBytes(data);
				}
				index++;
			}
			fileIn.close();
			dataIn.close();
			Logging.log("The save was loaded.");
		}
		Logging.logEndSectionTag("GAMELOAD");
	}

	public static void save() throws IOException {
		Logging.logStartSectionTag("GAMESAVE");
		FileOutputStream fileOut = new FileOutputStream(new File(rootDirectory + "\\Save.dat"));
		DataOutputStream dataOut = new DataOutputStream(fileOut);
		dataOut.writeInt(Main.money);
		dataOut.writeInt(Main.maxReachedStage);
		dataOut.writeInt(Main.selectedBarrel);
		for (int i = 0; i < Main.barrels.length; i++) {
			dataOut.writeBoolean(Main.barrels[i].bought);
			for (int j = 0; j < Main.barrels[i].gameProperties.length; j++) {
				byte[] propData = Main.barrels[i].gameProperties[j].getBytes();
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
