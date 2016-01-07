package game.mechanics.weaponry;

public abstract class PropertyImplementation {
	
	public GameProperty propertyType;
	public abstract float getActualValue();

	public PropertyImplementation(GameProperty propertyType) {
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
