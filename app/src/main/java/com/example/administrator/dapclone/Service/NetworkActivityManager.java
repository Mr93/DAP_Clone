package com.example.administrator.dapclone.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.networkinterface.NetworkApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 03/24/2017.
 */

public class NetworkActivityManager extends Service {

	private static final String TAG = NetworkActivityManager.class.getSimpleName();

	public static Object monitor = new Object();

	private TaskManager taskManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		taskManager = TaskManager.getInstance();
		if (intent != null) {
			TaskInfo taskInfo = intent.getParcelableExtra(ConstantValues.FILE_INFO);
			File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
			filePath.mkdirs();
			File file = new File(taskInfo.path);
			if (file.exists()) {
				Log.d(TAG, "downloadMultiThread: delete file " + file.delete());
			}
			taskManager.addTask(taskInfo);
			if (taskManager.getState() == Thread.State.WAITING) {
				taskManager.setRunning(true);
				synchronized (monitor) {
					monitor.notify();
				}
			} else if (taskManager.getState() == Thread.State.NEW) {
				taskManager.start();
			}
		}
		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy: ");
		super.onDestroy();
	}
}
