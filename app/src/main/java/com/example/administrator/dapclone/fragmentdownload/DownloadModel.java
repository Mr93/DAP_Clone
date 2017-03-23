package com.example.administrator.dapclone.fragmentdownload;

import android.os.Environment;
import android.util.Log;

import com.example.administrator.dapclone.FileInfo;
import com.example.administrator.dapclone.networkinterface.NetworkApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

	private int numberSubThread = 2;
	private byte[] buffer = new byte[1024 * 8];

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
					Log.d(TAG, "run md5 : " + response.header("Content-MD5"));
					if (response.code() / 100 == 2) {
						fileInfo.size = Long.valueOf(response.header("Content-Length"));
						fileInfo.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileInfo
								.name;
						File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
						filePath.mkdirs();
						File file = new File(fileInfo.path);
						if (file.exists()) {
							Log.d(TAG, "downloadMultiThread: delete file " + file.delete());
						}
						if ("bytes".equalsIgnoreCase(response.header("Accept-Ranges"))) {
							fileInfo.isMultiThread = true;
							downloadMultiThread(client, fileInfo);
						} else {
							fileInfo.isMultiThread = false;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void downloadMultiThread(OkHttpClient client, FileInfo fileInfo) throws IOException {
		long partSize = fileInfo.size / numberSubThread;
		long fileSize = fileInfo.size;
		int position = 0;
		while (fileSize > 0) {
			downloadAPart(client, fileInfo, position);
			fileSize = fileSize - (partSize);
			position = position + (int) partSize;
		}
	}

	private void downloadAPart(final OkHttpClient client, final FileInfo fileInfo, final int startPosition) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://google.com")
				.client(client)
				.build();
		int endPoint;
		endPoint = getEndPoint(fileInfo, startPosition);
		String range = "bytes=" + startPosition + "-" + (endPoint - 1);
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
								writeToFile(response.body(), fileInfo, startPosition);
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
				downloadAPart(client, fileInfo, startPosition);
			}
		});
	}

	private int getEndPoint(FileInfo fileInfo, int startPosition) {
		int endPoint;
		if ((fileInfo.size - startPosition) > (fileInfo.size / numberSubThread)) {
			endPoint = startPosition + (int) fileInfo.size / numberSubThread;
		} else {
			endPoint = (int) fileInfo.size;
		}
		return endPoint;
	}

	private void writeToFile(ResponseBody body, FileInfo fileInfo, int position) throws IOException {
		InputStream inputStream = body.byteStream();
		RandomAccessFile randomAccessFile = new RandomAccessFile(fileInfo.path, "rwd");
		randomAccessFile.seek(position);
		int count;
		while ((count = inputStream.read(buffer)) > 0) {
			randomAccessFile.write(buffer, 0, count);
		}
		inputStream.close();
		randomAccessFile.close();
		Log.d(TAG, "writeToFile: done ");
	}
}
