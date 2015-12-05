package game.barrels;

import java.nio.ByteBuffer;

import game.PaintUtils;

public class BarrelUpgradablePropertyImplementation extends BarrelPropertyImplementation {

	int[] upgradeCosts;
	float[] upgradeValues;
	float actualValue;
	public int upgradeLevel;
	boolean isUpgrading;
	float upgradingProgress = 0;
	int upgradingBy = 0;

	public BarrelUpgradablePropertyImplementation(BarrelGameProperty propertyType, int[] upgradeCosts, float[] upgradeValues, float startValue) {
		super(propertyType);
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
			this.upgradingProgress = PaintUtils.shiftedArcsine(PaintUtils.shiftedSine(this.upgradingProgress) * this.upgradingBy / (this.upgradingBy + 1));
			this.upgradingBy++;
		}
		else {
			this.isUpgrading = true;
			this.upgradingBy = 1;
			this.upgradingProgress = 0;
		}
	}
	
	public float getUpgradedDrawnFraction() {
		float upgradeState;
		if (this.isUpgrading) {
			upgradeState = this.upgradeLevel + (PaintUtils.shiftedSine(this.upgradingProgress) - 1) * this.upgradingBy;
		}
		else {
			upgradeState = this.upgradeLevel;
		}
		return upgradeState / (float) upgradeValues.length;
	}
	
	@Override
	public void update() {
		if (this.isUpgrading) {
			this.upgradingProgress += 1 / (float) 25;
			if (upgradingProgress >= 1) {
				this.isUpgrading = false;
				this.upgradingBy = 0;
			}
		}
	}
	
	@Override
	public void endAll() {
		this.isUpgrading = false;
	}
	
	public float getMaxValue() {
		float sum = 0;
		for (int i = 0; i < this.upgradeValues.length; i++) {
			sum += this.upgradeValues[i];
		}
		return actualValue + sum;
	}
	
	public int getMaxCost() {
		int sum = 0;
		for (int i = 0; i < this.upgradeCosts.length; i++) {
			sum += this.upgradeCosts[i];
		}
		return sum;
	}

	@Override
	public byte[] getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(this.upgradeLevel);
		return buffer.array();
	}
	
	@Override
	public void loadFromBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.upgradeLevel = buffer.getInt();
		for (int i = 0; i < this.upgradeLevel; i++) {
			this.actualValue += this.upgradeValues[i];
		}
	}
	
	@Override
	public float getActualValue() {
		return this.actualValue;
	}

}
