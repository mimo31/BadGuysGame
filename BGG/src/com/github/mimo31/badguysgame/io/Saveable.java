package com.github.mimo31.badguysgame.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.mimo31.badguysgame.Achievement;
import com.github.mimo31.badguysgame.IntHolder;
import com.github.mimo31.badguysgame.Main;
import com.github.mimo31.badguysgame.Statistics;
import com.github.mimo31.badguysgame.mechanics.weaponry.Weapon;

public abstract class Saveable {
	
	public abstract byte[] save() throws IOException;
	public abstract void load(byte[] data) throws IOException;
	public final String codeName;
	
	private Saveable(String codeName) {
		this.codeName = codeName;
	}
	
	public static Saveable baseIO = new Saveable("base") {

		@Override
		public byte[] save() throws IOException {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			dataOut.writeInt(Main.money);
			dataOut.writeInt(Main.maxReachedStage);
			dataOut.writeInt(Main.selectedBarrel.value);
			dataOut.writeInt(Main.selectedAutoweapon.value);
			byte[] data = byteOut.toByteArray();
			dataOut.flush();
			dataOut.close();
			byteOut.close();
			return data;
		}

		@Override
		public void load(byte[] data) throws IOException {
			if (data.length == 16) {
				DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
				Main.money = dataIn.readInt();
				Main.maxReachedStage = dataIn.readInt();
				Main.selectedAutoweapon.value = dataIn.readInt();
				dataIn.close();
			}
		}
		
	};
	
	public static Saveable statisticsIO = new Saveable("statistics") {
		
		@Override
		public byte[] save() throws IOException {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			Statistics.save(dataOut);
			byte[] data = byteOut.toByteArray();
			dataOut.flush();
			dataOut.close();
			byteOut.close();
			return data;
		}

		@Override
		public void load(byte[] data) throws IOException {
			DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
			Statistics.load(dataIn);
			dataIn.close();
		}
		
	};
	
	public static Saveable achievementsIO = new Saveable("achievements") {
		
		@Override
		public byte[] save() throws IOException {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			Achievement.save(dataOut);
			byte[] data = byteOut.toByteArray();
			dataOut.flush();
			dataOut.close();
			byteOut.close();
			return data;
		}

		@Override
		public void load(byte[] data) throws IOException {
			DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
			Achievement.load(dataIn);
			dataIn.close();
		}
		
	};
	
	public static Saveable barrelsIO = new Saveable("barrels") {
		
		@Override
		public byte[] save() throws IOException {
			return saveWeaponArray(Main.barrels, Main.selectedBarrel);
		}

		@Override
		public void load(byte[] data) throws IOException {
			loadWeaponArray(Main.barrels, Main.selectedBarrel, data);
		}
		
	};

	
	public static Saveable autoweaponsIO = new Saveable("autoweapons") {
		
		@Override
		public byte[] save() throws IOException {
			return saveWeaponArray(Main.autoweapons, Main.selectedAutoweapon);
		}

		@Override
		public void load(byte[] data) throws IOException {
			loadWeaponArray(Main.autoweapons, Main.selectedAutoweapon, data);
		}
		
	};

	
	public static Saveable crushersIO = new Saveable("crushers") {
		
		@Override
		public byte[] save() throws IOException {
			return saveWeaponArray(Main.crushers, Main.selectedCrusher);
		}

		@Override
		public void load(byte[] data) throws IOException {
			loadWeaponArray(Main.crushers, Main.selectedCrusher, data);
		}
		
	};
	
	private static byte[] saveWeaponArray(Weapon[] weapons, IntHolder selectedWeapon) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		dataOut.writeInt(selectedWeapon.value);
		dataOut.writeInt(weapons.length);
		for (int i = 0; i < weapons.length; i++) {
			dataOut.writeBoolean(weapons[i].bought);
			for (int j = 0; j < weapons[i].gameProperties.length; j++) {
				byte[] propData = weapons[i].gameProperties[j].getBytes();
				dataOut.writeInt(propData.length);
				dataOut.write(propData);
			}
		}
		byte[] data = byteOut.toByteArray();
		dataOut.flush();
		dataOut.close();
		byteOut.close();
		return data;
	}
	
	private static void loadWeaponArray(Weapon[] weapon, IntHolder selectedWeapon, byte[] data) throws IOException {
		DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
		selectedWeapon.value = dataIn.readInt();
		int weaponsLength = dataIn.readInt();
		for (int i = 0; i < weaponsLength; i++) {
			Weapon currentWeapon = weapon[i];
			currentWeapon.bought = dataIn.readBoolean();
			for (int j = 0; j < currentWeapon.gameProperties.length; j++) {
				byte[] propertyData = new byte[dataIn.readInt()];
				dataIn.read(propertyData, 0, propertyData.length);
				currentWeapon.gameProperties[j].loadFromBytes(propertyData);
			}
		}
		dataIn.close();
	}
	
	public static Saveable[] saveables = new Saveable[] { baseIO, statisticsIO, achievementsIO, barrelsIO, autoweaponsIO, crushersIO };
}
