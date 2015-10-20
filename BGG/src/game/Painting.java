package game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.IOException;

public final class Painting {
	
	public static void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException{
		if (!Main.inStartScreen) {
			for (int i = 0; i < Main.badGuys.size(); i++) {
				BadGuy currentBadGuy = Main.badGuys.get(i);
				g.drawImage(IO.getTexture("BadGuy.png", contentSize.width / 16), (int)((currentBadGuy.x / (float)4 + 1 / (float)8 - 1 / (float)32) * contentSize.width), (int)(currentBadGuy.y * contentSize.height - contentSize.width / 16), null);
			}
			g.drawImage(IO.getTexture("Base.png", contentSize.width / 8), contentSize.width / 2 - contentSize.width / 16, contentSize.height - contentSize.width / 16, null);
			Point barrelCenter = new Point(contentSize.width / 2, contentSize.height - contentSize.width / 16);
			AffineTransform transform = AffineTransform.getTranslateInstance(barrelCenter.x, barrelCenter.y);
			double vecX = -barrelCenter.x + mousePosition.x;
			double vecY = -mousePosition.y + barrelCenter.y;
			transform.rotate(vecY, vecX);
			transform.translate(-contentSize.width / 32, -contentSize.width / 32);
			g.drawImage(IO.getTexture("Barrel.png", contentSize.width / 16), transform, null);
			for (int i = 0; i < Main.bullets.size(); i++) {
				Bullet currentBullet = Main.bullets.get(i);
				g.drawImage(IO.getTexture("Bullet.png", contentSize.width / 64), (int)(currentBullet.x * contentSize.width - contentSize.width / (float)128), (int)(currentBullet.y * contentSize.height - contentSize.width / (float)128), null);
			}
		}
	}
	
}
