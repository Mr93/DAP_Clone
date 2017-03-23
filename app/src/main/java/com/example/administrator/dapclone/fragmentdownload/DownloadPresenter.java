package com.example.administrator.dapclone.fragmentdownload;

import android.os.Environment;
import android.util.Log;

import com.example.administrator.dapclone.FileInfo;
import com.example.administrator.dapclone.exception.NetworkException;
import com.example.administrator.dapclone.utils.Validator;

import java.io.File;

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
	public void download(String url) {
		try {
			if (!Validator.HTTP.equalsIgnoreCase(Validator.getProtocol(url)) &&
					!Validator.HTTPS.equalsIgnoreCase(Validator.getProtocol(url))) {
				url = Validator.addProtocol(url);
			}
			if (Validator.isValid(url)) {
				FileInfo downloadFile = new FileInfo();
				downloadFile.name = Validator.getFileNameFromUrl(url);
				downloadFile.extension = Validator.getExtension(url);
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
