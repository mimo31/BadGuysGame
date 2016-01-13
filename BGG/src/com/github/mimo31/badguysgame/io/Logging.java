package com.github.mimo31.badguysgame.io;

import java.util.ArrayList;

public class Logging {

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
