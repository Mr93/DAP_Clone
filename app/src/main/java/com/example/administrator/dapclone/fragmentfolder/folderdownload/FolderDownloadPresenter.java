package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.util.Log;

import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadPresenter implements ProvidedPresenter, RequiredPresenter {

	private static final String TAG = FolderDownloadPresenter.class.getSimpleName();
	RequiredView requiredView;
	ProvidedModel providedModel;
	private List<TaskInfo> taskInfoList;


	public FolderDownloadPresenter(RequiredView requiredView) {
		this.requiredView = requiredView;
		taskInfoList = new ArrayList<>();
	}

	@Override
	public void getDownloadDataFromDB() {
		providedModel.registerBroadCast();
		providedModel.getDownloadDataFromDB();
	}

	@Override
	public ArrayList<TaskInfo> getTaskInfoList() {
		return (ArrayList<TaskInfo>) taskInfoList;
	}

	@Override
	public void setDownloadData(ArrayList<TaskInfo> taskList) {
		if (requiredView != null) {
			this.taskInfoList = taskList;
			Collections.reverse(this.taskInfoList);
			requiredView.fetchDataRecycleView();
		}
	}

	@Override
	public void createNewTask(TaskInfo taskInfo) {
		if (taskInfo != null) {
			boolean isDuplicate = false;
			for (TaskInfo temp : taskInfoList) {
				if (temp.taskId == taskInfo.taskId && temp.name.equalsIgnoreCase(taskInfo.name) &&
						temp.url.equalsIgnoreCase(taskInfo.url) && temp.isDownload == taskInfo.isDownload) {
					isDuplicate = true;
					break;
				}
			}
			if (!isDuplicate && requiredView != null) {
				this.taskInfoList.add(0, taskInfo);
				requiredView.updateDataRecycleView();
			}
		}
		Log.d(TAG, "createNewTask: " + taskInfo.taskId);
		Log.d(TAG, "createNewTask: " + this.taskInfoList.size());
	}

	@Override
	public void updateATask(TaskInfo taskInfo) {
		Log.d(TAG, "updateATask: " + taskInfo.processedSize);
		Log.d(TAG, "updateATask: " + this.taskInfoList.size());
		if (taskInfo != null) {
			for (TaskInfo temp : taskInfoList) {
				if (temp.taskId == taskInfo.taskId && temp.name.equalsIgnoreCase(taskInfo.name) &&
						temp.url.equalsIgnoreCase(taskInfo.url) && temp.isDownload == taskInfo.isDownload) {
					temp.processedSize = taskInfo.processedSize;
					break;
				}
			}
			if (requiredView != null) {
				requiredView.updateDataRecycleView();
			}
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

	@Override
	public void unRegisterBroadcast() {
		providedModel.unRegisterBroadCast();
	}
}
