package com.example.administrator.dapclone.fragmentdownload;

import android.os.Environment;
import android.util.Log;

import com.example.administrator.dapclone.FileInfo;
import com.example.administrator.dapclone.networkinterface.NetworkApi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 03/22/2017.
 */

public class DownloadModel implements IDownloadFragment.ProvidedModel {

	private static final String TAG = DownloadModel.class.getSimpleName();

	private IDownloadFragment.RequiredPresenter presenter;

	public DownloadModel(IDownloadFragment.RequiredPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void download(final FileInfo fileInfo) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					OkHttpClient client = new OkHttpClient.Builder()
							.connectTimeout(5, TimeUnit.MINUTES)
							.readTimeout(5, TimeUnit.MINUTES)
							.build();
					Request request = new Request.Builder()
							.url(fileInfo.url)
							.build();
					Response response = client.newCall(request).execute();
					Log.d(TAG, "run: " + response);
					Log.d(TAG, "run: " + response.headers());
					Log.d(TAG, "run: " + response.code());

					if (response.code() / 100 == 2) {
						Log.d(TAG, "run: " + response.header("Content-Length"));
						fileInfo.size = Long.valueOf(response.header("Content-Length"));
						Log.d(TAG, "run: " + response.header("Accept-Ranges"));
						if ("bytes".equalsIgnoreCase(response.header("Accept-Ranges"))) {
							fileInfo.isMultiThread = true;
						} else {
							fileInfo.isMultiThread = false;
						}
						long partSize = fileInfo.size / 8;
						long fileSize = fileInfo.size;
						int position = 0;
						while (fileSize > 0) {
							performDownload(client, fileInfo, position);
							fileSize = fileSize - (1024 & 16);
							position = position + 1024 * 16;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void performDownload(OkHttpClient client, final FileInfo fileInfo, final int position) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://google.com")
				.client(client)
				.build();
		String range = "bytes=" + position + "-" + (position + (1024 * 16));
		Log.d(TAG, "performDownload: " + range);
		NetworkApi networkApi = retrofit.create(NetworkApi.class);
		Call<ResponseBody> responseBodyCall = networkApi.download(fileInfo.url, range);
		responseBodyCall.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, final retrofit2.Response<ResponseBody> response) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (response.isSuccessful()) {
							try {
								Log.d(TAG, "run: " + response.headers().get("Content-Disposition"));
								downloadFile(response.body(), fileInfo, position);
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
				Log.e(TAG, "onFailure: ", t);
			}
		});
	}

	private void downloadFile(ResponseBody body, FileInfo fileInfo, int position) throws IOException {
		fileInfo.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileInfo
				.name;
		byte data[] = new byte[1024 * 16];
		long fileSize = body.contentLength();
		InputStream bis = new BufferedInputStream(body.byteStream(), data.length);
		while (bis.read(data) != -1) {
			Log.d(TAG, "downloadFile: here");
			writeToFile(fileInfo, data, position);
		}
		/*int count;
		byte data[] = new byte[1024 * 16];
		long fileSize = body.contentLength();
		InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
		File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileInfo.name);
		OutputStream output = new FileOutputStream(outputFile);
		long startTime = System.currentTimeMillis();
		int timeCount = 1;
		while ((count = bis.read(data)) != -1) {
			Log.d(TAG, "downloadFile: here");
			long currentTime = System.currentTimeMillis() - startTime;
			if (currentTime > 1000 * timeCount) {
				timeCount++;
			}
			output.write(data, 0, count);
		}
		output.flush();
		output.close();
		bis.close();*/
		Log.d(TAG, "downloadFile: done");
	}

	private void writeToFile(FileInfo fileInfo, byte[] data, int position) {
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(fileInfo.path, "rw");
			randomAccessFile.seek(position);
			randomAccessFile.write(data);
			randomAccessFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
