package com.example.administrator.dapclone.fragmentfolder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.dapclone.R;
import com.example.administrator.dapclone.fragmentdownload.DownloadFragment;

/**
 * Created by Administrator on 03/28/2017.
 */

public class FolderFragment extends Fragment {
	private static final String TAG = FolderFragment.class.getSimpleName();
	private ViewPager viewPager;
	private TabLayout tabLayout;
	private FolderPagerAdapter folderPagerAdapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_folder, container, false);
		tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText("Download"));
		tabLayout.addTab(tabLayout.newTab().setText("Upload"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager_folder);
		folderPagerAdapter = new FolderPagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
		viewPager.setAdapter(folderPagerAdapter);

		return view;
	}
}
