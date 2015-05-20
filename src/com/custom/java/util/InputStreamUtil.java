package com.custom.java.util;

import com.custom.general.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InputStreamUtil {
	/**
	 * Get a opened input stream for given url
	 */
	public static InputStream getURLInputStream(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			return connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Convert inputStream to byteArray
	 */
	public static byte[] convertInStreamToBytes(InputStream inputStream) {
		if (inputStream == null) {
			Logger.d(InputStreamUtil.class, "Something wrong with loading image");

			return null;
		}

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buff)) != -1) {
				outputStream.write(buff, 0, len);
			}
			inputStream.close();
			outputStream.close();
			return outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}