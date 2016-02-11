package com.github.mimo31.badguysgame.mechanics;

public class ProjectileBlocker extends FallingObject {

	public ProjectileBlocker(float totalLive, float speed, float size, String textureName, String name) {
		super(totalLive, speed, size, textureName, name, false);
	}

	@Override
	public Coin getCoin() {
		return null;
	}

	public static class FullSizeProjectileBlocker extends ProjectileBlocker {

		public FullSizeProjectileBlocker(float totalLive, float speed, String textureName, String name) {
			super(totalLive, speed, 1, textureName, name);
		}
		
	}
	
}
