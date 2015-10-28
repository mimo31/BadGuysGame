package game;

public class Barrel {

	public BarrelGameProperty[] gameProperties;
	public int cost;
	public boolean bought;
	public String textureName;
	public String projectileTextureName;
	public String name;

	public Barrel(BarrelGameProperty[] gameProperties, int cost, String textureName, String projectileTextureName, boolean bought, String name) {
		this.gameProperties = gameProperties;
		this.cost = cost;
		this.textureName = textureName;
		this.projectileTextureName = projectileTextureName;
		this.bought = bought;
		this.name = name;
	}

	public float getProjectileSpeed() {
		return this.gameProperties[2].actualValue;
	}

	public float getProjectilePower() {
		return this.gameProperties[1].actualValue;
	}

	public float getLoadingSpeed() {
		return this.gameProperties[0].actualValue;
	}

	static class BarrelGameProperty {
		int[] upgradeCosts;
		float[] upgradeValues;
		float actualValue;
		int upgradeLevel;

		public BarrelGameProperty(int[] upgradeCosts, float[] upgradeValues, float startValue) {
			this.upgradeCosts = upgradeCosts;
			this.upgradeValues = upgradeValues;
			this.actualValue = startValue;
			this.upgradeLevel = 0;
		}
	}
}
