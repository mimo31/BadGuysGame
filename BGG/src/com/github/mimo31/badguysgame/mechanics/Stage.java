package com.github.mimo31.badguysgame.mechanics;

public class Stage {

	public Spawner[] spawners;
	public int[] spawnTimes;

	public Stage(Spawner[] spawners, int[] spawnTimes) {
		this.spawners = spawners;
		this.spawnTimes = spawnTimes;
	}

	public boolean allSpawned(float stageTime) {
		for (int i = 0; i < spawners.length; i++) {
			if (spawnTimes[i] > stageTime) {
				return false;
			}
		}
		return true;
	}
}