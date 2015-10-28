package game;

public interface Spawner {

	public BadGuy getBadGuy();

	public class BasicSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy();
		}

	}

	public class FastSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(1, 2, "FastBadGuy.png");
		}

	}

	public class ArmoredSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(4, 1, "ArmoredBadGuy.png");
		}

	}
}
