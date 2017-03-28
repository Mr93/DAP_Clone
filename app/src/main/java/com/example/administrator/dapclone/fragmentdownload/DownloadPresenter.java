package com.example.administrator.dapclone.fragmentdownload;

import android.content.Context;
import android.util.Log;

import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.exception.NetworkException;
import com.example.administrator.dapclone.utils.Validator;

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
				TaskInfo taskInfo = new TaskInfo();
				taskInfo.name = Validator.getFileNameFromUrl(url);
				taskInfo.extension = Validator.getExtension(url);
				taskInfo.url = url;
				if (!DBHelper.getInstance().checkTaskDownloadExisted(taskInfo)) {
					taskInfo.taskId = DBHelper.getInstance().getTaskIdByNameAndUrl(taskInfo);
					Log.d(TAG, "download: " + taskInfo.taskId);
					providedModel.download(taskInfo);
				}
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

	@Override
	public Context getContext() {
		return requiredView.getFragmentContext();
	}
}
