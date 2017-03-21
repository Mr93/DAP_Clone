package com.example.administrator.dap_clone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;

import com.example.administrator.dap_clone.MVP_FragmentDownload.FragmentDownload;

/**
 * Created by Administrator on 03/21/2017.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

	private static final String TAG = FragmentPagerAdapter.class.getSimpleName();
	private Context context;
	private final int PAGE_COUNT = 4;

	public FragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d(TAG, "getItem: " + position);
		Fragment fragment;
		if (position == 3) {
			fragment = new FragmentSettings();
		} else if (position == 0) {
			fragment = new FragmentDownload();
		} else {
			fragment = new FragmentSettings();
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

}
