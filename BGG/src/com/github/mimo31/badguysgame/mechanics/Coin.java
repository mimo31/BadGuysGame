package com.github.mimo31.badguysgame.mechanics;

import java.awt.Dimension;

public class Coin {
	
	/**
	 * Fraction of the width of the window representing the x location of the Coin.
	 * 
	 * Possible values from 0 to 1.
	 */
	public float x;
	/**
	 * Fraction of the height of the window representing the y location of the Coin.
	 * 
	 * Possible values from 0 to 1.
	 */
	public float y;
	public String textureName;
	public int value;
	
	public Coin(int value, String textureName) {
		this.value = value;
		this.textureName = textureName;
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * @return Whether the coin is certainly collected by the source of the magnetic force.
	 */
	public boolean attractTo(float x, float y, float strength, int time) {
		double distInX = x - this.x;
		double distInY = y - this.y;
		double totalMove = (strength / (Math.pow(distInX, 2) + Math.pow(distInY, 2))) * time / (double) 65536 / (float) 40;
		if (totalMove > Math.sqrt(Math.pow(distInX, 2) + Math.pow(distInY, 2))) {
			return true;
		}
		else if (totalMove < 1 / (float) 4096) {
			return false;
		}
		else {
			double yXratio = distInY / distInX;
			double xAdded = totalMove / Math.sqrt(1 + Math.pow(yXratio, 2)) * Math.signum(distInX);
			double yAdded = totalMove * yXratio / Math.sqrt(1 + Math.pow(yXratio, 2)) * Math.signum(distInX);
			this.x += xAdded;
			this.y += yAdded;
			return false;
		}
	}
	
	public void resolveEdgeCollisions(Dimension contentSize) {
		if (this.x < 1 / (float) 128) {
			this.x = 1 / (float) 64 - this.x;
		} 
		else if (this.x > 127 / (float) 128) {
			this.x = 127 / (float) 64 - this.x;
		}
		float upEdge = contentSize.width / (float) 128 / (float) contentSize.height;
		float downEdge = 1 - contentSize.width / 128 / contentSize.height;
		if (this.y < upEdge) {
			this.y = upEdge * 2 - this.y;
		}
		else if (this.y > downEdge){
			this.y = downEdge * 2 - this.y;
		}
	}
	
	public static Coin basicCoin() {
		return new Coin(1, "BasicCoin.png");
	}
	
	public static Coin coin2() {
		return new Coin(2, "2Coin.png");
	}
	
	public static Coin coin5() {
		return new Coin(5, "5Coin.png");
	}
	
	public static Coin coin10() {
		return new Coin(10, "10Coin.png");
	}
	
	public static Coin coin15() {
		return new Coin(15, "15Coin.png");
	}
}
