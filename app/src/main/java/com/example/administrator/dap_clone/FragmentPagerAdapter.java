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

/**
 * Created by Administrator on 03/21/2017.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

	private static final String TAG = FragmentPagerAdapter.class.getSimpleName();
	private Context context;
	private final int PAGE_COUNT = 4;
	private int[] imageResId = {
			R.drawable.ic_file_download_white_24dp,
			R.drawable.ic_file_upload_white_24dp,
			R.drawable.ic_folder_white_24dp,
			R.drawable.ic_settings_white_24dp
	};

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
		} else {
			fragment = new FragmentSettings();
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		CharSequence title = "default";
		if (position == 3) {
			title = createCustomTitle("Settings", imageResId[3]);
		}
		return title;
	}

	private CharSequence createCustomTitle(String title, int resId) {
		Drawable image = context.getResources().getDrawable(resId);
		image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
		SpannableString spannableString = new SpannableString(" ");
		ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
		spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}
}
