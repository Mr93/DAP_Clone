package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadFragmentModel implements ProvidedModel {
	private static final String TAG = FolderDownloadFragmentModel.class.getSimpleName();
	RequiredPresenter requiredPresenter;
	BroadcastReceiver broadcastReceiver;

	public FolderDownloadFragmentModel(RequiredPresenter requiredPresenter) {
		this.requiredPresenter = requiredPresenter;
	}

	@Override
	public void getDownloadDataFromDB() {
		new AsyncTask<Void, Void, ArrayList<TaskInfo>>() {
			@Override
			protected ArrayList<TaskInfo> doInBackground(Void... params) {
				return DBHelper.getInstance().getAllTask();
			}

			@Override
			protected void onPostExecute(ArrayList<TaskInfo> values) {
				if (values != null && !values.isEmpty()) {
					Log.d(TAG, "onPostExecute: " + values.size());
					requiredPresenter.setDownloadData(values);
				}
				super.onPostExecute(values);
			}
		}.execute();
	}

	@Override
	public void registerBroadCast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantValues.ACTION_NEW_TASK);
		filter.addAction(ConstantValues.ACTION_ERROR_TASK);
		filter.addAction(ConstantValues.ACTION_UPDATE_TASK);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null) {
					TaskInfo taskInfo = intent.getParcelableExtra(ConstantValues.FILE_INFO);
					if (taskInfo == null) {
						return;
					}
					if (ConstantValues.ACTION_NEW_TASK.equalsIgnoreCase(intent.getAction())) {

					} else if (ConstantValues.ACTION_UPDATE_TASK.equalsIgnoreCase(intent.getAction())) {

					} else {

					}
				}
			}
		};
		MyApplication.getAppContext().registerReceiver(broadcastReceiver, filter);
	}
}
