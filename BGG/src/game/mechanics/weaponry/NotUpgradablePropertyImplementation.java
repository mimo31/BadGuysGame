package game.mechanics.weaponry;

public class NotUpgradablePropertyImplementation extends PropertyImplementation {

	private float value;
	
	public NotUpgradablePropertyImplementation(GameProperty propertyType, float value) {
		super(propertyType);
		this.value = value;
	}
	
	@Override
	public float getActualValue() {
		return this.value;
	}

}
