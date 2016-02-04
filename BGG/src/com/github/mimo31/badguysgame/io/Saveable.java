package com.github.mimo31.badguysgame.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.mimo31.badguysgame.Achievement;
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
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			dataOut.writeInt(Main.selectedBarrel.value);
			dataOut.writeInt(Main.barrels.length);
			for (int i = 0; i < Main.barrels.length; i++) {
				dataOut.writeBoolean(Main.barrels[i].bought);
				for (int j = 0; j < Main.barrels[i].gameProperties.length; j++) {
					byte[] propData = Main.barrels[i].gameProperties[j].getBytes();
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

		@Override
		public void load(byte[] data) throws IOException {
			DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
			Main.selectedBarrel.value = dataIn.readInt();
			int barrelsLength = dataIn.readInt();
			for (int i = 0; i < barrelsLength; i++) {
				Weapon currentBarrel = Main.barrels[i];
				currentBarrel.bought = dataIn.readBoolean();
				for (int j = 0; j < currentBarrel.gameProperties.length; j++) {
					byte[] propertyData = new byte[dataIn.readInt()];
					dataIn.read(propertyData, 0, propertyData.length);
					currentBarrel.gameProperties[j].loadFromBytes(propertyData);
				}
			}
			dataIn.close();
		}
		
	};
	
	public static Saveable autoweaponsIO = new Saveable("autoweapons") {
		
		@Override
		public byte[] save() throws IOException {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			dataOut.writeInt(Main.selectedAutoweapon.value);
			dataOut.writeInt(Main.autoweapons.length);
			for (int i = 0; i < Main.autoweapons.length; i++) {
				dataOut.writeBoolean(Main.autoweapons[i].bought);
				for (int j = 0; j < Main.autoweapons[i].gameProperties.length; j++) {
					byte[] propData = Main.autoweapons[i].gameProperties[j].getBytes();
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

		@Override
		public void load(byte[] data) throws IOException {
			DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
			Main.selectedAutoweapon.value = dataIn.readInt();
			int autoweaponsLength = dataIn.readInt();
			for (int i = 0; i < autoweaponsLength; i++) {
				Weapon currentWeapon = Main.autoweapons[i];
				currentWeapon.bought = dataIn.readBoolean();
				for (int j = 0; j < currentWeapon.gameProperties.length; j++) {
					byte[] propertyData = new byte[dataIn.readInt()];
					dataIn.read(propertyData, 0, propertyData.length);
					currentWeapon.gameProperties[j].loadFromBytes(propertyData);
				}
			}
			dataIn.close();
		}
		
	};
	
	public static Saveable[] saveables = new Saveable[] { baseIO, statisticsIO, achievementsIO, barrelsIO, autoweaponsIO };
}
