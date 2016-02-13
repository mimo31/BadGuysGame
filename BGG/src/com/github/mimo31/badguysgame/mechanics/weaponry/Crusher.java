package com.github.mimo31.badguysgame.mechanics.weaponry;

import java.awt.Graphics2D;
import java.awt.Point;

public abstract class Crusher extends Weapon {

	public Crusher(PropertyImplementation[] gameProperties, int cost, String textureName, boolean bought, String name, int achievementRequied) {
		super(gameProperties, cost, textureName, null, bought, name, achievementRequied);
	}

	public abstract void draw(Graphics2D g, Point position, int size, float state);
}
