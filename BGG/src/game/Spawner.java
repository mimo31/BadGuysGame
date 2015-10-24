package game;

public interface Spawner {

	public int getSpawnTime();

	public BadGuy getBadGuy();

	public class BasicSpawner implements Spawner {
		public int spawnTime;

		public BasicSpawner(int spawnTime) {
			this.spawnTime = spawnTime;
		}

		@Override
		public int getSpawnTime() {
			return spawnTime;
		}

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy();
		}

	}

	public class FastSpawner implements Spawner {
		public int spawnTime;

		public FastSpawner(int spawnTime) {
			this.spawnTime = spawnTime;
		}

		@Override
		public int getSpawnTime() {
			return spawnTime;
		}

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(1, 2, "FastBadGuy.png");
		}

	}

	public class ArmoredSpawner implements Spawner {
		public int spawnTime;

		public ArmoredSpawner(int spawnTime) {
			this.spawnTime = spawnTime;
		}

		@Override
		public int getSpawnTime() {
			return spawnTime;
		}

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(4, 1, "ArmoredBadGuy.png");
		}

	}
}
