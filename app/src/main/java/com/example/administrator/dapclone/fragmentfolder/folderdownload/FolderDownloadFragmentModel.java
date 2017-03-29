package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadFragmentModel implements ProvidedModel {
	private static final String TAG = FolderDownloadFragmentModel.class.getSimpleName();
	RequiredPresenter requiredPresenter;

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
}
