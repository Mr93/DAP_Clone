package com.example.administrator.dapclone.Service;

import android.util.Log;

import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.SubTaskInfo;
import com.example.administrator.dapclone.TaskInfo;

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
	private static final int MAX_THREAD = 8;
	static final long PART_SIZE = 512000;
	private long numberSubThread = 0;

	public Task(TaskInfo taskInfo, TaskManager taskManager) {
		this.taskInfo = taskInfo;
		this.taskManager = taskManager;
		if (taskInfo.taskId == -1) {
			Log.d(TAG, "Task: create sub task ");
			Log.d(TAG, "Task: " + taskInfo);
			pendingThread = new LinkedBlockingQueue<>();
			long temp = 0;
			taskInfo.taskId = DBHelper.getInstance().insertTask(taskInfo);
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
				SubTask subTask = new SubTask(this, subTaskInfo);
				pendingThread.offer(subTask);
			}
		} else {

		}
	}

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}
}
