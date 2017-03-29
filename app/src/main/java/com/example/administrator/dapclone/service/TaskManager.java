package com.example.administrator.dapclone.service;

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
	private boolean isRunning = false;
	private int redownloadErrorTime = 1;


	private TaskManager() {
		downloadingTask = new LinkedBlockingQueue<>();
		pendingTask = new LinkedBlockingQueue<>();
		errorTask = new LinkedBlockingQueue<>();
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
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_DOWNLOADING);
		for (int i = 0; i < taskInfoList.size(); i++) {
			downloadingTask.offer(new Task(taskInfoList.get(i), this));
		}
	}

	private void getPendingQueue() {
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_PENDING);
		for (int i = 0; i < taskInfoList.size(); i++) {
			if (SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD) > downloadingTask
					.size()) {
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
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_ERROR);
		for (int i = 0; i < taskInfoList.size(); i++) {
			errorTask.offer(new Task(taskInfoList.get(i), this));
		}
	}

	private void updateFromPendingToDownloading() {
		int offset = SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD) -
				downloadingTask.size();
		Log.d(TAG, "updateFromPendingToDownloading: " + offset);
		Log.d(TAG, "updateFromPendingToDownloading: " + SettingUtils.getIntSettings(ConstantValues
				.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD));
		Log.d(TAG, "updateFromPendingToDownloading: " + downloadingTask.size());
		if (pendingTask.isEmpty() && downloadingTask.isEmpty() && redownloadErrorTime > 0 && !errorTask.isEmpty()) {
			for (Task task : errorTask) {
				Log.d(TAG, "updateDownloadingList: " + errorTask.remove(task));
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				Task newTask = new Task(task.getTaskInfo(), this);
				pendingTask.offer(newTask);
				DBHelper.getInstance().updateTask(task.getTaskInfo());
			}
			redownloadErrorTime = redownloadErrorTime - 1;
		}
		if (redownloadErrorTime <= 0) {
			Log.d(TAG, "updateFromPendingToDownloading: error " + errorTask.size());
		}
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
	public synchronized void start() {
		super.start();
		isRunning = true;
	}

	@Override
	public void run() {
		Log.d(TAG, "run: start task manager");
		setUpQueues();
		try {
			while (isRunning) {
				updateFromPendingToDownloading();
				Log.d(TAG, "run taskCompleted: " + downloadingTask.size());
				for (Task task : downloadingTask) {
					Log.d(TAG, "run: " + task.getState());
					if (task.getState() == State.NEW) {
						task.start();
					} else if (task.getState() == State.TERMINATED) {
						taskError(task);
					}
				}
				if (downloadingTask.size() == 0) {
					Log.d(TAG, "run: error " + errorTask.size());
					isRunning = false;
					synchronized (NetworkService.monitor) {
						try {
							NetworkService.monitor.wait();
						} catch (InterruptedException e) {

						}
					}
				} else {
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void addTask(TaskInfo taskInfo) {
		Task task = new Task(taskInfo, this);
		if (checkContain(taskInfo, errorTask)) {
			task = getTaskFromQueue(taskInfo, errorTask);
			Log.d(TAG, "addTask error: " + task.getTaskInfo().processedSize);
			errorTask.remove(task);
			if (downloadingTask.size() >= SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD)) {
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				pendingTask.offer(task);
			} else {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
			}
		} else if (!checkContain(taskInfo, downloadingTask) && !checkContain(taskInfo, pendingTask)) {
			if (downloadingTask.size() >= SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD)) {
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				pendingTask.offer(task);
			} else {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
			}
		}
		Log.d(TAG, "addTask: " + downloadingTask.size());
		Log.d(TAG, "addTask: " + pendingTask.size());
		Log.d(TAG, "addTask: " + errorTask.size());
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

	public Task getTaskFromQueue(TaskInfo taskInfo, BlockingQueue<Task> tasks) {
		for (Task task : tasks) {
			if (task.getTaskInfo().isDownload == taskInfo.isDownload &&
					task.getTaskInfo().name.equalsIgnoreCase(taskInfo.name) &&
					task.getTaskInfo().url.equalsIgnoreCase(taskInfo.url)) {
				return task;
			}
		}
		return null;
	}

	public synchronized void taskCompleted(Task task) {
		Log.d(TAG, "taskCompleted: " + downloadingTask.remove(task));
		Log.d(TAG, "taskCompleted: " + downloadingTask.size());
		task.getTaskInfo().status = ConstantValues.STATUS_COMPLETED;
		DBHelper.getInstance().updateTask(task.getTaskInfo());
		DBHelper.getInstance().deleteSubTaskByTaskId(task.getTaskInfo().taskId);
		updateFromPendingToDownloading();
	}

	public synchronized void taskError(Task task) {
		Log.d(TAG, "taskCompleted: " + downloadingTask.remove(task));
		errorTask.offer(task);
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}
}
