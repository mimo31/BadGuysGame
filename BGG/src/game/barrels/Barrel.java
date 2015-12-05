package game.barrels;

public class Barrel {

	public static final BarrelGameProperty[] propertiesIndex = new BarrelGameProperty[] { new BarrelGameProperty("Loading time", 1), new BarrelGameProperty("Projectile power", 1), new BarrelGameProperty("Projectile speed", 1) };
	public static final int loadingTimeID = getPropertyID("Loading time");
	public static final int projectilePowerID = getPropertyID("Projectile power");
	public static final int projectileSpeedID = getPropertyID("Projectile speed");
	
	public BarrelPropertyImplementation[] gameProperties;
	public int cost;
	public boolean bought;
	public String textureName;
	public String projectileTextureName;
	public String name;

	public Barrel(BarrelPropertyImplementation[] gameProperties, int cost, String textureName, String projectileTextureName, boolean bought, String name) {
		this.gameProperties = gameProperties;
		this.cost = cost;
		this.textureName = textureName;
		this.projectileTextureName = projectileTextureName;
		this.bought = bought;
		this.name = name;
	}

	public float getProperty(int id) {
		for (int i = 0; i < this.gameProperties.length; i++) {
			if (this.gameProperties[i].propertyType == propertiesIndex[id]) {
				return this.gameProperties[i].getActualValue();
			}
		}
		return propertiesIndex[id].defaultValue;
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

	public void update() {
		for (int i = 0; i < this.gameProperties.length; i++) {
			this.gameProperties[i].update();
		}
	}

	public void forceUpgrade() {
		for (int i = 0; i < this.gameProperties.length; i++) {
			this.gameProperties[i].endAll();
		}
	}
	
	@SuppressWarnings("serial")
	public static class PropertyNotImplementedException extends Exception {
		
	}
}
