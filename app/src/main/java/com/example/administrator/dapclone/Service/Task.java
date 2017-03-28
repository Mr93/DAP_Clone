package com.example.administrator.dapclone.Service;

import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.SubTaskInfo;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 03/27/2017.
 */

public class Task extends Thread {
	private static final String TAG = Task.class.getSimpleName();
	private TaskInfo taskInfo;
	private boolean isRunning = false;
	private TaskManager taskManager;
	BlockingQueue<SubTask> downloadingThread, pendingThread, errorThread;
	private static final int MAX_THREAD = 16;
	static final long PART_SIZE = 256000;
	private int redownloadErrorTime = 3;

	public Task(TaskInfo taskInfo, TaskManager taskManager) {
		this.taskInfo = taskInfo;
		this.taskManager = taskManager;
		pendingThread = new LinkedBlockingQueue<>();
		downloadingThread = new LinkedBlockingQueue<>();
		errorThread = new LinkedBlockingQueue<>();
	}

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	@Override
	public void run() {
		super.run();
		updateListThread();
		isRunning = true;
		try {
			while (isRunning) {
				updateDownloadingList();
				for (SubTask task : downloadingThread) {
					if (task.getState() == State.NEW) {
						task.start();
					}
				}
				if (downloadingThread.size() == 0) {
					isRunning = false;
					if (errorThread.size() == 0) {
						taskInfo.status = ConstantValues.STATUS_COMPLETED;
						DBHelper.getInstance().updateTask(taskInfo);
						taskManager.taskCompleted(this);
					} else {
						taskInfo.status = ConstantValues.STATUS_ERROR;
						DBHelper.getInstance().updateTask(taskInfo);
						taskManager.taskError(this);
					}
				} else {
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {

		}
	}

	private void updateListThread() {
		Log.d(TAG, "updateListThread: " + taskInfo.taskId);
		if (taskInfo.taskId == -1) {
			taskInfo.taskId = DBHelper.getInstance().insertTask(taskInfo);
			Log.d(TAG, "updateListThread: " + taskInfo.taskId);
			createNewListSubTask();
		} else {
			ArrayList<SubTaskInfo> listSubTaskInfo = DBHelper.getInstance().getAllSubTask(taskInfo.taskId);
			if (listSubTaskInfo.size() != 0) {
				Log.d(TAG, "updateListThread: " + taskInfo.taskId);

				getOldListSubTask(listSubTaskInfo);
			} else {
				Log.d(TAG, "updateListThread: " + taskInfo.taskId);

				taskInfo.status = ConstantValues.STATUS_PENDING;
				DBHelper.getInstance().updateTask(taskInfo);
				createNewListSubTask();
			}
		}
	}

	private void createNewListSubTask() {
		long temp = 0;
		while (taskInfo.size - temp > 0) {
			long start = temp;
			long end = temp + PART_SIZE - 1;
			if (end > taskInfo.size - 1) {
				end = taskInfo.size - 1;
			}
			temp = end + 1;
			SubTaskInfo subTaskInfo = new SubTaskInfo();
			subTaskInfo.start = start;
			subTaskInfo.end = end;
			subTaskInfo.taskId = taskInfo.taskId;
			SubTask subTask = new SubTask(this, subTaskInfo);
			pendingThread.offer(subTask);
		}
	}


	private void getOldListSubTask(ArrayList<SubTaskInfo> listSubTaskInfo) {
		for (SubTaskInfo subTaskInfo : listSubTaskInfo) {
			if (ConstantValues.STATUS_ERROR.equalsIgnoreCase(subTaskInfo.status)) {
				errorThread.offer(new SubTask(this, subTaskInfo));
			} else if (ConstantValues.STATUS_PENDING.equalsIgnoreCase(subTaskInfo.status)) {
				pendingThread.offer(new SubTask(this, subTaskInfo));
			} else {
				downloadingThread.offer(new SubTask(this, subTaskInfo));
			}
		}
	}


	private void updateDownloadingList() {
		int offset = MAX_THREAD - downloadingThread.size();
		Log.d(TAG, "updateDownloadingList: " + offset);
		Log.d(TAG, "updateDownloadingList: " + downloadingThread.size());
		if (pendingThread.isEmpty() && downloadingThread.isEmpty() && redownloadErrorTime > 0 && !errorThread.isEmpty()) {
			for (SubTask subTask : errorThread) {
				Log.d(TAG, "updateDownloadingList: " + errorThread.remove(subTask));
				subTask.getSubTaskInfo().status = ConstantValues.STATUS_PENDING;
				pendingThread.offer(subTask);
				DBHelper.getInstance().updateSubTask(subTask.getSubTaskInfo(), taskInfo.taskId);
			}
			redownloadErrorTime = redownloadErrorTime - 1;
		}
		for (int i = 0; i < offset; i++) {
			SubTask subTask = pendingThread.poll();
			if (subTask != null) {
				subTask.getSubTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingThread.offer(subTask);
				DBHelper.getInstance().updateSubTask(subTask.getSubTaskInfo(), taskInfo.taskId);
			}
		}
	}

	public synchronized void onThreadDone(SubTask subTask) {
		Log.d(TAG, "onThreadDone: " + downloadingThread.remove(subTask));
		Log.d(TAG, "onThreadDone: " + downloadingThread.size());
		taskInfo.processedSize = taskInfo.processedSize + (int) (subTask.getSubTaskInfo().end - subTask
				.getSubTaskInfo().start + 1);
		Log.d(TAG, "onThreadDone: downloaded size " + (taskInfo.processedSize));
		DBHelper.getInstance().updateTask(taskInfo);
		updateDownloadingList();
	}

	public synchronized void onThreadError(SubTask subTask) {
		Log.d(TAG, "onThreadError: " + downloadingThread.remove(subTask));
		Log.d(TAG, "onThreadDone: " + downloadingThread.size());
		subTask.getSubTaskInfo().status = ConstantValues.STATUS_ERROR;
		errorThread.offer(subTask);
		DBHelper.getInstance().updateSubTask(subTask.getSubTaskInfo(), taskInfo.taskId);
	}

	@Override
	public void interrupt() {
		super.interrupt();
		Log.d(TAG, "interrupt: ");

	}
}
