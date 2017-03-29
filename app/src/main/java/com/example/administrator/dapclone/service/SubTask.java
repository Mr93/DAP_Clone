package com.example.administrator.dapclone.service;

import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.SubTaskInfo;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.networkinterface.NetworkApi;

import java.io.FileNotFoundException;
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
 * Created by Administrator on 03/27/2017.
 */

public class SubTask extends Thread {
	private static final String TAG = SubTask.class.getSimpleName();
	private Task task;
	private SubTaskInfo subTaskInfo;
	private boolean isRunning = false;
	private byte[] buffer = new byte[4096];
	RandomAccessFile randomAccessFile;
	private int errorTime = 0;
	private boolean isError = false;

	public SubTask(Task task, SubTaskInfo subTaskInfo) {
		this.task = task;
		Log.d(TAG, "SubTask aaaa: " + this.task.getTaskInfo().taskId);
		this.subTaskInfo = subTaskInfo;
		Log.d(TAG, "SubTask aaaa: " + this.getSubTaskInfo().taskId);
	}

	public SubTaskInfo getSubTaskInfo() {
		return subTaskInfo;
	}

	@Override
	public synchronized void start() {
		super.start();
		isRunning = true;
		try {
			randomAccessFile = new RandomAccessFile(task.getTaskInfo().path, "rw");
			randomAccessFile.seek(subTaskInfo.start);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		try {
			updateDB();
			while (isRunning) {
				isRunning = false;
				OkHttpClient client = new OkHttpClient.Builder()
						.connectTimeout(5, TimeUnit.MINUTES)
						.readTimeout(5, TimeUnit.MINUTES)
						.build();
				downloadAPart(client, task.getTaskInfo());
			}
		} finally {
			if (isError) {
				stopDownload();
			}
		}

	}

	private void updateDB() {
		if (subTaskInfo.subTaskId == -1) {
			this.subTaskInfo.subTaskId = DBHelper.getInstance().insertSubTask(subTaskInfo, task.getTaskInfo().taskId);
		} else {
			if (task.getTaskInfo().taskId == -1) {
				Log.d(TAG, "updateDB: 2 ");
				this.subTaskInfo.subTaskId = DBHelper.getInstance().insertSubTask(subTaskInfo, task.getTaskInfo().taskId);
			}
		}
	}

	private void downloadAPart(final OkHttpClient client, final TaskInfo taskInfo) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://google.com")
				.client(client)
				.build();
		String range = "bytes=" + subTaskInfo.start + "-" + subTaskInfo.end;
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
								writeToFile(response.body());
								Log.d(TAG, "run: code " + response.code());
							} catch (IOException e) {
								isError = true;
								e.printStackTrace();
							}
						} else {
							isError = true;
							Log.d(TAG, "server contact failed");
						}
					}
				}).start();
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				handLeError(client, taskInfo);
			}
		});
	}

	private void handLeError(OkHttpClient client, TaskInfo taskInfo) {
		if (errorTime < 3) {
			downloadAPart(client, taskInfo);
			errorTime++;
		} else {
			isError = true;
		}
	}

	private void stopDownload() {
		subTaskInfo.status = ConstantValues.STATUS_ERROR;
		Log.d(TAG, "stopDownload: " + subTaskInfo.status + ", " + subTaskInfo.end + ", " + this.getState());
		DBHelper.getInstance().updateSubTask(subTaskInfo, subTaskInfo.taskId);
		task.onThreadError(this);
	}

	//synchronized
	private void writeToFile(ResponseBody body) throws IOException {
		InputStream inputStream = body.byteStream();
		int count;
		while ((count = inputStream.read(buffer)) != -1) {
			randomAccessFile.write(buffer, 0, count);
		}
		inputStream.close();
		randomAccessFile.close();
		Log.d(TAG, "writeToFile: done " + subTaskInfo.end);
		subTaskInfo.status = ConstantValues.STATUS_COMPLETED;
		task.onThreadDone(this);
		DBHelper.getInstance().updateSubTask(subTaskInfo, subTaskInfo.taskId);
	}
}
