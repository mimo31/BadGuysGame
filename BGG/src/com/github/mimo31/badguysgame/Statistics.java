package com.github.mimo31.badguysgame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Statistics {

	public static List<NumberByNameStatistic> badGuysKilledByType = new ArrayList<NumberByNameStatistic>();
	public static int totalBadGuysKilled = 0;
	public static int totalMoneyEarned = 0;
	
	public static void badGuyKilled(String name) {
		if (totalBadGuysKilled == 0) {
			Achievement.achieve(0);
		}
		totalBadGuysKilled++;
		boolean found = false;
		for (int i = 0; i < badGuysKilledByType.size(); i++) {
			if (badGuysKilledByType.get(i).name.equals(name)) {
				badGuysKilledByType.get(i).increaseNumber();
				found = true;
				break;
			}
		}
		if (!found) {
			badGuysKilledByType.add(new NumberByNameStatistic(name));
			if (name.equals("Fast")) {
				Achievement.achieve(2);
			}
			else if (name.equals("Boss1")) {
				Achievement.achieve(3);
			}
			else if (name.equals("Speedy")) {
				Achievement.achieve(8);
			}
		}
	}
	
	public static void save(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(badGuysKilledByType.size());
		for (int i = 0; i < badGuysKilledByType.size(); i++) {
			outputStream.writeUTF(badGuysKilledByType.get(i).name);
			outputStream.writeInt(badGuysKilledByType.get(i).number);
		}
		outputStream.writeInt(totalBadGuysKilled);
		outputStream.writeInt(totalMoneyEarned);
	}
	
	public static void load(DataInputStream inputStream) throws IOException {
		int dataLength = inputStream.readInt();
		for (int i = 0; i < dataLength; i++) {
			String name = inputStream.readUTF();
			int number = inputStream.readInt();
			badGuysKilledByType.add(new NumberByNameStatistic(name, number));
		}
		totalBadGuysKilled = inputStream.readInt();
		totalMoneyEarned = inputStream.readInt();
	}
	
	public static void moneyCollected(int amount) {
		if (totalMoneyEarned == 0) {
			Achievement.achieve(1);
		}
		if (totalMoneyEarned < 50 && totalMoneyEarned + amount >= 50) {
			Achievement.achieve(4);
		}
		if (totalMoneyEarned < 200 && totalMoneyEarned + amount >= 200) {
			Achievement.achieve(5);
		}
		totalMoneyEarned += amount;
	}

	public static class NumberByNameStatistic {
		
		public int number;
		public String name;
		
		public NumberByNameStatistic(String name) {
			this.name = name;
			this.number = 1;
		}
		
		public NumberByNameStatistic(String name, int number) {
			this.name = name;
			this.number = number;
		}
		
		public void increaseNumber() {
			this.number++;
		}
	}
}


