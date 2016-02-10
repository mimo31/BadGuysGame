package com.github.mimo31.badguysgame.mechanics;

public class Projectile {
	
	public float x;
	public float y;
	public float dirX;
	public float dirY;
	public final float hitPower;
	public final String textureName;
	public final float coinMagnet;
	public final boolean isFromBadGuy;

	public Projectile(float x, float y, float unscaledDirX, float unscaledDirY, float speed, String textureName, float hitPower, float coinMagnet, boolean isFromBadGuy) {
		double factor = Math.sqrt(Math.pow(unscaledDirX, 2) + Math.pow(unscaledDirY, 2));
		this.dirX = (float) (unscaledDirX * speed / factor);
		this.dirY = (float) (unscaledDirY * speed / factor);
		this.x = x;
		this.y = y;
		this.hitPower = hitPower;
		this.textureName = textureName;
		this.coinMagnet = coinMagnet;
		this.isFromBadGuy = isFromBadGuy;
	}
}
