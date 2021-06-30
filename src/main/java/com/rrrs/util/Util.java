package com.rrrs.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

	public static String filenameWithDate(String filename) {
		if (filename == null || filename=="")
			return "";
		LocalDateTime dateTime=LocalDateTime.now();
		return com.google.common.io.Files.getNameWithoutExtension(filename).toUpperCase() + "_"
				+ dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "."
				+ com.google.common.io.Files.getFileExtension(filename);
	}
	public static String filenameWithCaseInsensitive(String filename) {
		if (filename == null || filename=="")
			return "";
		return com.google.common.io.Files.getNameWithoutExtension(filename).toUpperCase() + "."
				+ com.google.common.io.Files.getFileExtension(filename);
	}
	
	public static String filenameWithoutExtension(String filename){
		return com.google.common.io.Files.getNameWithoutExtension(filename);
	}

	public static String fileextensionWithoutName(String filename){
		return com.google.common.io.Files.getFileExtension(filename);
	}

	
}
