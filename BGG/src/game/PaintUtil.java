package game;

public class PaintUtil {
	
	public static float shiftedSine(float x) {
		return (float) (Math.sin(x * Math.PI - Math.PI / (double) 2) + 1) / (float) 2;
	}

	public static float shiftedArcsine(float x) {
		return (float) (Math.asin(x * 2 - 1) / (Math.PI / 2) + 1) / (float) 2;
	}
}
