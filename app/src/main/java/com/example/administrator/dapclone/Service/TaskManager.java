package com.example.administrator.dapclone.Service;

import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.SettingUtils;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 03/27/2017.
 */

public class TaskManager extends Thread {
	private static final String TAG = TaskManager.class.getSimpleName();
	BlockingQueue<Task> downloadingTask, pendingTask, errorTask;
	private static TaskManager instance;
	public boolean isRunning = false;

	private TaskManager() {
		setUpQueues();
	}

	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}

	private void setUpQueues() {
		getDownloadingQueue();
		getPendingQueue();
		getErrorQueue();
		//updateFromPendingToDownloading();
	}

	//get queue from DB
	private void getDownloadingQueue() {
		downloadingTask = new LinkedBlockingQueue<>();
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_DOWNLOADING);
		for (int i = 0; i < taskInfoList.size(); i++) {
			downloadingTask.offer(new Task(taskInfoList.get(i), this));
		}
	}

	private void getPendingQueue() {
		pendingTask = new LinkedBlockingQueue<>();
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_PENDING);
		for (int i = 0; i < taskInfoList.size(); i++) {
			if (SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD) > downloadingTask.size()) {
				Task task = new Task(taskInfoList.get(i), this);
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
				DBHelper.getInstance().updateTask(task.getTaskInfo());
			} else {
				pendingTask.offer(new Task(taskInfoList.get(i), this));
			}
		}
	}

	private void getErrorQueue() {
		errorTask = new LinkedBlockingQueue<>();
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_ERROR);
		for (int i = 0; i < taskInfoList.size(); i++) {
			errorTask.offer(new Task(taskInfoList.get(i), this));
		}
	}

	private void updateFromPendingToDownloading() {
		int offset = SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD) - downloadingTask.size();
		Log.d(TAG, "updateFromPendingToDownloading: " + offset);
		Log.d(TAG, "updateFromPendingToDownloading: " + SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD));
		Log.d(TAG, "updateFromPendingToDownloading: " + downloadingTask.size());
		for (int i = 0; i < offset; i++) {
			Task task = pendingTask.poll();
			if (task != null) {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
				Log.d(TAG, "updateFromPendingToDownloading: " + task.getTaskInfo().taskId);
				DBHelper.getInstance().updateTask(task.getTaskInfo());
			}
		}
	}

	@Override
	public void run() {
		Log.d(TAG, "run: start task manager");
		isRunning = true;
		try {
			while (isRunning) {
				updateFromPendingToDownloading();
				for (Task task : downloadingTask) {
					if (!task.isRunning()) {
						task.setRunning(true);
						task.start();
					}
				}
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addTask(TaskInfo taskInfo) {
		Task task = new Task(taskInfo, this);
		if (checkContain(taskInfo, errorTask)) {
			errorTask.remove(task);
			if (downloadingTask.size() >= SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD)) {
				pendingTask.offer(task);
			} else {
				downloadingTask.offer(task);
			}
		} else if (!checkContain(taskInfo, downloadingTask) && !checkContain(taskInfo, pendingTask)) {
			if (downloadingTask.size() >= SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD)) {
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				pendingTask.offer(task);
			} else {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
			}
		}
	}

	public boolean checkContain(TaskInfo taskInfo, BlockingQueue<Task> tasks) {
		for (Task task : tasks) {
			if (task.getTaskInfo().isDownload == taskInfo.isDownload &&
					task.getTaskInfo().name.equalsIgnoreCase(taskInfo.name) &&
					task.getTaskInfo().url.equalsIgnoreCase(taskInfo.url)) {
				return true;
			}
		}
		return false;
	}

	public void taskCompleted(Task task) {
		Log.d(TAG, "taskCompleted: " + downloadingTask.remove(task));
		task.getTaskInfo().status = ConstantValues.STATUS_COMPLETED;
		DBHelper.getInstance().updateTask(task.getTaskInfo());
		updateFromPendingToDownloading();
	}

	public void taskErrored(Task task) {
		Log.d(TAG, "taskCompleted: " + downloadingTask.remove(task));
		errorTask.offer(task);
	}
}
