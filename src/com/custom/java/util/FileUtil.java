package com.custom.java.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

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

	/**
	 * Delete all files in the target directory
	 */
	public static void deleteFilesInDirectory(File directory) {
		if ((directory != null) && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	/**
	 * Install apk from file
	 */
	public static void installAPKFromFile(Context context, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}