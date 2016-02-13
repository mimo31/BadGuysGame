package com.github.mimo31.badguysgame.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.mimo31.badguysgame.Main;

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
			int numberOfSaveables = dataIn.readInt();
			String[] codeNames = new String[numberOfSaveables];
			int[] lengths = new int[numberOfSaveables];
			for (int i = 0; i < numberOfSaveables; i++) {
				codeNames[i] = dataIn.readUTF();
				lengths[i] = dataIn.readInt();
			}
			for (int i = 0; i < codeNames.length; i++) {
				int saveablesIndex = -1;
				for (int j = 0; j < Saveable.saveables.length; j++) {
					if (Saveable.saveables[j].codeName.equals(codeNames[i])) {
						saveablesIndex = j;
					}
				}
				if (saveablesIndex == -1) {
					dataIn.skipBytes(lengths[i]);
				}
				else {
					byte[] readData = new byte[lengths[i]];
					dataIn.read(readData, 0, readData.length);
					Saveable.saveables[saveablesIndex].load(readData);
					Logging.log("Loading " + Saveable.saveables[saveablesIndex].codeName + ".");
				}
			}
			fileIn.close();
			dataIn.close();
			Logging.log("The save has been loaded.");
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
		dataOut.writeInt(Saveable.saveables.length);
		byte[][] dataToSave = new byte[Saveable.saveables.length][];
		for (int i = 0; i < dataToSave.length; i++) {
			dataToSave[i] = Saveable.saveables[i].save();
			dataOut.writeUTF(Saveable.saveables[i].codeName);
			dataOut.writeInt(dataToSave[i].length);
		}
		for (int i = 0; i < dataToSave.length; i++) {
			dataOut.write(dataToSave[i]);
			Logging.log("Saving " + Saveable.saveables[i].codeName + ".");
		}
		fileOut.close();
		dataOut.close();
		Logging.log("The Game has been saved.");
		Logging.logEndSectionTag("GAMESAVE");
	}
}
