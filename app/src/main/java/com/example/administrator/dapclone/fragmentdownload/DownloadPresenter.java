package com.example.administrator.dapclone.fragmentdownload;

import android.util.Log;

import com.example.administrator.dapclone.exception.NetworkException;
import com.example.administrator.dapclone.utils.UrlValidate;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 03/21/2017.
 */

public class DownloadPresenter implements IDownloadFragment.ProvidedPresenter, IDownloadFragment.RequiredPresenter {

	private static final String TAG = DownloadPresenter.class.getSimpleName();
	private IDownloadFragment.RequiredView requiredView;
	private IDownloadFragment.ProvidedModel providedModel;


	@Override
	public void download(final String url) {
		try {
			if (UrlValidate.isValid(url)) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							OkHttpClient client = new OkHttpClient();
							Request request = new Request.Builder()
									.url(url)
									.build();
							Log.d(TAG, "run: here ");
							Response response = client.newCall(request).execute();
							Log.d(TAG, "run: " + response);
							Log.d(TAG, "run: " + response.header("Content-Length"));
							Log.d(TAG, "run: " + response.header("Content-Range"));
							Log.d(TAG, "run: " + response.header("Content-Type"));
							Log.d(TAG, "run: " + response.header("Status-Line"));
							Log.d(TAG, "run: " + response.header("Accept-Ranges"));
							Log.d(TAG, "run: " + response.header("Accept-Ranges"));
							Log.d(TAG, "run: " + response.headers());
							Log.d(TAG, "run: " + response.code());

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();

			}
		} catch (NetworkException e) {
			requiredView.invalidUrl(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setView(IDownloadFragment.RequiredView view) {
		requiredView = view;
	}

	@Override
	public void setModel(IDownloadFragment.ProvidedModel model) {
		providedModel = model;
	}
}
