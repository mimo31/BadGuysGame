package game;

public class Stage {
	
	public Spawner[] spawners;
	
	public Stage(Spawner[] spawnTimes){
		this.spawners = spawnTimes;
	}
	
	public boolean allSpawned(int stageTime) {
		for (int i = 0; i < spawners.length; i++) {
			if (spawners[i].getSpawnTime() > stageTime) {
				return false;
			}
		}
		return true;
	}
}