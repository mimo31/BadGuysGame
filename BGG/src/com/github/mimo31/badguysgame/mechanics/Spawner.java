package com.github.mimo31.badguysgame.mechanics;

import com.github.mimo31.badguysgame.mechanics.BadGuy.ClassicSizeBadGuy;
import com.github.mimo31.badguysgame.mechanics.BadGuy.BossSizeBadGuy;

public interface Spawner {

	public FallingObject getFallingObject();

	public static class BasicSpawner implements Spawner {

		@Override
		public FallingObject getFallingObject() {
			return new ClassicSizeBadGuy(1, 1, "BasicBadGuy.png", "Basic") {

				@Override
				public Coin getCoin() {
					return Coin.basicCoin();
				}

			};
		}

	}

	public static class FastSpawner implements Spawner {

		@Override
		public FallingObject getFallingObject() {
			return new ClassicSizeBadGuy(1, 2, "FastBadGuy.png", "Fast") {

				@Override
				public Coin getCoin() {
					return Coin.coin2();
				}

			};
		}

	}

	public static class SpeedySpawner implements Spawner {

		@Override
		public BadGuy getFallingObject() {
			return new ClassicSizeBadGuy(1, 4, "SpeedyBadGuy.png", "Speedy") {

				@Override
				public Coin getCoin() {
					return Coin.coin5();
				}

			};
		}

	}

	public static class ArmoredSpawner implements Spawner {

		@Override
		public BadGuy getFallingObject() {
			return new ClassicSizeBadGuy(4, 1, "ArmoredBadGuy.png", "Armored") {

				@Override
				public Coin getCoin() {
					return Coin.coin2();
				}

			};
		}

	}

	public static class HeavyArmoredSpawner implements Spawner {

		@Override
		public BadGuy getFallingObject() {
			return new ClassicSizeBadGuy(8, 1, "HeavyArmoredBadGuy.png", "HeavyArmored") {

				@Override
				public Coin getCoin() {
					return Coin.coin5();
				}

			};
		}

	}

	public static class FirstBossSpawner implements Spawner {

		@Override
		public BadGuy getFallingObject() {
			return new BossSizeBadGuy(64, 0.5f, "FirstBoss.png", "Boss1") {

				@Override
				public Coin getCoin() {
					return Coin.coin10();
				}

			};
		}

	}

	public static class SecondBossSpawner implements Spawner {

		@Override
		public BadGuy getFallingObject() {
			return new ShootingBadGuy(64, 1f, "SecondBoss.png", "Boss2", 1 / (float) 2, 1, 1, 1, "BasicBadProjectile.png") {

				@Override
				public Coin getCoin() {
					return Coin.coin15();
				}

			};
		}

	}
	
	public static class ShootingSpawner implements Spawner {

		@Override
		public BadGuy getFallingObject() {
			return new ShootingBadGuy(8, 2, "ShootingBadGuy.png", "Shooting", 1 / (float) 4, 1, 1, 1, "BasicBadProjectile.png") {
				
				@Override
				public Coin getCoin() {
					return Coin.coin10();
				}
			};
		}
		
	}
	
	public static class WoodenBlockSpawner implements Spawner {
		
		@Override
		public ProjectileBlocker getFallingObject() {
			return new ProjectileBlocker.FullSizeProjectileBlocker(16, 1, "WoodenBlocker.png", "WoodenBlocker");
		}
		
	}
}
