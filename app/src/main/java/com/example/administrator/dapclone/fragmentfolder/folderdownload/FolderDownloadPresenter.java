package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadPresenter implements ProvidedPresenter, RequiredPresenter {

	RequiredView requiredView;
	ProvidedModel providedModel;

	public FolderDownloadPresenter(RequiredView requiredView) {
		this.requiredView = requiredView;
	}

	@Override
	public void getDownloadDataFromDB() {
		providedModel.getDownloadDataFromDB();
	}

	@Override
	public void setDownloadData(ArrayList<TaskInfo> taskList) {
		if (requiredView != null) {
			requiredView.updateDataRecycleView(taskList);
		}
	}

	@Override
	public void setView(RequiredView view) {
		this.requiredView = view;
	}

	@Override
	public void setModel(ProvidedModel model) {
		this.providedModel = model;
	}
}
