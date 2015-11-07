package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import game.Version.VersionStringFormatException;

/**
 * Handles IO for the whole program, such as loading textures from the file
 * system
 */
public final class IO {

	private static ArrayList<Texture> originalTextures = new ArrayList<Texture>();
	private static ArrayList<Texture> scaledTextures = new ArrayList<Texture>();
	public final static String rootDirectory = System.getProperty("user.dir") + "\\BadGuysGame";
	public final static String resourcesDirectory = rootDirectory + "\\Resources";
	public final static String serverRootDirectory = "http://178.248.252.60/~xfukv01/BGG";
	public static final Version version = new Version();

	/**
	 * Returns a scaled image from the rootDirectory.
	 * 
	 * @param name
	 *            The file name in the root directory.
	 * @param width
	 *            The width of the returned image.
	 */
	public static BufferedImage getTexture(String name, int width) throws IOException {
		if (width == 0) {
			BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			image.setRGB(0, 0, 0);
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
		boolean found = false;
		Texture originalTexture = null;
		// Looks into originalTextures, sets originalTexture to one of the
		// entries of originalTextures if there is a corresponding one
		// returns the texture if the original texture found has the requested
		// width
		for (int i = 0; i < originalTextures.size(); i++) {
			Texture currentTexture = originalTextures.get(i);
			if (currentTexture.name.equals(name)) {
				if (currentTexture.image.getWidth() == width) {
					return currentTexture.image;
				}
				else {
					found = true;
					originalTexture = currentTexture;
				}
			}
		}
		// If the original texture was found (but with wrong width), then this
		// looks into scaledTextures for the corresponding texture.
		// If it finds the texture, it scales it to the requested width (if
		// necessary) and returns the scaled texture.
		if (found) {
			for (int i = 0; i < scaledTextures.size(); i++) {
				Texture currentTexture = scaledTextures.get(i);
				if (currentTexture.name.equals(name)) {
					if (currentTexture.image.getWidth() != width) {
						currentTexture.image = scaleImage(originalTexture.image, width / (float) originalTexture.image.getWidth());
					}
					return currentTexture.image;
				}
			}
		}
		// If the original is not in the originalTextures, then it loads the
		// texture form the file system and adds it to originalTextures
		if (!found) {
			String imagePath = resourcesDirectory + "\\" + name;
			if (Files.exists(Paths.get(imagePath))) {
				originalTexture = new Texture(name, ImageIO.read(new File(resourcesDirectory + "\\" + name)));
				originalTextures.add(originalTexture);
				if (originalTexture.image.getWidth() == width) {
					return originalTexture.image;
				}
			}
			else {
				throw new IOException("There is no file called \"" + name + "\" in the root directory.");
			}
		}
		// Takes the original, scales it to the requested width, and adds it to
		// scaledTextures
		Texture scaledTexture = new Texture(name, scaleImage(originalTexture.image, width / (float) originalTexture.image.getWidth()));
		scaledTextures.add(scaledTexture);
		return scaledTexture.image;
	}

	/** Scales the image by the scalingFactor */
	public static BufferedImage scaleImage(BufferedImage image, float scalingFactor) {
		BufferedImage scaledImage = new BufferedImage((int) (image.getWidth() * scalingFactor), (int) (image.getHeight() * scalingFactor), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) scaledImage.getGraphics();
		g.drawImage(image, 0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null);
		return scaledImage;
	}

	/** Represents a texture - a BufferedImage and a string name */
	private static class Texture {
		String name;
		BufferedImage image;

		public Texture(String name, BufferedImage image) {
			this.name = name;
			this.image = image;
		}
	}

	public static void initializeIO() throws IOException, VersionStringFormatException {
		logStartSectionTag("IO");
		log("Initializing IO");
		log("Looking for the root game directory.");
		boolean someResourcesAlready;
		if (Files.exists(Paths.get(rootDirectory))) {
			log("Root directory found.");
			if (Files.exists(Paths.get(resourcesDirectory))) {
				log("Resource directory found.");
				someResourcesAlready = true;
				if (!Files.exists(Paths.get(resourcesDirectory + "\\META.txt"))) {
					log("META file in the resources directory is missing!", "WARNING");
					log("Wiping the resources directory.");
					for (String file : new File(resourcesDirectory).list()) {
						Files.delete(Paths.get(resourcesDirectory + "\\" + file));
					}
					someResourcesAlready = false;
				}
			}
			else {
				log("Resources directory not found!", "WARNING");
				log("Creating resources directory.");
				Files.createDirectory(Paths.get(rootDirectory + "\\Resources"));
				someResourcesAlready = false;
			}
		}
		else {
			log("Creating the root directory.");
			Files.createDirectory(Paths.get(rootDirectory));
			log("Creating resources directory.");
			Files.createDirectory(Paths.get(rootDirectory + "\\Resources"));
			someResourcesAlready = false;
		}
		if (someResourcesAlready) {
			log("Downloading the resource meta file.");
			ResourceMeta[] serverMetas = ResourceMeta.getServerMetas();
			log("Reading the client resource meta file.");
			List<ResourceMeta> clientMetas = ResourceMeta.getClientMetas();
			List<ResourceMeta> neededResourcesMetas = new ArrayList<ResourceMeta>();
			for (int i = 0; i < serverMetas.length; i++) {
				if (!serverMetas[i].sinceVersion.isNewer(version)) {
					neededResourcesMetas.add(serverMetas[i]);
				}
			}
			log("Updating the local resources.");
			for (ResourceMeta neededMeta : neededResourcesMetas) {
				ResourceMeta correspondingClientMeta = null;
				for (int i = 0; i < clientMetas.size(); i++) {
					if (clientMetas.get(i).name.equals(neededMeta.name)) {
						correspondingClientMeta = clientMetas.get(i);
					}
				}
				if (correspondingClientMeta == null) {
					addResource(neededMeta.name);
					clientMetas.add(neededMeta);
					log("Added resource - " + neededMeta.name);
				}
				else {
					if (correspondingClientMeta.resourceVersion != -1 && correspondingClientMeta.resourceVersion < neededMeta.resourceVersion) {
						correspondingClientMeta.resourceVersion = neededMeta.resourceVersion;
						addResource(neededMeta.name);
						log("Updated resource - " + neededMeta.name);
					}
				}
			}
			ResourceMeta.writeClientMeta(clientMetas);
		}
		else {
			log("Downloading the resource meta file.");
			ResourceMeta[] serverMetas = ResourceMeta.getServerMetas();
			List<ResourceMeta> futureClientMetas = new ArrayList<ResourceMeta>();
			for (int i = 0; i < serverMetas.length; i++) {
				if (!serverMetas[i].sinceVersion.isNewer(version)) {
					futureClientMetas.add(serverMetas[i]);
				}
			}
			log("Downloading resources.");
			for (ResourceMeta meta : futureClientMetas) {
				log("Added resource - " + meta.name);
				addResource(meta.name);
			}
			log("Creating the local meta file.");
			ResourceMeta.writeClientMeta(futureClientMetas);
		}
		log("IO initialized");
		logEndSectionTag("IO");
	}

	private static void addResource(String resourceName) throws MalformedURLException, IOException {
		InputStream resourceServerStream = new URL(serverRootDirectory + "/Resources/" + resourceName).openStream();
		File futureResourceFile = new File(resourcesDirectory + "\\" + resourceName);
		if (futureResourceFile.exists()) {
			futureResourceFile.delete();
		}
		FileOutputStream resourceOutputStream = new FileOutputStream(futureResourceFile);
		byte[] buffer = new byte[16384];
		int readBytes = 0;
		while ((readBytes = resourceServerStream.read(buffer)) != -1) {
			resourceOutputStream.write(buffer, 0, readBytes);
		}
		resourceServerStream.close();
		resourceOutputStream.close();
	}

	private static class ResourceMeta {
		String name;
		Version sinceVersion;
		int resourceVersion;

		public static ResourceMeta[] getServerMetas() throws VersionStringFormatException, IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(serverRootDirectory + "/Resources/META.txt").openStream()));
			ArrayList<ResourceMeta> metas = new ArrayList<ResourceMeta>();
			String readLine;
			while ((readLine = reader.readLine()) != null) {
				String trimmedLine = readLine.trim().replace("  ", "");
				if (!trimmedLine.isEmpty()) {
					int spaceIndex = 0;
					while (trimmedLine.charAt(spaceIndex) != ' ') {
						spaceIndex++;
					}
					String name = trimmedLine.substring(0, spaceIndex);
					int oldSpaceIndex = spaceIndex;
					spaceIndex++;
					while (trimmedLine.charAt(spaceIndex) != ' ') {
						spaceIndex++;
					}
					Version sinceVer = new Version(trimmedLine.substring(oldSpaceIndex + 1, spaceIndex));
					int version = Integer.parseInt(trimmedLine.substring(spaceIndex + 1));
					metas.add(new ResourceMeta(name, sinceVer, version));
				}
			}
			reader.close();
			return metas.toArray(new ResourceMeta[0]);
		}

		public static List<ResourceMeta> getClientMetas() throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(resourcesDirectory + "\\META.txt"))));
			ArrayList<ResourceMeta> metas = new ArrayList<ResourceMeta>();
			String readLine;
			while ((readLine = reader.readLine()) != null) {
				String trimmedLine = readLine.trim().replace("  ", "");
				if (!trimmedLine.isEmpty()) {
					int spaceIndex = 0;
					while (trimmedLine.charAt(spaceIndex) != ' ') {
						spaceIndex++;
					}
					String name = trimmedLine.substring(0, spaceIndex);
					int version = Integer.parseInt(trimmedLine.substring(spaceIndex + 1));
					metas.add(new ResourceMeta(name, version));
				}
			}
			reader.close();
			return metas;
		}

		public static void writeClientMeta(List<ResourceMeta> metas) throws IOException {
			List<String> lines = new ArrayList<String>(metas.size());
			for (ResourceMeta meta : metas) {
				lines.add(meta.toString());
			}
			Files.write(Paths.get(resourcesDirectory + "\\META.txt"), lines);
		}

		@Override
		public String toString() {
			return this.name + " " + String.valueOf(this.resourceVersion);
		}

		public ResourceMeta(String name, Version sinceVersion, int resourceVersion) {
			this.name = name;
			this.sinceVersion = sinceVersion;
			this.resourceVersion = resourceVersion;
		}

		public ResourceMeta(String name, int resourceVersion) {
			this.name = name;
			this.resourceVersion = resourceVersion;
		}
	}

	private static ArrayList<String> sectionTags = new ArrayList<String>();
	private static String sectionTagsString = "";

	public static void logStartSectionTag(String tag) {
		sectionTags.add(tag);
		sectionTagsString = getSectionTags();
	}

	public static void logEndSectionTag(String tag) {
		sectionTags.remove(tag);
		sectionTagsString = getSectionTags();
	}

	private static String getSectionTags() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < sectionTags.size(); i++) {
			builder.append("[");
			builder.append(sectionTags.get(i));
			builder.append("]");
		}
		return builder.toString();
	}

	public static void log(String s, String tag) {
		System.out.println("[" + tag + "]" + sectionTagsString + " " + s);
	}

	public static void log(String s) {
		log(s, "INFO");
	}
}
