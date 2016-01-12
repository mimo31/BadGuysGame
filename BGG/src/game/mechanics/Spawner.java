package game.mechanics;

public interface Spawner {

	public BadGuy getBadGuy();

	public static class BasicSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(){
				
				@Override
				public Coin getCoin() {
					return Coin.basicCoin();
				}
				
			};
		}

	}

	public static class FastSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(1, 2, "FastBadGuy.png", "Fast", false) {

				@Override
				public Coin getCoin() {
					return Coin.coin2();
				}
				
			};
		}

	}

	public static class SpeedySpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(1, 4, "SpeedyBadGuy.png", "Speedy", false) {

				@Override
				public Coin getCoin() {
					return Coin.coin5();
				}
				
			};
		}

	}

	public static class ArmoredSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(4, 1, "ArmoredBadGuy.png", "Armored", false){
				
				@Override
				public Coin getCoin() {
					return Coin.coin2();
				}
				
			};
		}

	}
	
	public static class HeavyArmoredSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(8, 1, "HeavyArmoredBadGuy.png", "HeavyArmored", false){
				
				@Override
				public Coin getCoin() {
					return Coin.coin5();
				}
				
			};
		}

	}
	
	public static class FirstBossSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(64, 0.5f, "FirstBoss.png", "Boss1", true){
				
				@Override
				public Coin getCoin() {
					return Coin.coin10();
				}
				
			};
		}

	}
}
