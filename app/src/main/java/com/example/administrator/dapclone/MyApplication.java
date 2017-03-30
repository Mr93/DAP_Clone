package com.example.administrator.dapclone;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 03/27/2017.
 */

public class MyApplication extends Application {

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		createApplicationContext(this);
	}

	private static void createApplicationContext(MyApplication downloadManagerApplication) {
		context = downloadManagerApplication.getApplicationContext();
	}

	public static Context getAppContext() {
		return context;
	}
}
