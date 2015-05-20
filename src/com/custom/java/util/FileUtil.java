package com.custom.java.util;

import android.os.Environment;

import java.io.File;

public class FileUtil {
	/**
	 * Create a directory for specific type and name
	 */
	public static File getDir(String type, String name) {
		File tempDir = null;
		if (type == Environment.DIRECTORY_PICTURES) {
			tempDir = new File(Environment.getExternalStoragePublicDirectory(type), name);
		}

		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		return tempDir;
	}
}