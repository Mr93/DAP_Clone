package com.example.administrator.dapclone.fragmentdownload;

import android.os.Environment;
import android.util.Log;

import com.example.administrator.dapclone.FileInfo;
import com.example.administrator.dapclone.exception.NetworkException;
import com.example.administrator.dapclone.networkinterface.NetworkApi;
import com.example.administrator.dapclone.utils.UrlValidate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 03/21/2017.
 */

public class DownloadPresenter implements IDownloadFragment.ProvidedPresenter, IDownloadFragment.RequiredPresenter {

	private static final String TAG = DownloadPresenter.class.getSimpleName();
	private IDownloadFragment.RequiredView requiredView;
	private IDownloadFragment.ProvidedModel providedModel;

	public DownloadPresenter(IDownloadFragment.RequiredView requiredView) {
		this.requiredView = requiredView;
	}

	@Override
	public void download(final String url) {
		try {
			if (UrlValidate.isValid(url)) {
				FileInfo downloadFile = new FileInfo();
				downloadFile.name = UrlValidate.getFileNameFromUrl(url);
				downloadFile.extension = UrlValidate.getExtension(url);
				downloadFile.url = url;
				providedModel.download(downloadFile);
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
