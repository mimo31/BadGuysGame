package game.mechanics.barrels;

public abstract class BarrelPropertyImplementation {
	
	public BarrelGameProperty propertyType;
	public abstract float getActualValue();

	public BarrelPropertyImplementation(BarrelGameProperty propertyType) {
		this.propertyType = propertyType;
	}
	
	public void loadFromBytes(byte[] bytes) {
	}
	
	public byte[] getBytes() {
		return new byte[0];
	}
	
	public void update(int time) {
	}
	
	public void endAll() {
	}
}
