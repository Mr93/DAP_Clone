package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.dapclone.R;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.fragmentfolder.FolderFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadFragment extends Fragment implements RequiredView {
	private static final String TAG = FolderDownloadFragment.class.getSimpleName();
	private RecyclerView recyclerView;
	private DownloadListAdapter downloadListAdapter;
	private List<TaskInfo> taskInfoList;
	private ProvidedPresenter providedPresenter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskInfoList = new ArrayList<>();
		downloadListAdapter = new DownloadListAdapter(taskInfoList, getContext());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.folder_download_fragment, container, false);
		initRecyclerView(view);
		createTouchCallBack();
		return view;
	}

	private void initRecyclerView(View view) {
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(downloadListAdapter);
	}

	private void createTouchCallBack() {
		ItemTouchHelper.SimpleCallback simpleCallback = createCallBackSwipe();
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
		itemTouchHelper.attachToRecyclerView(recyclerView);
	}

	@NonNull
	private ItemTouchHelper.SimpleCallback createCallBackSwipe() {
		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
				ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

			}
		};
		return simpleCallback;
	}

	@Override
	public void onStart() {
		super.onStart();
		setUpMVP();
		providedPresenter.getDownloadDataFromDB();
	}

	private void setUpMVP() {
		FolderDownloadPresenter presenter = new FolderDownloadPresenter(this);
		ProvidedModel model = new FolderDownloadFragmentModel(presenter);
		presenter.setModel(model);
		providedPresenter = presenter;
	}

	@Override
	public void updateDataRecycleView(ArrayList<TaskInfo> taskList) {
		if (recyclerView != null) {
			this.taskInfoList.clear();
			this.taskInfoList.addAll(taskList);
			downloadListAdapter.notifyDataSetChanged();
			Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
		}
	}
}
