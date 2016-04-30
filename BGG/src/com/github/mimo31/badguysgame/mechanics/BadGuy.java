package com.github.mimo31.badguysgame.mechanics;

public abstract class BadGuy extends FallingObject {
	
	public BadGuy(float totalLive, float speed, float size, String textureName, String name, String destroySoundName) {
		super(totalLive, speed, size, textureName, name, true, destroySoundName);
	}
	
	public BadGuy(float totalLive, float speed, float size, String textureName, String name) {
		super(totalLive, speed, size, textureName, name, true, "BadGuyKill.wav");
	}

	public static abstract class ClassicSizeBadGuy extends BadGuy {

		public ClassicSizeBadGuy(float totalLive, float speed, String textureName, String name) {
			super(totalLive, speed, 1 / (float) 4, textureName, name);
		}
		
	}
	
	public static abstract class BossSizeBadGuy extends BadGuy {
		
		public BossSizeBadGuy(float totalLive, float speed, String textureName, String name) {
			super(totalLive, speed, 1 / (float) 2, textureName, name);
		}
		
	}
	
}
