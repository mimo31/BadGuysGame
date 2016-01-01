package game.mechanics.barrels;

import game.Achievement;

public class Barrel {

	public static final BarrelGameProperty[] propertiesIndex = new BarrelGameProperty[] { new BarrelGameProperty("Loading time", 1), new BarrelGameProperty("Projectile power", 1), new BarrelGameProperty("Projectile speed", 1), new BarrelGameProperty("Coin magnet", 0) };
	public static final int loadingTimeID = getPropertyID("Loading time");
	public static final int projectilePowerID = getPropertyID("Projectile power");
	public static final int projectileSpeedID = getPropertyID("Projectile speed");
	public static final int coinMagnetID = getPropertyID("Coin magnet");
	
	public BarrelPropertyImplementation[] gameProperties;
	public final int cost;
	public boolean bought;
	public final String textureName;
	public final String projectileTextureName;
	public final String name;
	public final int achievementRequied;

	public Barrel(BarrelPropertyImplementation[] gameProperties, int cost, String textureName, String projectileTextureName, boolean bought, String name, int achievementRequied) {
		this.gameProperties = gameProperties;
		this.cost = cost;
		this.textureName = textureName;
		this.projectileTextureName = projectileTextureName;
		this.bought = bought;
		this.name = name;
		this.achievementRequied = achievementRequied;
	}

	public float getProperty(int id) {
		for (int i = 0; i < this.gameProperties.length; i++) {
			if (this.gameProperties[i].propertyType == propertiesIndex[id]) {
				return this.gameProperties[i].getActualValue();
			}
		}
		return propertiesIndex[id].defaultValue;
	}
	
	public boolean doDisplay() {
		if (achievementRequied == -1) {
			return true;
		}
		return Achievement.achievements[achievementRequied].achieved;
	}
	
	public static int getPropertyID(String name) {
		for (int i = 0; i < propertiesIndex.length; i++) {
			if (propertiesIndex[i].name.equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	private BarrelPropertyImplementation getPropertyImplementation(int id) {
		for (int i = 0; i < this.gameProperties.length; i++) {
			if (this.gameProperties[i].propertyType == propertiesIndex[id]) {
				return this.gameProperties[i];
			}
		}
		return null;
	}
	
	public boolean doesImplementProperty(int id) {
		if (this.getPropertyImplementation(id) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPropertyUpgradable(int id) throws PropertyNotImplementedException {
		BarrelPropertyImplementation propertyImplementation = this.getPropertyImplementation(id);
		if (propertyImplementation != null) {
			return propertyImplementation instanceof BarrelUpgradablePropertyImplementation;
		}
		else {
			throw new PropertyNotImplementedException();
		}
	}

	public void update(int time) {
		for (int i = 0; i < this.gameProperties.length; i++) {
			this.gameProperties[i].update(time);
		}
	}

	public void forceUpgrade() {
		for (int i = 0; i < this.gameProperties.length; i++) {
			this.gameProperties[i].endAll();
		}
	}
	
	public boolean isFullyUpgraded() {
		for (int i = 0; i < this.gameProperties.length; i++) {
			if (this.gameProperties[i] instanceof BarrelUpgradablePropertyImplementation) {
				if (!((BarrelUpgradablePropertyImplementation)this.gameProperties[i]).isFullyUpgraded()) {
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("serial")
	public static class PropertyNotImplementedException extends Exception {
		
	}
}
