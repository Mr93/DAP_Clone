package com.example.administrator.dapclone.fragmentdownload;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.FileInfo;
import com.example.administrator.dapclone.Service.NetworkActivityManager;
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
					if (response.code() / 100 == 2) {
						fileInfo.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileInfo
								.name;
						if (response.header("Content-Length") != null) {
							fileInfo.size = Long.valueOf(response.header("Content-Length"));
						}
						if ("bytes".equalsIgnoreCase(response.header("Accept-Ranges")) && fileInfo.size != 0) {
							fileInfo.isMultiThread = true;
						} else {
							fileInfo.isMultiThread = false;
						}
						Intent intent = new Intent(presenter.getContext(), NetworkActivityManager.class);
						intent.putExtra(ConstantValues.FILE_INFO, fileInfo);
						presenter.getContext().startService(intent);
						//downloadMultiThread(client, fileInfo);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


}
