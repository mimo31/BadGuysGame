package game;

public class Projectile {
	public float x;
	public float y;
	public float dirX;
	public float dirY;
	public float hitPower;
	public String textureName;

	public Projectile(float x, float y, float unscaledDirX, float unscaledDirY, float speed, String textureName, float hitPower) {
		double factor = Math.sqrt(Math.pow(unscaledDirX, 2) + Math.pow(unscaledDirY, 2));
		this.dirX = (float) (unscaledDirX * speed / factor);
		this.dirY = (float) (unscaledDirY * speed / factor);
		this.x = x;
		this.y = y;
		this.hitPower = hitPower;
		this.textureName = textureName;
	}
}
