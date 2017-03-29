package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.fragmentdownload.IDownloadFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 03/29/2017.
 */

public interface IFolderDownloadFragment {
	interface RequiredView {
		void updateDataRecycleView(ArrayList<TaskInfo> taskList);
	}

	interface ProvidedPresenter {
		void getDownloadDataFromDB();

		void setView(RequiredView view);

		void setModel(ProvidedModel model);
	}

	interface RequiredPresenter {
		void setDownloadData(ArrayList<TaskInfo> taskList);
	}

	interface ProvidedModel {
		void getDownloadDataFromDB();
	}
}
