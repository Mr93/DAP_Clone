package com.example.administrator.dap_clone.Utils;

import android.util.Log;

/**
 * Created by Administrator on 03/21/2017.
 */

public class UrlValidate {

	private static final String TAG = UrlValidate.class.getSimpleName();

	public static boolean isValid(String url) {
		boolean valid = true;
		if (url == null) {
			Log.d(TAG, "isValid: null");
			valid = false;
		} else if (url.equalsIgnoreCase("")) {
			Log.d(TAG, "isValid: blank");
			valid = false;
		}
		return valid;
	}


}
