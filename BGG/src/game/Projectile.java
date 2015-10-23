package game;

public class Projectile {
	public float x;
	public float y;
	public float dirX;
	public float dirY;
	public float hitPower;
	
	public Projectile(float x, float y, float unscaledDirX, float unscaledDirY) {
		double factor = Math.sqrt(Math.pow(unscaledDirX, 2) + Math.pow(unscaledDirY, 2));
		this.dirX = (float)(unscaledDirX / factor);
		this.dirY = (float)(unscaledDirY / factor);
		this.x = x;
		this.y = y;
		this.hitPower = 1;
	}
}
