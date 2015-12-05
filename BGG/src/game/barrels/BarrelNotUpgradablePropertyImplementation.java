package game.barrels;

public class BarrelNotUpgradablePropertyImplementation extends BarrelPropertyImplementation {

	private float value;
	
	public BarrelNotUpgradablePropertyImplementation(BarrelGameProperty propertyType, float value) {
		super(propertyType);
		this.value = value;
	}
	
	@Override
	public float getActualValue() {
		return this.value;
	}

}
