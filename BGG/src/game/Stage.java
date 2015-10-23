package game;

public class Stage {
	
	public int[] spawnTimes;
	
	public Stage(int[] spawnTimes){
		this.spawnTimes = spawnTimes;
	}
	
	public boolean allSpawned(int stageTime) {
		for (int i = 0; i < spawnTimes.length; i++) {
			if (spawnTimes[i] > stageTime) {
				return false;
			}
		}
		return true;
	}
}
