package com.example.administrator.dapclone.Service;

import android.util.Log;

import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.SubTaskInfo;

/**
 * Created by Administrator on 03/27/2017.
 */

public class SubTask extends Thread {
	private static final String TAG = SubTask.class.getSimpleName();
	private Task task;
	private SubTaskInfo subTaskInfo;

	public SubTask(Task task, SubTaskInfo subTaskInfo) {
		this.task = task;
		this.subTaskInfo = subTaskInfo;
		this.subTaskInfo.subTaskId = DBHelper.getInstance().insertSubTask(subTaskInfo, task.getTaskInfo().taskId);
		Log.d(TAG, "SubTask: create id " + this.subTaskInfo.subTaskId);
	}
}
