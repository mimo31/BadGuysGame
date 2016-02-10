package com.github.mimo31.badguysgame;

import java.util.List;

import com.github.mimo31.badguysgame.StringDraw.TextAlign;
import com.github.mimo31.badguysgame.io.ResourceHandler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Achievement {

	public static final Achievement[] achievements = new Achievement[11];
	public static AchievementConnection[] connections;
	public static final Rectangle achievementSpaceSize = new Rectangle();

	// New achievement displaying
	public static List<Achievement> achievementQueue = new ArrayList<Achievement>();
	public static Achievement nowDisplaying = null;
	public static int displayingState = 0;

	public final String name;
	/**
	 * Null corresponds to no texture.
	 */
	public final String textureName;
	public boolean achieved;
	/**
	 * Specifies the names of the items that are unlocked after this achievement
	 * is achieved.
	 */
	public final String[] unlockedItems;
	public final String task;
	public final boolean hidden;
	public final int x;
	public final int y;
	public final int[] dependencies;
	public final int[] requiedToShow;

	public Achievement(String name, String task, String textureName, String[] unlockedItems, boolean hidden, int x, int y, int[] dependencies, int[] requiedToShow) {
		this.name = name;
		this.task = task;
		this.textureName = textureName;
		this.unlockedItems = unlockedItems;
		this.hidden = hidden;
		this.x = x;
		this.y = y;
		this.dependencies = dependencies;
		this.requiedToShow = requiedToShow;
	}

	public static void initializeAchievements() {
		achievements[0] = new Achievement("That was easy", "Shoot down the first Bad Guy.", "BasicBadGuy.png", new String[] { "Fast Projectile Barrel" }, false, 0, 0, new int[0], new int[0]);
		achievements[1] = new Achievement("Money is useful", "Earn a coin.", "BasicCoin.png", new String[0], false, 768, 256, new int[] { 0 }, new int[0]);
		achievements[2] = new Achievement("Quick shot", "Shoot down the first Fast Bad Guy.", "FastBadGuy.png", new String[0], false, 768, -128, new int[] { 0 }, new int[0]);
		achievements[3] = new Achievement("BOSS", "Defeat a Boss.", "FirstBoss.png", new String[] { "Basic autoweapon" }, true, 1536, -128, new int[] { 2 }, new int[] { 0 });
		achievements[4] = new Achievement("Getting rich", "Earn 50 coins.", "2Coin.png", new String[] { "Magnetic Barrel" }, false, 1536, 128, new int[] { 1 }, new int[] { 0 });
		achievements[5] = new Achievement("A lot of money", "Earn 200 coins.", "5Coin.png", new String[0], false, 2304, 128, new int[] { 4 }, new int[] { 4 });
		achievements[6] = new Achievement("Power up", "Upgrade a barrel.", "Upgrade.png", new String[0], false, 1536, 384, new int[] { 1 }, new int[] { 0 });
		achievements[7] = new Achievement("A great gun", "Max all properties in one barrel.", "GreatBarrel.png", new String[0], false, 2304, 384, new int[] { 6 }, new int[] { 6 });
		achievements[8] = new Achievement("Speeding up", "Shot down the first Speedy Bad Guy.", "SpeedyBadGuy.png", new String[] { "Electro Barrel" }, true, 2304, -128, new int[] { 3 }, new int[] { 2 });
		achievements[9] = new Achievement("Becoming powerful", "Buy the Electro Barrel.", "ElectroBarrel.png", new String[0], false, 3072, 128, new int[] { 5, 8 }, new int[] { 8 });
		achievements[10] = new Achievement("Beware of projectiles", "Eliminate one projectile from a Bad Guy", "BasicBadProjectile.png", new String[] { "Protected autoweapon" }, false, 3072, -128, new int[] { 8 }, new int[0]);
		
		List<AchievementConnection> connectionList = new ArrayList<AchievementConnection>();
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(0, 1, 32, 0)));
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(0, 2, -32, 0)));
		addConnection(connectionList, 2, 3);
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(1, 4, -32, 0)));
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(1, 6, 32, 0)));
		addConnection(connectionList, 4, 5);
		addConnection(connectionList, 6, 7);
		addConnection(connectionList, 3, 8);
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(8, 9, 32, -32)));
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(5, 9, 32, 32)));
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(8, 10, -32, -32)));
		connections = connectionList.toArray(new AchievementConnection[connectionList.size()]);
	}
	
	public static void save(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(achievements.length);
		for (int i = 0; i < achievements.length; i++) {
			outputStream.writeBoolean(achievements[i].achieved);
		}
	}
	
	public static void load(DataInputStream inputStream) throws IOException {
		int saveAmount = inputStream.readInt();
		for (int i = 0; i < achievements.length && i < saveAmount; i++) {
			achievements[i].achieved = inputStream.readBoolean();
		}
	}

	public static void achieve(int index) {
		if (!achievements[index].achieved) {
			achievements[index].achieved = true;
			if (nowDisplaying == null) {
				nowDisplaying = achievements[index];
			}
			else {
				achievementQueue.add(achievements[index]);
			}
		}
	}

	public static void update(int time) {
		if (nowDisplaying != null) {
			displayingState += time;
		}
		if (displayingState >= 4500) {
			displayingState = 0;
			if (!achievementQueue.isEmpty()) {
				nowDisplaying = achievementQueue.get(0);
				achievementQueue.remove(0);
			}
			else {
				nowDisplaying = null;
			}
		}
	}

	public static void paint(Graphics2D g, Dimension contentSize) throws IOException {
		if (nowDisplaying != null) {
			Point rectanglePosition;
			if (displayingState < 1500) {
				rectanglePosition = new Point(contentSize.height * 3 / 16 * (displayingState - 1500) / 1500, contentSize.height * 15 / 16);
			}
			else if (displayingState < 3500) {
				rectanglePosition = new Point(0, contentSize.height * 15 / 16);
			}
			else {
				rectanglePosition = new Point(0, contentSize.height * 15 / 16 + (displayingState - 3500) * contentSize.height / 16 / 500);
			}
			nowDisplaying.paint(g, rectanglePosition, contentSize.height / 16, false);
			rectanglePosition.y -= contentSize.height / 16;
			g.setColor(Color.black);
			Rectangle achievementTextRectangle = new Rectangle(rectanglePosition.x, rectanglePosition.y, contentSize.height * 3 / 16, contentSize.height / 16);
			g.fill(achievementTextRectangle);
			g.setColor(Color.white);
			StringDraw.drawMaxString(g, contentSize.height / 64, "New achievement", achievementTextRectangle);
		}
	}

	private static void addConnection(List<AchievementConnection> connectionList, int indexFrom, int indexTo) {
		connectionList.addAll(Arrays.asList(AchievementConnection.getConnections(indexFrom, indexTo)));
	}

	public void paint(Graphics2D g, Point position, int height, boolean expanded) throws IOException {
		if (this.achieved) {
			g.setColor(Color.GREEN);
		}
		else {
			g.setColor(Color.gray);
		}
		g.fillRect(position.x, position.y, height * 3, height);
		if (this.achieved) {
			g.setColor(Color.red);
		}
		else {
			g.setColor(Color.white);
		}
		if (this.textureName == null) {
			StringDraw.drawMaxString(g, height / 8, this.name, StringDraw.TextAlign.LEFT, new Rectangle(position.x, position.y, height * 3, height));
		}
		else {
			g.drawImage(ResourceHandler.getTexture(this.textureName, height * 3 / 4), position.x + height / 8, position.y + height / 8, null);
			StringDraw.drawMaxString(g, height / 8, this.name, StringDraw.TextAlign.LEFT, new Rectangle(position.x + height, position.y, height * 2, height));
		}
		if (expanded) {
			Rectangle taskRect = new Rectangle(position.x, position.y + height, height * 3, height / 2);
			g.setColor(Color.black);
			g.fill(taskRect);
			g.setColor(Color.white);
			StringDraw.drawMaxString(g, height / 8, this.task, TextAlign.LEFT, taskRect);
			if (this.unlockedItems.length != 0) {
				Rectangle unlocksRect = new Rectangle(taskRect.x, (int) taskRect.getMaxY(), taskRect.width, taskRect.height);
				g.setColor(Color.black);
				g.fill(unlocksRect);
				g.setColor(Color.magenta);
				StringDraw.drawMaxString(g, height / 8, "Unlocks:", TextAlign.LEFT, unlocksRect);
				for (int i = 0; i < this.unlockedItems.length; i++) {
					Rectangle unlockedRect = new Rectangle(unlocksRect.x, (int) unlocksRect.getMaxY() + i * (height / 2), unlocksRect.width, unlocksRect.height);
					g.setColor(Color.black);
					g.fill(unlockedRect);
					g.setColor(Color.white);
					StringDraw.drawMaxString(g, height / 8, this.unlockedItems[i], TextAlign.LEFT, unlockedRect);
				}
			}
		}
	}

	public Rectangle getSpaceRectangle(boolean expanded) {
		int height = 128;
		if (expanded) {
			height += 64;
			if (this.unlockedItems.length != 0) {
				height += 64;
				height += 64 * this.unlockedItems.length;
			}
		}
		return new Rectangle(this.x - 192, this.y - 64, 384, height);
	}

	public boolean shouldShow() {
		if (this.achieved) {
			return true;
		}
		if (this.hidden) {
			return false;
		}
		for (int i = 0; i < this.dependencies.length; i++) {
			if (!achievements[this.dependencies[i]].shouldShow()) {
				return false;
			}
		}
		for (int i = 0; i < this.requiedToShow.length; i++) {
			if (!achievements[this.requiedToShow[i]].achieved) {
				return false;
			}
		}
		return true;
	}

	public static class AchievementConnection {

		public final int fromX;
		public final int fromY;
		public final int toX;
		public final int toY;
		public final int fromIndex;
		public final int toIndex;

		public static AchievementConnection[] getConnections(int fromIndex, int toIndex, int startAt, int endAt) {
			if (achievements[fromIndex].y + startAt == achievements[toIndex].y + endAt) {
				return new AchievementConnection[] { new AchievementConnection(achievements[fromIndex].x + 192, achievements[fromIndex].y + startAt, achievements[toIndex].x - 192, achievements[toIndex].y + startAt, fromIndex, toIndex) };
			}
			AchievementConnection[] array = new AchievementConnection[3];
			int xDistance = achievements[toIndex].x - achievements[fromIndex].x - 384;
			int yDistance = achievements[toIndex].y - achievements[fromIndex].y + endAt - startAt;
			array[0] = new AchievementConnection(achievements[fromIndex].x + 192, achievements[fromIndex].y + startAt, achievements[fromIndex].x + 192 + xDistance / 2, achievements[fromIndex].y + startAt, fromIndex, toIndex);
			array[1] = new AchievementConnection(array[0].toX, array[0].toY, array[0].toX, array[0].toY + yDistance, fromIndex, toIndex);
			array[2] = new AchievementConnection(array[1].toX, array[1].toY, achievements[toIndex].x - 192, array[1].toY, fromIndex, toIndex);
			return array;
		}

		public static AchievementConnection[] getConnections(int fromIndex, int toIndex) {
			return getConnections(fromIndex, toIndex, 0, 0);
		}

		public AchievementConnection(int fromX, int fromY, int toX, int toY, int fromIndex, int toIndex) {
			this.fromX = fromX;
			this.fromY = fromY;
			this.toX = toX;
			this.toY = toY;
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
		}

		public boolean doesIntersect(Rectangle rect) {
			if (rect.contains(new Point(this.fromX, this.fromY)) || rect.contains(new Point(this.toX, this.toY))) {
				return true;
			}
			boolean xMover = this.toX != this.fromX;
			if (xMover) {
				if (this.fromY < rect.getMaxY() && this.fromY >= rect.getMinY() && this.fromX < rect.getMinX() && this.toX > rect.getMaxX()) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				if (this.fromX < rect.getMaxX() && this.fromX >= rect.getMinX()) {
					boolean toIsBelow = this.toY > rect.getMaxY();
					boolean fromIsBelow = this.fromY > rect.getMaxY();
					return toIsBelow ^ fromIsBelow;
				}
				else {
					return false;
				}
			}
		}

		public boolean shouldShow() {
			return achievements[this.toIndex].shouldShow();
		}

		public boolean shouldBeFull() {
			return achievements[this.toIndex].achieved;
		}
	}
}
