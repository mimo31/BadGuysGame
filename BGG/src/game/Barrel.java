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
	
	public void update() {
		for (int i = 0; i < 3; i++) {
			this.gameProperties[i].update();
		}
	}

	static class BarrelGameProperty {
		int[] upgradeCosts;
		float[] upgradeValues;
		float actualValue;
		int upgradeLevel;
		boolean isUpgrading;
		float upgradingProgress = 0;

		public BarrelGameProperty(int[] upgradeCosts, float[] upgradeValues, float startValue) {
			this.upgradeCosts = upgradeCosts;
			this.upgradeValues = upgradeValues;
			this.actualValue = startValue;
			this.upgradeLevel = 0;
			this.isUpgrading = false;
		}
		
		public boolean isFullyUpgraded() {
			if (upgradeLevel == upgradeValues.length) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public float getUpgradeValue() {
			return upgradeValues[upgradeLevel];
		}
		
		public int getUpgradeCost() {
			return upgradeCosts[upgradeLevel];
		}
		
		public void upgrade() {
			this.actualValue += this.getUpgradeValue();
			this.upgradeLevel++;
			if (this.isUpgrading) {
				this.upgradingProgress = PaintUtil.shiftedArcsine(PaintUtil.shiftedSine(this.upgradingProgress) / 2);
			}
			else {
				this.isUpgrading = true;
				this.upgradingProgress = 0;
			}
		}
		
		public float getUpgradedDrawnFraction() {
			return (upgradeLevel + PaintUtil.shiftedSine(this.upgradingProgress) - 1) / (float) upgradeValues.length;
		}
		
		public void update() {
			if (this.isUpgrading) {
				this.upgradingProgress += 1 / (float) 25;
				if (upgradingProgress >= 1) {
					this.isUpgrading = false;
				}
			}
		}
	}
}
