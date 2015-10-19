package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
*Handles IO for the whole program, such as loading textures from the file system
*/
public final class IO {
	
	private static ArrayList<Texture> originalTextures = new ArrayList<Texture>();
	private static ArrayList<Texture> scaledTextures = new ArrayList<Texture>();
	public final static String rootDirectory = "D:\\Viktor\\Programming\\BGG";
	
	/**Returns a scaled image from the rootDirectory.
	 * @param name The file name in the root directory.
	 * @param width The width of the returned image.
	 * */
	public static BufferedImage getTexture(String name, int width) throws IOException {
		boolean found = false;
		Texture originalTexture = null;
		//Looks into originalTextures, sets originalTexture to one of the entries of originalTextures if there is a corresponding one
		//returns the texture if the original texture found has the requested width
		for (int i = 0; i < originalTextures.size(); i++) {
			Texture currentTexture = originalTextures.get(i);
			if (currentTexture.name.equals(name)) {
				if (currentTexture.image.getWidth() == width){
					return currentTexture.image;
				}
				else {
					found = true;
					originalTexture = currentTexture;
				}
			}
		}
		//If the original texture was found (but with wrong width), then this looks into scaledTextures for the corresponding texture
		//If it finds the texture, it scales it to the requested width (if necessary) and returns the scaled texture
		if (found) {
			for (int i = 0; i < scaledTextures.size(); i++) {
				Texture currentTexture = scaledTextures.get(i);
				if (currentTexture.name.equals(name)) {
					if (currentTexture.image.getWidth() != width) {
						currentTexture.image = scaleImage(originalTexture.image, width / (float)originalTexture.image.getWidth());
					}
					return currentTexture.image;
				}
			}
		}
		//If the original is not in the originalTextures, then it loads the texture form the file system and adds it to originalTextures
		if (!found) {
			String imagePath = rootDirectory + "\\" + name;
			if (Files.exists(Paths.get(imagePath))) {
				originalTexture = new Texture(name, ImageIO.read(new File(rootDirectory + "\\" + name)));
				originalTextures.add(originalTexture);
				if (originalTexture.image.getWidth() == width) {
					return originalTexture.image;
				}
			}
			else{
				throw new IOException("There is no file called \"" + name + "\" in the root directory.");
			}
		}
		//Takes the original, scales it to the requested width, and adds it to scaledTextures
		Texture scaledTexture = new Texture(name, scaleImage(originalTexture.image, width / (float)originalTexture.image.getWidth()));
		scaledTextures.add(scaledTexture);
		return scaledTexture.image;
	}
	
	/**Scales the image by the scalingFactor*/
	public static BufferedImage scaleImage(BufferedImage image, float scalingFactor) {
		BufferedImage scaledImage = new BufferedImage((int)(image.getWidth() * scalingFactor), (int)(image.getHeight() * scalingFactor), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) scaledImage.getGraphics();
		g.drawImage(image, 0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null);
		return scaledImage;
	}
	
	/**Represents a texture - a BufferedImage and a string name*/
	private static class Texture{
		String name;
		BufferedImage image;
		
		public Texture(String name, BufferedImage image){
			this.name = name;
			this.image = image;
		}
	}
}
