package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.administrator.dapclone.R;
import com.example.administrator.dapclone.TaskInfo;

import java.io.File;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadFragment extends Fragment implements RequiredView {
	private static final String TAG = FolderDownloadFragment.class.getSimpleName();
	private RecyclerView recyclerView;
	private DownloadListAdapter downloadListAdapter;
	private ProvidedPresenter providedPresenter;
	private VideoView videoPreview;
	private MediaController mediaController;
	private ImageView imagePreview;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.folder_download_fragment, container, false);
		videoPreview = (VideoView) view.findViewById(R.id.video_preview);
		mediaController = new MediaController(getActivity());
		videoPreview.setMediaController(mediaController);
		imagePreview = (ImageView) view.findViewById(R.id.image_preview);
		initRecyclerView(view);
		createTouchCallBack();
		return view;
	}

	private void initRecyclerView(View view) {
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
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
	public void onStop() {
		super.onStop();
		providedPresenter.unRegisterBroadcast();
	}

	@Override
	public void fetchDataRecycleView() {
		if (recyclerView != null) {
			downloadListAdapter = new DownloadListAdapter(providedPresenter.getTaskInfoList(), providedPresenter);
			recyclerView.setAdapter(downloadListAdapter);
			downloadListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void updateDataRecycleView() {
		if (recyclerView != null && downloadListAdapter != null) {
			downloadListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void previewVideo(TaskInfo taskInfo) {

		recyclerView.setVisibility(View.GONE);
		videoPreview.setVisibility(View.VISIBLE);
		String path = taskInfo.path;
		videoPreview.setVideoURI(Uri.parse(path));
		videoPreview.setBackgroundDrawable(mediaController.getBackground());
		videoPreview.start();
	}

	@Override
	public void previewMusic(TaskInfo taskInfo) {
		recyclerView.setVisibility(View.GONE);
		videoPreview.setVisibility(View.VISIBLE);
		String path = taskInfo.path;
		videoPreview.setVideoURI(Uri.parse(path));
		videoPreview.start();
	}

	@Override
	public void previewPicture(TaskInfo taskInfo) {
		recyclerView.setVisibility(View.GONE);
		imagePreview.setVisibility(View.VISIBLE);
		File imgFile = new File(taskInfo.path);
		if (imgFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			imagePreview.setImageBitmap(bitmap);
		}
	}


}
