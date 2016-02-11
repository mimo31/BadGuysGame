package com.github.mimo31.badguysgame.mechanics;

public abstract class ShootingBadGuy extends BadGuy {

	public final float projectileSpeed;
	public final float projectilePower;
	public final float loadingTime;
	public final String projectileTextureName;
	public float loadState;
	
	protected ShootingBadGuy(float totalLive, float speed, String textureName, String name, float size, float projectileSpeed, float projectilePower, float loadingTime, String projectileTextureName) {
		super(totalLive, speed, size, textureName, name);
		this.projectileSpeed = projectileSpeed;
		this.projectilePower = projectilePower;
		this.loadingTime = loadingTime;
		this.projectileTextureName = projectileTextureName;
	}
	
}
