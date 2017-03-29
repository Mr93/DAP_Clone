package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
		providedModel.registerBroadCast();
		providedModel.getDownloadDataFromDB();
	}

	@Override
	public void setDownloadData(ArrayList<TaskInfo> taskList) {
		if (requiredView != null) {
			Collections.reverse(taskList);
			requiredView.updateDataRecycleView(taskList);
		}
	}

	@Override
	public void createNewTask(TaskInfo taskInfo) {
		ArrayList<TaskInfo> listTask = (ArrayList<TaskInfo>) requiredView.getListTask();
		listTask.add(0, taskInfo);
		if (requiredView != null) {
			requiredView.updateDataRecycleView(listTask);
		}
	}

	@Override
	public void updateATask(TaskInfo taskInfo) {
		ArrayList<TaskInfo> listTask = (ArrayList<TaskInfo>) requiredView.getListTask();
		listTask.add(0, taskInfo);
		if (requiredView != null) {
			requiredView.updateDataRecycleView(listTask);
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
