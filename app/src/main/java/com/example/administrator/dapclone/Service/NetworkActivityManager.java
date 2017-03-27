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

	private int numberSubThread = 8;
	private byte[] buffer = new byte[4096];

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (intent != null) {
				TaskInfo taskInfo = (TaskInfo) intent.getParcelableExtra(ConstantValues.FILE_INFO);
				File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
				filePath.mkdirs();
				File file = new File(taskInfo.path);
				if (file.exists()) {
					Log.d(TAG, "downloadMultiThread: delete file " + file.delete());
				}
				OkHttpClient client = new OkHttpClient.Builder()
						.connectTimeout(5, TimeUnit.MINUTES)
						.readTimeout(5, TimeUnit.MINUTES)
						.build();
				downloadMultiThread(client, taskInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	private void downloadMultiThread(OkHttpClient client, TaskInfo taskInfo) throws IOException {
		long partSize = taskInfo.size / numberSubThread;
		long fileSize = taskInfo.size;
		int position = 0;
		while (fileSize > 0) {
			downloadAPart(client, taskInfo, position);
			fileSize = fileSize - (partSize);
			position = position + (int) partSize;
		}
	}

	private void downloadAPart(final OkHttpClient client, final TaskInfo taskInfo, final int startPosition) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://google.com")
				.client(client)
				.build();
		int endPoint;

		endPoint = getEndPoint(taskInfo, startPosition);
		String range = "bytes=" + startPosition + "-" + (endPoint - 1);
		NetworkApi networkApi = retrofit.create(NetworkApi.class);
		Call<ResponseBody> responseBodyCall = networkApi.download(taskInfo.url, range, "close");
		responseBodyCall.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(final Call<ResponseBody> call, final retrofit2.Response<ResponseBody> response) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (response.isSuccessful()) {
							try {
								if (response.code() != 206) {
									Log.d(TAG, "run: error");
								}
								Log.d(TAG, "run: code " + response.code());
								Log.d(TAG, "run " + startPosition + ": " + response.headers().get("Content-Length"));
								Log.d(TAG, "run " + startPosition + ": " + response.headers().get("Content-Range"));
								writeToFile(response.body(), taskInfo, startPosition);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							Log.d(TAG, "server contact failed");
						}
					}
				}).start();
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				Log.d(TAG, "onFailure: " + startPosition);
				Log.e(TAG, "onFailure: ", t);
				downloadAPart(client, taskInfo, startPosition);
			}
		});
	}

	private int getEndPoint(TaskInfo taskInfo, int startPosition) {
		int endPoint;
		if ((taskInfo.size - startPosition) > (taskInfo.size / numberSubThread)) {
			endPoint = startPosition + (int) taskInfo.size / numberSubThread;
		} else {
			endPoint = (int) taskInfo.size;
		}
		return endPoint;
	}

	private synchronized void writeToFile(ResponseBody body, TaskInfo taskInfo, int position) throws IOException {
		InputStream inputStream = body.byteStream();
		RandomAccessFile randomAccessFile = new RandomAccessFile(taskInfo.path, "rw");
		Log.d(TAG, "writeToFile " + position + ": " + body.contentLength());
		randomAccessFile.seek(position);
		int count;
		while ((count = inputStream.read(buffer)) != -1) {
			randomAccessFile.write(buffer, 0, count);
		}
		inputStream.close();
		randomAccessFile.close();
		Log.d(TAG, "writeToFile: done ");
	}
}
