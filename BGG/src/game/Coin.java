package game;

public class Coin {
	
	/**
	 * Fraction of the width of the window representing the x location of the Coin.
	 * 
	 * Possible values from 0 to 1.
	 */
	public float x;
	/**
	 * Fraction of the height of the window representing the y location of the Coin.
	 * 
	 * Possible values from 0 to 1.
	 */
	public float y;
	public String textureName;
	public int value;
	
	public Coin(int value, String textureName) {
		this.value = value;
		this.textureName = textureName;
		this.x = 0;
		this.y = 0;
	}
	
	public static Coin basicCoin() {
		return new Coin(1, "BasicCoin.png");
	}
	
	public static Coin coin2() {
		return new Coin(2, "2Coin.png");
	}
	
	public static Coin coin5() {
		return new Coin(5, "5Coin.png");
	}
}
