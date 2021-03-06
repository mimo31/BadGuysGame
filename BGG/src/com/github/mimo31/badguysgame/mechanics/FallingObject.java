package com.github.mimo31.badguysgame.mechanics;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.github.mimo31.badguysgame.PaintUtils;
import com.github.mimo31.badguysgame.io.ResourceHandler;

public abstract class FallingObject {

	public int x;
	public float y;
	public final float totalLive;
	public float live;
	public final float speed;
	/*
	 * Represents the proportion of the size of the object and the width of the column.
	 */
	public final float size;
	public final String textureName;
	public final String name;
	public final boolean isGameEnding;
	public final String destroySoundName;

	public boolean isBeingHit;
	public float hitBy;
	public float hittingProgress;
	public float hitProgressStep;

	public boolean isDead;
	
	public FallingObject(float totalLive, float speed, float size, String textureName, String name, boolean isGameEnding, String destroySoundName) {
		this.totalLive = totalLive;
		this.live = totalLive;
		this.speed = speed;
		this.size = size;
		this.textureName = textureName;
		this.name = name;
		this.isGameEnding = isGameEnding;
		this.destroySoundName = destroySoundName;
	}

	public void hit(float hitPower) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		if (this.isBeingHit) {
			if (this.isDead) {
				this.hitProgressStep = 1 / (float) 32 / 40 / (this.live / (hitPower + this.live * this.hitProgressStep * 32 * 40));
			}
			else if (this.hitBy + hitPower >= this.live) {
				this.playDestroySound();
				this.hittingProgress = PaintUtils.shiftedArcsine(PaintUtils.shiftedSine(this.hittingProgress) * this.hitBy / this.live);
				this.hitProgressStep = 1 / (float) 32 / 40 / (this.live / (hitPower + this.hitBy));
				this.isDead = true;
				this.hitBy = this.live;
			}
			else {
				this.hittingProgress = PaintUtils.shiftedArcsine(PaintUtils.shiftedSine(this.hittingProgress) * this.hitBy / (this.hitBy + hitPower));
				this.hitBy = this.hitBy + hitPower;
				this.hitProgressStep = 1 / (float) 32 / 40 * (1 - this.hittingProgress);
			}
		}
		else {
			this.isBeingHit = true;
			this.hittingProgress = 0;
			if (hitPower >= this.live) {
				this.playDestroySound();
				this.isDead = true;
				this.hitBy = this.live;
				this.hitProgressStep = 1 / (float) 32 / 40 / (this.live / hitPower);
			}
			else {
				this.hitBy = hitPower;
				this.hitProgressStep = 1 / (float) 32 / 40;
			}
		}
	}
	
	private void playDestroySound() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		ResourceHandler.playSound(this.destroySoundName);
	}

	public float getShownLive() {
		if (this.isBeingHit) {
			return (this.live - PaintUtils.shiftedSine(hittingProgress) * hitBy) / this.totalLive;
		}
		else {
			return this.live / this.totalLive;
		}
	}

	public void move(int time) {
		this.y += this.speed * time / (float) 384 / (float) 40;
	}

	public abstract Coin getCoin();
}
