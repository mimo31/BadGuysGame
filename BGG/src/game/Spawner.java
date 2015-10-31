package game;

public interface Spawner {

	public BadGuy getBadGuy();

	public class BasicSpawner implements Spawner {

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

	public class FastSpawner implements Spawner {

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

	public class ArmoredSpawner implements Spawner {

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
}
