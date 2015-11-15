package game;

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
			return new BadGuy(1, 2, "FastBadGuy.png") {

				@Override
				public Coin getCoin() {
					return Coin.coin2();
				}
				
			};
		}

	}

	public static class ArmoredSpawner implements Spawner {

		@Override
		public BadGuy getBadGuy() {
			return new BadGuy(4, 1, "ArmoredBadGuy.png"){
				
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
			return new BadGuy(8, 1, "HeavyArmoredBadGuy.png"){
				
				@Override
				public Coin getCoin() {
					return Coin.coin5();
				}
				
			};
		}

	}
}
