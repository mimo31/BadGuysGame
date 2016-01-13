package com.github.mimo31.badguysgame.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.mimo31.badguysgame.io.Version.VersionStringFormatException;

class ResourceMeta {
	String name;
	Version sinceVersion;
	int resourceVersion;

	public static ResourceMeta[] getServerMetas() throws VersionStringFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(IOBase.serverRootDirectory + "/Resources/META.txt").openStream()));
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(IOBase.resourcesDirectory + "\\META.txt"))));
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
		Files.write(Paths.get(IOBase.resourcesDirectory + "\\META.txt"), lines);
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
