package game.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import game.io.Version.VersionStringFormatException;

public class IOInitialization {

	public static boolean initialize() throws IOException, VersionStringFormatException {
		Logging.logStartSectionTag("IO");
		Logging.log("Initializing IO");
		Logging.log("Looking for the root game directory.");
		boolean someResourcesAlready;
		if (Files.exists(Paths.get(IOBase.rootDirectory))) {
			Logging.log("Root directory found.");
			if (Files.exists(Paths.get(IOBase.resourcesDirectory))) {
				Logging.log("Resource directory found.");
				someResourcesAlready = true;
				if (!Files.exists(Paths.get(IOBase.resourcesDirectory + "\\META.txt"))) {
					Logging.log("META file in the resources directory is missing!", "WARNING");
					Logging.log("Wiping the resources directory.");
					for (String file : new File(IOBase.resourcesDirectory).list()) {
						Files.delete(Paths.get(IOBase.resourcesDirectory + "\\" + file));
					}
					someResourcesAlready = false;
				}
			}
			else {
				Logging.log("Resources directory not found!", "WARNING");
				Logging.log("Creating resources directory.");
				Files.createDirectory(Paths.get(IOBase.rootDirectory + "\\Resources"));
				someResourcesAlready = false;
			}
		}
		else {
			Logging.log("Creating the root directory.");
			Files.createDirectory(Paths.get(IOBase.rootDirectory));
			Logging.log("Creating resources directory.");
			Files.createDirectory(Paths.get(IOBase.rootDirectory + "\\Resources"));
			someResourcesAlready = false;
		}
		if (someResourcesAlready) {
			if (IOBase.isServerReachable()) {
				Logging.log("Downloading the resource meta file.");
				ResourceMeta[] serverMetas = ResourceMeta.getServerMetas();
				Logging.log("Reading the client resource meta file.");
				List<ResourceMeta> clientMetas = ResourceMeta.getClientMetas();
				List<ResourceMeta> neededResourcesMetas = new ArrayList<ResourceMeta>();
				for (int i = 0; i < serverMetas.length; i++) {
					if (!serverMetas[i].sinceVersion.isNewer(IOBase.version)) {
						neededResourcesMetas.add(serverMetas[i]);
					}
				}
				Logging.log("Updating the local resources.");
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
						Logging.log("Added resource - " + neededMeta.name);
					}
					else {
						if (correspondingClientMeta.resourceVersion != -1 && correspondingClientMeta.resourceVersion < neededMeta.resourceVersion) {
							correspondingClientMeta.resourceVersion = neededMeta.resourceVersion;
							addResource(neededMeta.name);
							Logging.log("Updated resource - " + neededMeta.name);
						}
					}
				}
				ResourceMeta.writeClientMeta(clientMetas);
			}
			else {
				Logging.log("Server is unreachable! - Skiping the resources update.", "WARNING");
			}
		}
		else {
			if (IOBase.isServerReachable()) {
				Logging.log("Downloading the resource meta file.");
				ResourceMeta[] serverMetas = ResourceMeta.getServerMetas();
				List<ResourceMeta> futureClientMetas = new ArrayList<ResourceMeta>();
				for (int i = 0; i < serverMetas.length; i++) {
					if (!serverMetas[i].sinceVersion.isNewer(IOBase.version)) {
						futureClientMetas.add(serverMetas[i]);
					}
				}
				Logging.log("Downloading resources.");
				for (ResourceMeta meta : futureClientMetas) {
					Logging.log("Added resource - " + meta.name);
					addResource(meta.name);
				}
				Logging.log("Creating the local meta file.");
				ResourceMeta.writeClientMeta(futureClientMetas);
			}
			else {
				Logging.log("Server is unreachable! - Failed to download resources.", "WARNING");
				Logging.log("IO initialized (unsuccessfully)");
				Logging.logEndSectionTag("IO");
				return false;
			}
		}
		Logging.log("IO initialized");
		Logging.logEndSectionTag("IO");
		return true;
	}
	
	private static void addResource(String resourceName) throws MalformedURLException, IOException {
		InputStream resourceServerStream = new URL(IOBase.serverRootDirectory + "/Resources/" + resourceName).openStream();
		File futureResourceFile = new File(IOBase.resourcesDirectory + "\\" + resourceName);
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
}
