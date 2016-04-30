package com.github.mimo31.badguysgame.mechanics;

public abstract class ShootingBadGuy extends BadGuy {

	public final float projectileSpeed;
	public final float projectilePower;
	public final float loadingTime;
	public final String projectileTextureName;
	public float loadState;
	public final String shootSoundName;
	
	protected ShootingBadGuy(float totalLife, float speed, String textureName, String name, float size, float projectileSpeed, float projectilePower, float loadingTime, String projectileTextureName, String shootSoundName) {
		super(totalLife, speed, size, textureName, name);
		this.projectileSpeed = projectileSpeed;
		this.projectilePower = projectilePower;
		this.loadingTime = loadingTime;
		this.projectileTextureName = projectileTextureName;
		this.shootSoundName = shootSoundName;
	}
	
	protected ShootingBadGuy(float totalLife, float speed, String textureName, String name, float size, float projectileSpeed, float projectilePower, float loadingTime, String projectileTextureName) {
		this(totalLife, speed, textureName, name, size, projectileSpeed, projectilePower, loadingTime, projectileTextureName, "DarkShot.wav");
	}
	
}
