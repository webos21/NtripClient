package com.gmail.webos21.android.ild;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir at SDCARD to save cached images
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// if SDCARD is mounted (SDCARD is present on device and mounted)
			String extDirPath = Environment.getExternalStorageDirectory()
					+ "/Android/data/" + context.getPackageName();
			cacheDir = new File(extDirPath, "cache");
		} else {
			// if checking on simulator the create cache dir in your application
			// context
			cacheDir = context.getCacheDir();
		}

		if (!cacheDir.exists()) {
			// create cache dir in your application context
			cacheDir.mkdirs();
		}
	}

	public File getFile(String url) {
		int posLastDot = url.lastIndexOf('.');
		int posLastSlash = url.lastIndexOf('/');

		String suffix = url.substring(posLastDot);
		String filename = url.substring(posLastSlash + 1, posLastDot);
		String cacheFileName = "R" + Integer.toHexString(url.hashCode()) + "-"
				+ filename + suffix;

		File f = new File(cacheDir, cacheFileName);
		return f;

	}

	public void clear() {
		// list all files inside cache directory
		File[] files = cacheDir.listFiles();
		if (files == null) {
			return;
		}

		// delete all cache directory files
		for (File f : files) {
			f.delete();
		}
	}

}
