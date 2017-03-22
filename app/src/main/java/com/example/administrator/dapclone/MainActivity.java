package com.example.administrator.dapclone;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.administrator.dapclone.view.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private ViewPager viewPager;
	private BottomNavigationView bottomNavigationView;
	private static final String POSITION = "POSITION";
	private int[] menuBottomNavigationBarId = {
			R.id.download_bottom_menu,
			R.id.upload_bottom_menu,
			R.id.folder_bottom_menu,
			R.id.setting_bottom_menu
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), this));
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(this);
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		viewPager.addOnPageChangeListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.download_bottom_menu:
				viewPager.setCurrentItem(0);
				break;
			case R.id.upload_bottom_menu:
				viewPager.setCurrentItem(1);
				break;
			case R.id.folder_bottom_menu:
				viewPager.setCurrentItem(2);
				break;
			case R.id.setting_bottom_menu:
				viewPager.setCurrentItem(3);
				break;
			default:
				viewPager.setCurrentItem(0);
				break;
		}
		return true;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		bottomNavigationView.setSelectedItemId(menuBottomNavigationBarId[position]);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
