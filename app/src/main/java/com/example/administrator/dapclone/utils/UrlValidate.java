package com.example.administrator.dapclone.utils;

import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.example.administrator.dapclone.exception.NetworkException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 03/21/2017.
 */

public class UrlValidate {

	private static final String TAG = UrlValidate.class.getSimpleName();
	private static String[] extensionList = {
			"png",
			"jpeg",
			"bmp",
			"mp4",
			"avi",
			"jpg",
			"pdf",
			"mp3"
	};

	public static boolean isValid(String url) throws NetworkException {
		boolean valid;
		if (url == null) {
			throw new NetworkException("url null");
		} else if (url.equalsIgnoreCase("")) {
			throw new NetworkException("url blank");
		} else if (!isValidExtension(url)) {
			throw new NetworkException("unsupported extension");
		} else {
			valid = true;
		}
		return valid;
	}

	public static boolean isValidExtension(String stringUrl) {
		String extension = getExtension(stringUrl);
		for (String supportExtension : extensionList) {
			if (supportExtension.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

	public static String getExtension(String url) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		return extension;
	}

	public static String getProtocol(String stringUrl) {
		String protocol = "";
		try {
			URL url = new URL(stringUrl);
			protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return protocol;
	}

	public static String getFileNameFromUrl(String stringUrl) {
		Log.d(TAG, "getFileNameFromUrl: " + URLUtil.guessFileName(stringUrl, null, null));
		return URLUtil.guessFileName(stringUrl, null, null);
	}
}
